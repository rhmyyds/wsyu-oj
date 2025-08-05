package com.rhm.system.service.sysuser;

import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.vo.LoginUserVO;
import com.rhm.system.domain.sysuser.dto.SysUserSaveDTO;

public interface ISysUserService {

    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(String token);

    boolean logout(String token);
}
