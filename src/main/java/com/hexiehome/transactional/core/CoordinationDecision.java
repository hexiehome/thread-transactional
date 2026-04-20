package com.hexiehome.transactional.core;

public enum CoordinationDecision {
    COMMIT,
    ROLLBACK_WORKER_FAILURE,
    ROLLBACK_TIMEOUT,
    ROLLBACK_ORCHESTRATION_EXCEPTION
}
