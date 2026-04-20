package com.hexiehome.transactional.core;

public class CoordinationDecisionEngine {

    public CoordinationDecision decide(boolean awaitFinished, int successCount, int expectedWorkers) {
        if (!awaitFinished) {
            return CoordinationDecision.ROLLBACK_TIMEOUT;
        }
        if (successCount != expectedWorkers) {
            return CoordinationDecision.ROLLBACK_WORKER_FAILURE;
        }
        return CoordinationDecision.COMMIT;
    }
}
