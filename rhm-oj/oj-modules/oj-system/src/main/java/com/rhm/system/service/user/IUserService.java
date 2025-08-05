package com.rhm.system.service.user;

import com.rhm.system.domain.user.dto.UserDTO;
import com.rhm.system.domain.user.dto.UserQueryDTO;
import com.rhm.system.domain.user.vo.UserVO;

import java.util.List;

public interface IUserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateStatus(UserDTO userDTO);
}
