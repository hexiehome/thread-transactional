package com.hexiehome.transactional.server;

import com.hexiehome.transactional.core.CoordinationContext;
import com.hexiehome.transactional.core.WorkerBehavior;
import com.hexiehome.transactional.dao.SaveDataMapper;
import com.hexiehome.transactional.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 批量插入数据
 *
 * @author cmd
 * @data 2020/4/10 19:16
 */
@Slf4j
@Service
public class InsertDataService extends BaseServer {
    @Autowired
    private SaveDataMapper saveDataMapper;

    @Transactional(rollbackFor = Exception.class, transactionManager = "transactionalTransactionManager", propagation = Propagation.REQUIRED)
    public void saveData(CoordinationContext context, List<UserInfo> userInfos, WorkerBehavior behavior) throws Exception {
        Exception ex = null;
        try {
            if (behavior.artificialSleepMs() > 0) {
                Thread.sleep(behavior.artificialSleepMs());
            }
            if (behavior.forceFailure()) {
                throw new IllegalStateException("forced failure for teaching scenario");
            }
            saveDataMapper.saveDate(userInfos);
            context.getWorkerResults().put(Boolean.TRUE);
        } catch (Exception e) {
            log.error("saveData error ", e);
            context.getWorkerResults().put(Boolean.FALSE);
            ex = e;
        } finally {
            awaitCoordinatorDecision(context, ex);
        }
    }
}
