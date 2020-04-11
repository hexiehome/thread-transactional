package com.hexiehome.transactional.server;

import com.hexiehome.transactional.dao.SaveDataMapper;
import com.hexiehome.transactional.domain.TransactionInfo;
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
    public void saveData(TransactionInfo transactionInfo, List<UserInfo> userInfos, Integer type) throws Exception {
        Exception ex = null;
        try {
            saveDataMapper.saveDate(userInfos);
            transactionInfo.getThreadResult().put(Boolean.TRUE);
            if (type % 3 == 0) {
//                System.out.println(1 / 0);
            }
        } catch (Exception e) {
            log.error("saveData error ", e);
            transactionInfo.getThreadResult().put(Boolean.FALSE);
            ex = e;
        } finally {
            // 执行完成 等待主线程通知回滚
            handleException(transactionInfo, ex);
        }
    }
}
