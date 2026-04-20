package com.hexiehome.transactional.server;

import com.hexiehome.transactional.core.CoordinationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseServer {

    protected void awaitCoordinatorDecision(CoordinationContext context, Exception workerException) throws Exception {
        if (!context.isMultiThread()) {
            if (workerException != null) {
                throw workerException;
            }
            return;
        }
        context.getWorkerCompletionSignal().countDown();
        context.getCoordinatorSignal().await();
        if (context.getRollbackRequired().get()) {
            log.error("runId={} detected rollback decision, raising worker exception", context.getRunId());
            throw workerException == null
                    ? new RuntimeException("rollback required by coordinator decision")
                    : workerException;
        }
    }
}
