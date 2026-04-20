package com.hexiehome.transactional.domain;

public record ScenarioInfo(
        ScenarioType scenario,
        String purpose,
        String expectedOutcome
) {
}
