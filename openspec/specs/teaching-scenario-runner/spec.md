# teaching-scenario-runner Specification

## Purpose
TBD - created by archiving change modernize-teaching-thread-transactional. Update Purpose after archive.
## Requirements
### Requirement: Provide deterministic teaching scenarios
The system SHALL provide named runnable scenarios that demonstrate coordinated multi-thread transactional outcomes for educational purposes.

#### Scenario: Run all-success scenario
- **WHEN** a user triggers the all-success scenario
- **THEN** all worker batches complete successfully and the final decision is commit

#### Scenario: Run partial-failure scenario
- **WHEN** a user triggers the partial-failure scenario with at least one failing worker batch
- **THEN** the final decision is rollback and no partial batch is committed

#### Scenario: Run timeout scenario
- **WHEN** a user triggers the timeout scenario and worker completion exceeds the configured coordination timeout
- **THEN** the final decision is rollback due to timeout

### Requirement: Surface scenario intent and expected outcome
The system SHALL expose scenario metadata that includes scenario name, purpose, and expected decision so learners can understand what each run is proving.

#### Scenario: Read scenario catalog
- **WHEN** a user requests the available teaching scenarios
- **THEN** the response includes scenario identifier, description, and expected commit/rollback outcome

### Requirement: Keep scenario execution reproducible
The system SHALL support deterministic scenario controls for batch size, worker count, and failure injection to ensure repeatable demonstrations.

#### Scenario: Execute same scenario twice with same controls
- **WHEN** a user runs the same scenario twice with identical configuration
- **THEN** both runs produce the same final decision type and compatible execution traces

