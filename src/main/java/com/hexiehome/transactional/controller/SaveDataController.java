package com.hexiehome.transactional.controller;

import com.hexiehome.transactional.domain.UserInfo;
import com.hexiehome.transactional.server.ThreadTransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author cmd
 * @data 2020/4/10 19:54
 */
@Slf4j
@RestController
@RequestMapping("/user/v1.0")
public class SaveDataController {

    @Autowired
    private ThreadTransactionalService service;


    @GetMapping
    public boolean createUser(Integer size) {
        if (size < 1) {
            log.warn("createUser param is error");
            return false;
        }
        List<UserInfo> userInfos = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setName(UUID.randomUUID().toString());
            userInfo.setSex(i * 4);
            userInfos.add(userInfo);
        }
        service.handleMessage(userInfos);
        return true;
    }
}
