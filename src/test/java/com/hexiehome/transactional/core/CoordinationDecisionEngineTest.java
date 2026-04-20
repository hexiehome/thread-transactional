package com.hexiehome.transactional.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinationDecisionEngineTest {

    private final CoordinationDecisionEngine engine = new CoordinationDecisionEngine();

    @Test
    void shouldCommitWhenAllWorkersSucceed() {
        CoordinationDecision decision = engine.decide(true, 4, 4);
        assertEquals(CoordinationDecision.COMMIT, decision);
    }

    @Test
    void shouldRollbackWhenAnyWorkerFails() {
        CoordinationDecision decision = engine.decide(true, 3, 4);
        assertEquals(CoordinationDecision.ROLLBACK_WORKER_FAILURE, decision);
    }

    @Test
    void shouldRollbackWhenAwaitTimeout() {
        CoordinationDecision decision = engine.decide(false, 4, 4);
        assertEquals(CoordinationDecision.ROLLBACK_TIMEOUT, decision);
    }
}
