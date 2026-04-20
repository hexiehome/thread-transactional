## ADDED Requirements

### Requirement: Emit stage-based coordination logs
The system SHALL emit structured logs for the key stages of each scenario run, including start, worker completion, decision evaluation, and final outcome.

#### Scenario: Observe normal execution stages
- **WHEN** a scenario executes without internal errors
- **THEN** logs include stage entries for scenario start, worker results collection, and final decision

### Requirement: Explain rollback causes explicitly
The system SHALL record a machine-readable rollback cause when rollback occurs, including failure, timeout, or orchestration exception.

#### Scenario: Worker failure triggers rollback
- **WHEN** any worker batch fails during a scenario run
- **THEN** logs and result payload include rollback cause as worker-failure

#### Scenario: Timeout triggers rollback
- **WHEN** scenario coordination exceeds configured timeout
- **THEN** logs and result payload include rollback cause as timeout

### Requirement: Correlate events within one scenario run
The system SHALL assign a correlation identifier per scenario run and include it in all related logs and outputs.

#### Scenario: Trace one run end-to-end
- **WHEN** a user inspects logs for one scenario run
- **THEN** all stage events can be filtered by the same run correlation identifier
