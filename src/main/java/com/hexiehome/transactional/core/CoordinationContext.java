package com.hexiehome.transactional.core;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class CoordinationContext {

    private final AtomicBoolean rollbackRequired = new AtomicBoolean(false);
    private final BlockingQueue<Boolean> workerResults = new LinkedBlockingQueue<>();
    private CountDownLatch coordinatorSignal;
    private CountDownLatch workerCompletionSignal;
    private boolean multiThread;
    private String runId;
    private CoordinationDecision decision = CoordinationDecision.COMMIT;
}
