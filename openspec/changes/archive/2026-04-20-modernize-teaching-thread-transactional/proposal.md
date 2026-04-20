## Why

The project demonstrates a valuable multi-thread transaction coordination idea, but the codebase and documentation are outdated for current Java and open-source expectations. Modernizing it now will improve teaching readability, reproducibility, and community trust, making the repository easier to learn from and more likely to attract contributors and stars.

## What Changes

- Upgrade the project baseline to a modern Java and Spring Boot stack suitable for educational use in 2026.
- Refactor transaction coordination flow into explicit, teaching-oriented components with clear responsibilities and naming.
- Add scenario-driven demonstration flows (all success, partial failure, timeout rollback) that are easy to run and verify.
- Introduce repeatable automated tests that validate transaction coordination behavior under concurrent execution.
- Redesign README as a teaching guide with architecture visualization, quick-start steps, scenario walkthroughs, and contribution guidance.
- Replace legacy API documentation tooling and stale dependency choices with current ecosystem alternatives.

## Capabilities

### New Capabilities
- `teaching-scenario-runner`: Defines runnable scenarios that demonstrate coordinated commit and rollback behavior in concurrent batch writes.
- `transaction-coordination-observability`: Defines structured visibility for decision outcomes and execution stages so readers can follow transaction flow.
- `project-onboarding-readme`: Defines a comprehensive README experience for setup, architecture understanding, scenario execution, and learning path.

### Modified Capabilities
- None.

## Impact

- Affected code: transaction orchestration service layer, API entrypoint, configuration, dependency management, and test suite.
- Affected docs: root `README.md`, optional supporting docs under `docs/`.
- Affected tooling/dependencies: Java version, Spring Boot version, OpenAPI documentation library, testing stack (including containerized integration testing).
- External behavior: public demo endpoints and startup instructions may change for clarity and consistency.
