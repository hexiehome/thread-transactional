package com.hexiehome.transactional.domain;

import lombok.Data;

/**
 * 事务控制
 *
 * @author CMD
 */
@Data
public class RollBack {
    private Boolean isRollBack;

    public RollBack(Boolean isRollBack) {
        this.isRollBack = isRollBack;
    }
}