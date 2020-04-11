package com.hexiehome.transactional.domain;

import lombok.Data;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author cmd
 * @data 2020/4/10 19:21
 */
@Data
public class TransactionInfo {
    /**
     * 每个线程执行结果
     */
    private BlockingDeque<Boolean> threadResult = new LinkedBlockingDeque<>(30);
    /**
     * 事务管理器
     */
    private RollBack rollBack;
    /**
     * 主线程监控
     */
    private CountDownLatch mainLatch;
    /**
     * 当前子线程监控
     */
    private CountDownLatch threadLatch;

    /**
     * 是否是多线程
     */
    private boolean isMultiThreading = false;
}
