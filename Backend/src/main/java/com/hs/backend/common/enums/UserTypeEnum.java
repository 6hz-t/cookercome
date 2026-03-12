package com.hs.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    CUSTOMER(1, "普通用户"),
    CHEF(2, "厨师");

    private final Integer code;
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static UserTypeEnum getByCode(Integer code) {
        for (UserTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
