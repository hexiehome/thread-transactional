# Thread Transactional (Teaching Demo)

A teaching-first Spring Boot demo that explains how to coordinate multi-threaded batch writes with a single commit/rollback decision.

## What You Can Learn

- How a coordinator decides `COMMIT` vs `ROLLBACK` after concurrent worker execution.
- Why timeout/failure handling must be explicit in multi-thread transaction workflows.
- How to make concurrency behavior observable through stage-based logs and run IDs.

## Architecture

```text
Client Request
      |
      v
Scenario Runner (ALL_SUCCESS / PARTIAL_FAILURE / TIMEOUT)
      |
      v
Coordinator
  - split batches
  - dispatch workers
  - await completion (timeout aware)
  - decide commit/rollback
      |
      v
Worker Transactions (MyBatis + MySQL)
      |
      v
Structured Logs (runId + stage + decision)
```

## Quick Start

### 1) Start MySQL

```bash
docker compose up -d
```

The table schema is initialized from `docs/sql/init.sql`.

### 2) Configure environment (optional)

Default values are in `src/main/resources/application.properties`.
You can override with environment variables:

- `TX_DB_URL`
- `TX_DB_USERNAME`
- `TX_DB_PASSWORD`

### 3) Run the app

```bash
mvn spring-boot:run
```

OpenAPI UI: [http://localhost:6790/swagger-ui.html](http://localhost:6790/swagger-ui.html)

## Scenario API

### List scenarios

```bash
curl http://localhost:6790/api/v1/scenarios
```

### Run a scenario

```bash
curl -X POST http://localhost:6790/api/v1/scenarios/run \
  -H "Content-Type: application/json" \
  -d "{\"scenario\":\"PARTIAL_FAILURE\",\"size\":10,\"batchSize\":5,\"timeoutSeconds\":2}"
```

Supported scenarios:

- `ALL_SUCCESS` -> expected `COMMIT`
- `PARTIAL_FAILURE` -> expected `ROLLBACK_WORKER_FAILURE`
- `TIMEOUT` -> expected `ROLLBACK_TIMEOUT`

## Verification

Run scenario-focused tests:

```bash
mvn -Dtest=CoordinationDecisionEngineTest,ThreadTransactionalServiceTest test
```

These tests validate:

- all-success leads to `COMMIT`
- worker failure leads to `ROLLBACK_WORKER_FAILURE`
- timeout leads to `ROLLBACK_TIMEOUT`

## Roadmap

- Add one-click script to replay all scenarios with sample outputs.
- Add more edge-case tests (empty batch, high contention, delayed worker failures).
- Add bilingual docs for broader community reach.

## Contributing

See `CONTRIBUTING.md`.

## License

MIT
