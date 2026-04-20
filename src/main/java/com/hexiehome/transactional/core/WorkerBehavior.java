package com.hexiehome.transactional.core;

public record WorkerBehavior(boolean forceFailure, long artificialSleepMs) {
}
