package com.hs.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 厨师等级枚举
 */
@Getter
@AllArgsConstructor
public enum ChefLevelEnum {

    JUNIOR(1, "初级厨师"),
    INTERMEDIATE(2, "中级厨师"),
    SENIOR(3, "高级厨师"),
    MASTER(4, "名厨");

    private final Integer code;
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static ChefLevelEnum getByCode(Integer code) {
        for (ChefLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
