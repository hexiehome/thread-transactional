package com.hexiehome.transactional.dao;

import com.hexiehome.transactional.domain.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cmd
 * @data 2020/4/10 19:19
 */
@Repository
public interface SaveDataMapper {
    /**
     * 报错数据
     *
     * @param userInfos
     * @throws Exception sql异常
     */
    void saveDate(@Param("userInfos") List<UserInfo> userInfos) throws Exception;
}
