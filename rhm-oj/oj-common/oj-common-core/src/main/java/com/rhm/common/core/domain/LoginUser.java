package com.rhm.common.core.domain;

import lombok.Data;

/**
 * 登录用户的信息，也是redis中存储的用户信息
 */
@Data
public class LoginUser {
    private String nickName;
    private Integer identity;
    private String headImage;
}
