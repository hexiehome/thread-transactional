package com.hexiehome.transactional.server;

import com.hexiehome.transactional.core.CoordinationContext;
import com.hexiehome.transactional.core.CoordinationDecision;
import com.hexiehome.transactional.core.WorkerBehavior;
import com.hexiehome.transactional.domain.ScenarioRequest;
import com.hexiehome.transactional.domain.ScenarioResult;
import com.hexiehome.transactional.domain.ScenarioType;
import com.hexiehome.transactional.domain.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadTransactionalServiceTest {

    @Test
    void shouldCommitForAllSuccessScenario() {
        ThreadTransactionalService service = buildService();
        ScenarioResult result = service.runScenario(new ScenarioRequest(ScenarioType.ALL_SUCCESS, 10, 5, 2));
        assertEquals(CoordinationDecision.COMMIT, result.decision());
    }

    @Test
    void shouldRollbackForPartialFailureScenario() {
        ThreadTransactionalService service = buildService();
        ScenarioResult result = service.runScenario(new ScenarioRequest(ScenarioType.PARTIAL_FAILURE, 10, 5, 2));
        assertEquals(CoordinationDecision.ROLLBACK_WORKER_FAILURE, result.decision());
    }

    @Test
    void shouldRollbackForTimeoutScenario() {
        ThreadTransactionalService service = buildService();
        ScenarioResult result = service.runScenario(new ScenarioRequest(ScenarioType.TIMEOUT, 10, 5, 1));
        assertEquals(CoordinationDecision.ROLLBACK_TIMEOUT, result.decision());
    }

    private ThreadTransactionalService buildService() {
        ThreadTransactionalService service = new ThreadTransactionalService(new InMemoryInsertDataService());
        ReflectionTestUtils.setField(service, "defaultTimeoutSeconds", 1);
        return service;
    }

    private static class InMemoryInsertDataService extends InsertDataService {
        @Override
        public void saveData(CoordinationContext context, List<UserInfo> userInfos, WorkerBehavior behavior) throws Exception {
            if (behavior.artificialSleepMs() > 0) {
                Thread.sleep(behavior.artificialSleepMs());
            }
            context.getWorkerResults().put(!behavior.forceFailure());
            context.getWorkerCompletionSignal().countDown();
            context.getCoordinatorSignal().await();
        }
    }
}
