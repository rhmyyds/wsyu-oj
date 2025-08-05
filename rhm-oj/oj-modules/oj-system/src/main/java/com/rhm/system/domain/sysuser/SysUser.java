package com.rhm.system.domain.sysuser;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rhm.common.core.domain.BaseEntity;
import lombok.Data;

@Data
//@Getter
//@Setter
@TableName("tb_sys_user")
public class  SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    private String userAccount;
    private String password;
    private String nickName;
}
