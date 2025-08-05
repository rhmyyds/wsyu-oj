package com.rhm.system.domain.user.dto;

import com.rhm.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class UserQueryDTO extends PageQueryDTO {
    private Long userId;
    private String nickName;
}
