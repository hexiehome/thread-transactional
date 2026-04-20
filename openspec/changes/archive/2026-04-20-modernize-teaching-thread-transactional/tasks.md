## 1. Baseline Modernization

- [x] 1.1 Upgrade project to a modern Java LTS baseline and align Spring Boot to a current compatible major version.
- [x] 1.2 Replace legacy API documentation dependencies with a current OpenAPI integration and remove deprecated tooling.
- [x] 1.3 Update build and runtime configuration so a new contributor can start the project with minimal manual setup.

## 2. Teaching-Oriented Coordination Refactor

- [x] 2.1 Introduce a coordinator-centered transaction orchestration flow with explicit split, dispatch, collect, and decide stages.
- [x] 2.2 Refactor existing transaction synchronization helpers into clearly named components optimized for educational readability.
- [x] 2.3 Add deterministic rollback-cause modeling for worker failure, timeout, and orchestration exceptions.

## 3. Scenario Runner Capability

- [x] 3.1 Implement runnable named scenarios for all-success, partial-failure, and timeout demonstrations.
- [x] 3.2 Add a scenario catalog endpoint or command output that shows purpose and expected outcome for each scenario.
- [x] 3.3 Support controlled scenario inputs (batch size, worker count, failure injection) for repeatable demonstrations.

## 4. Observability and Verification

- [x] 4.1 Add structured stage-based logs with per-run correlation identifiers across scenario execution.
- [x] 4.2 Add automated tests that validate expected decisions for all-success, partial-failure, and timeout scenarios.
- [x] 4.3 Add integration-level verification setup (e.g., containerized database) and document test execution commands.

## 5. README and Open-Source Experience

- [x] 5.1 Rewrite README into a teaching-first structure: value, architecture, quick start, scenarios, and validation path.
- [x] 5.2 Add architecture and flow visualization assets (ASCII or image) mapped to coordinator stages and rollback decisions.
- [x] 5.3 Add open-source hygiene sections (contribution guide entry points, roadmap, and license visibility) to improve repository credibility.
