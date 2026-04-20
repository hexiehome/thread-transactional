## ADDED Requirements

### Requirement: Present a teaching-first README structure
The repository README SHALL present a learning-oriented structure that includes project value, architecture overview, quick start, scenario walkthroughs, and verification steps.

#### Scenario: New visitor scans README
- **WHEN** a first-time visitor opens the repository README
- **THEN** the document clearly communicates what the project teaches and where to start in under five minutes

### Requirement: Provide runnable quick-start instructions
The README SHALL include step-by-step setup and run instructions that allow users to execute at least one scenario with minimal assumptions.

#### Scenario: User follows quick-start from clean environment
- **WHEN** a user follows the quick-start steps in order
- **THEN** the user can start the service and run a teaching scenario successfully

### Requirement: Map documentation to verification evidence
The README SHALL include explicit links between documented scenarios and the automated tests that verify them.

#### Scenario: User wants to validate a scenario claim
- **WHEN** a user reads a scenario section in README
- **THEN** the section points to the corresponding test command or test case that validates the expected result

### Requirement: Improve open-source credibility signals
The README SHALL include open-source hygiene sections for contribution guidance, roadmap visibility, and license clarity.

#### Scenario: Contributor evaluates repository readiness
- **WHEN** a potential contributor reviews the README
- **THEN** they can identify contribution steps, project direction, and licensing information without inspecting additional files
