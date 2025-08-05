package com.rhm.common.core.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    Normal(1),
    Block(0);
    private Integer value;
    UserStatus(Integer value) {
        this.value = value;
    }
}
