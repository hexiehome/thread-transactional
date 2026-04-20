package com.hexiehome.transactional.server;

import com.hexiehome.transactional.core.CoordinationContext;
import com.hexiehome.transactional.core.CoordinationDecision;
import com.hexiehome.transactional.core.CoordinationDecisionEngine;
import com.hexiehome.transactional.core.WorkerBehavior;
import com.hexiehome.transactional.domain.ScenarioInfo;
import com.hexiehome.transactional.domain.ScenarioRequest;
import com.hexiehome.transactional.domain.ScenarioResult;
import com.hexiehome.transactional.domain.ScenarioType;
import com.hexiehome.transactional.domain.UserInfo;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ThreadTransactionalService {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private static final int DEFAULT_BATCH_SIZE = 5;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private final InsertDataService insertDataService;
    private final CoordinationDecisionEngine decisionEngine = new CoordinationDecisionEngine();

    @Value("${transactional.coordination.timeout-seconds:10}")
    private int defaultTimeoutSeconds;

    public ThreadTransactionalService(InsertDataService insertDataService) {
        this.insertDataService = insertDataService;
    }

    public List<ScenarioInfo> listScenarios() {
        List<ScenarioInfo> infos = new ArrayList<>();
        infos.add(new ScenarioInfo(ScenarioType.ALL_SUCCESS, "All worker batches succeed", "COMMIT"));
        infos.add(new ScenarioInfo(ScenarioType.PARTIAL_FAILURE, "One worker is forced to fail", "ROLLBACK_WORKER_FAILURE"));
        infos.add(new ScenarioInfo(ScenarioType.TIMEOUT, "Workers exceed configured coordination timeout", "ROLLBACK_TIMEOUT"));
        return infos;
    }

    public ScenarioResult runScenario(ScenarioRequest request) {
        ScenarioType scenario = request.scenario() == null ? ScenarioType.ALL_SUCCESS : request.scenario();
        int size = request.size() == null || request.size() < 1 ? DEFAULT_BATCH_SIZE : request.size();
        int batchSize = request.batchSize() == null || request.batchSize() < 1 ? DEFAULT_BATCH_SIZE : request.batchSize();
        int timeoutSeconds = request.timeoutSeconds() == null || request.timeoutSeconds() < 1
                ? defaultTimeoutSeconds
                : request.timeoutSeconds();

        return handleMessage(generateUsers(size), scenario, batchSize, timeoutSeconds);
    }

    public ScenarioResult handleMessage(List<UserInfo> userInfos, ScenarioType scenarioType, int batchSize, int timeoutSeconds) {
        if (CollectionUtils.isEmpty(userInfos)) {
            return new ScenarioResult("n/a", scenarioType, CoordinationDecision.ROLLBACK_ORCHESTRATION_EXCEPTION, 0, 0, "input list is empty");
        }

        String runId = UUID.randomUUID().toString();
        CoordinationContext context = new CoordinationContext();
        context.setRunId(runId);
        context.setCoordinatorSignal(new CountDownLatch(1));

        List<List<UserInfo>> partitions = partition(userInfos, batchSize);
        context.setMultiThread(partitions.size() > 1);
        context.setWorkerCompletionSignal(new CountDownLatch(partitions.size()));

        log.info("stage=scenario-start runId={} scenario={} batches={} timeoutSeconds={}",
                runId, scenarioType, partitions.size(), timeoutSeconds);

        try {
            for (int i = 0; i < partitions.size(); i++) {
                int workerIndex = i;
                List<UserInfo> users = partitions.get(i);
                executor.submit(() -> executeWorker(context, users, scenarioType, workerIndex));
            }

            boolean finished = context.getWorkerCompletionSignal().await(timeoutSeconds, TimeUnit.SECONDS);
            int successCount = drainSuccessCount(context, partitions.size());
            CoordinationDecision decision = decisionEngine.decide(finished, successCount, partitions.size());

            if (decision != CoordinationDecision.COMMIT) {
                context.getRollbackRequired().set(true);
            }
            context.setDecision(decision);

            log.info("stage=decision runId={} decision={} successWorkers={} expectedWorkers={}",
                    runId, decision, successCount, partitions.size());

            return new ScenarioResult(
                    runId,
                    scenarioType,
                    decision,
                    partitions.size(),
                    successCount,
                    "scenario completed with coordinator decision"
            );
        } catch (Exception e) {
            context.getRollbackRequired().set(true);
            context.setDecision(CoordinationDecision.ROLLBACK_ORCHESTRATION_EXCEPTION);
            log.error("stage=orchestration-error runId={} message={}", runId, e.getMessage(), e);
            return new ScenarioResult(
                    runId,
                    scenarioType,
                    CoordinationDecision.ROLLBACK_ORCHESTRATION_EXCEPTION,
                    partitions.size(),
                    0,
                    "orchestration exception: " + e.getMessage()
            );
        } finally {
            context.getCoordinatorSignal().countDown();
            log.info("stage=scenario-end runId={} decision={}", runId, context.getDecision());
        }
    }

    private void executeWorker(CoordinationContext context, List<UserInfo> users, ScenarioType scenarioType, int workerIndex) {
        try {
            WorkerBehavior behavior = behaviorFor(scenarioType, workerIndex);
            log.info("stage=worker-start runId={} worker={} forcedFailure={} sleepMs={}",
                    context.getRunId(), workerIndex, behavior.forceFailure(), behavior.artificialSleepMs());
            insertDataService.saveData(context, users, behavior);
            log.info("stage=worker-success runId={} worker={}", context.getRunId(), workerIndex);
        } catch (Exception e) {
            log.error("stage=worker-failed runId={} worker={} message={}",
                    context.getRunId(), workerIndex, e.getMessage());
        }
    }

    private WorkerBehavior behaviorFor(ScenarioType scenarioType, int workerIndex) {
        if (scenarioType == ScenarioType.PARTIAL_FAILURE && workerIndex == 0) {
            return new WorkerBehavior(true, 0);
        }
        if (scenarioType == ScenarioType.TIMEOUT) {
            return new WorkerBehavior(false, TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds + 2L));
        }
        return new WorkerBehavior(false, 0);
    }

    private int drainSuccessCount(CoordinationContext context, int expectedWorkers) {
        int success = 0;
        for (int i = 0; i < expectedWorkers; i++) {
            Boolean result = context.getWorkerResults().poll();
            if (Boolean.TRUE.equals(result)) {
                success++;
            }
        }
        return success;
    }

    private List<List<UserInfo>> partition(List<UserInfo> users, int batchSize) {
        List<List<UserInfo>> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i += batchSize) {
            int end = Math.min(i + batchSize, users.size());
            result.add(users.subList(i, end));
        }
        return result;
    }

    private List<UserInfo> generateUsers(int size) {
        List<UserInfo> users = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setName(UUID.randomUUID().toString());
            userInfo.setSex(i % 2);
            users.add(userInfo);
        }
        return users;
    }

    @PreDestroy
    void shutdownPool() {
        executor.shutdown();
    }
}
