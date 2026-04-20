package com.hexiehome.transactional.domain;

public record ScenarioRequest(
        ScenarioType scenario,
        Integer size,
        Integer batchSize,
        Integer timeoutSeconds
) {
}
