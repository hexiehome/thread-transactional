package com.hexiehome.transactional.domain;

import com.hexiehome.transactional.core.CoordinationDecision;

public record ScenarioResult(
        String runId,
        ScenarioType scenario,
        CoordinationDecision decision,
        int expectedWorkers,
        int successWorkers,
        String message
) {
}
