package com.hexiehome.transactional.server;

import com.google.common.collect.Lists;
import com.hexiehome.transactional.domain.RollBack;
import com.hexiehome.transactional.domain.TransactionInfo;
import com.hexiehome.transactional.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程事务管理器[通过线程通信解决多线程事务提交问题]
 * 注：
 * 1.多线程入库仅支持同一种操作:比如单insert/单update,由于数据库的事务隔离级别,不能对同一条数据同时insert和update,会导致死锁
 * 2.如果非要使用多线程同时insert和update,则需要把事务隔离级别降为READ_COMMITTED,但是会出现先update导致失败问题(注解不存在)
 * 3.MySQL默认事务隔离级别:REPEATABLE_READ
 *
 * @author cmd
 * @data 2020/4/10 19:10
 */
@Slf4j
@Service
public class ThreadTransactionalService {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private final int batchSize = 5;
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    @Autowired
    private InsertDataService insertDataService;

    public void handleMessage(List<UserInfo> userInfos) {
        if (CollectionUtils.isEmpty(userInfos)) {
            log.warn("handleMessage data is null");
            return;
        }
        // 主线程监控
        CountDownLatch mainLatch = new CountDownLatch(1);
        // 子线程监控
        CountDownLatch threadLatch = null;
        // 事务共享管理器
        RollBack rollBack = new RollBack(false);
        BlockingDeque<Boolean> threadResult = new LinkedBlockingDeque<>(THREAD_COUNT);
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setThreadResult(threadResult);
        transactionInfo.setRollBack(rollBack);
        transactionInfo.setMainLatch(mainLatch);

        try {
            if (userInfos.size() < batchSize) {
                insertDataService.saveData(transactionInfo, userInfos, userInfos.size());
            } else {
                // 拆分大小为batchSize一个存储
                transactionInfo.setMultiThreading(true);
                List<List<UserInfo>> arrayList = Lists.partition(userInfos, batchSize);
                threadLatch = new CountDownLatch(arrayList.size());
                transactionInfo.setThreadLatch(threadLatch);
                for (List<UserInfo> infos : arrayList) {
                    // 这里会出现线程池不够的情况
                    executor.submit(new HandleTask(transactionInfo, infos));
                }
            }

            // 判断子线程是否成功
            if (threadLatch != null) {
                // 等待子线程执行完毕
                boolean await = threadLatch.await(10, TimeUnit.SECONDS);
                if (await) {
                    while (threadResult.size() > 0) {
                        final Boolean re = threadResult.take();
                        if (!re) {
                            rollBack.setIsRollBack(true);
                            break;
                        }
                    }
                } else {
                    // 超时 直接回滚
                    rollBack.setIsRollBack(true);
                }
            }
        } catch (Exception e) {
            log.error("handleMessage error ", e);
            // 主线程发生异常 也要回滚
            rollBack.setIsRollBack(true);
        } finally {
            mainLatch.countDown();
        }
    }

    private AtomicInteger type = new AtomicInteger(0);

    private class HandleTask implements Runnable {
        private TransactionInfo transactionInfo;
        private List<UserInfo> userInfos;

        private HandleTask(TransactionInfo transactionInfo, List<UserInfo> userInfos) {
            this.transactionInfo = transactionInfo;
            this.userInfos = userInfos;
        }

        @Override
        public void run() {
            try {
                log.info("HandleTask start");
                insertDataService.saveData(transactionInfo, userInfos, type.incrementAndGet());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
