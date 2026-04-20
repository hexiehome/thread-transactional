## Context

This project is a historical demo focused on coordinating multi-threaded database writes with all-or-nothing rollback semantics. The core idea remains valuable for teaching transaction behavior, but the implementation and documentation currently reflect older framework assumptions and limited onboarding flow.

The change targets an educational outcome: readers should be able to understand transaction coordination mechanics within minutes, run reproducible scenarios, and verify expected outcomes through tests. The design must therefore optimize for clarity, deterministic demonstrations, and explicit flow visibility rather than maximal abstraction.

Constraints:
- Preserve the core teaching topic (concurrent batch writes with coordinated commit/rollback decisions).
- Keep setup lightweight for open-source users.
- Avoid introducing architecture complexity that obscures transactional mechanics.

## Goals / Non-Goals

**Goals:**
- Establish a modern baseline stack suitable for a 2026 teaching demo.
- Refactor orchestration into clearly named roles (coordinator, worker execution, decision policy).
- Provide deterministic scenario runners for success, failure, and timeout behaviors.
- Add automated verification that maps directly to documented scenarios.
- Redesign README around learning flow: value proposition, architecture, quick start, scenarios, and validation.

**Non-Goals:**
- Building a production-grade distributed transaction framework.
- Solving cross-service consistency (e.g., XA/Saga across multiple bounded contexts).
- Pursuing maximum throughput optimization at the cost of readability.
- Expanding into unrelated feature domains outside transaction coordination education.

## Decisions

1. **Adopt a teaching-first layered structure**
   - Decision: Separate orchestration logic, infrastructure adapters, and scenario definitions into explicit modules/packages.
   - Why: Learners can map concepts to code boundaries quickly.
   - Alternatives considered:
     - Keep current flat service layout: lower migration effort but poorer comprehension.
     - Heavy hexagonal architecture: clean isolation but unnecessary abstraction for this demo.

2. **Use a coordinator-centric execution model**
   - Decision: Introduce a transaction coordination component that owns split/dispatch/collect/decide flow and exposes decision points explicitly.
   - Why: Makes rollback triggers and synchronization semantics visible and testable.
   - Alternatives considered:
     - Leave synchronization spread across service and base class: hard to reason about.
     - Full reactive pipeline: modern but less intuitive for many Java learners in transaction contexts.

3. **Represent demo behavior as named scenarios**
   - Decision: Formalize all-success, partial-failure, and timeout as first-class runnable scenarios.
   - Why: Matches educational storytelling and enables direct mapping between README and tests.
   - Alternatives considered:
     - Single endpoint with ad-hoc parameters only: flexible but weak narrative and reproducibility.

4. **Upgrade ecosystem dependencies for compatibility and trust**
   - Decision: Move to modern Java/Spring baseline and replace legacy API doc tooling with current OpenAPI stack.
   - Why: Reduces breakage risk for new users and aligns with current community standards.
   - Alternatives considered:
     - Keep old baseline for minimal changes: preserves legacy behavior but hurts adoption and credibility.

5. **Make observability part of the learning contract**
   - Decision: Emit consistent structured logs for scenario start, worker result, final decision, and rollback cause.
   - Why: Readers learn faster when internal state transitions are visible.
   - Alternatives considered:
     - Minimal logs only: less noise but lower explainability.

## Risks / Trade-offs

- **[Risk] Behavior drift during modernization** -> **Mitigation:** define scenario-based tests before finalizing refactor and compare old/new outcome expectations.
- **[Risk] Added structure may feel heavier than a tiny demo** -> **Mitigation:** keep class count minimal and each class tied to one teaching concept.
- **[Risk] Environment differences (DB, OS, JDK) reduce reproducibility** -> **Mitigation:** provide containerized local database option and explicit quick-start scripts.
- **[Risk] README grows too long** -> **Mitigation:** prioritize scan-friendly sections and move deep dives into supporting docs.
