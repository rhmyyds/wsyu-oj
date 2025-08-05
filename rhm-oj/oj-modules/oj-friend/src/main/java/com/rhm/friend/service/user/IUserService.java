package com.rhm.friend.service.user;

import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.vo.LoginUserVO;
import com.rhm.friend.domain.user.dto.UserDTO;
import com.rhm.friend.domain.user.dto.UserUpdateDTO;
import com.rhm.friend.domain.user.vo.UserVO;

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    UserVO detail();

    int edit(UserUpdateDTO userUpdateDTO);

    int updateHeadImage(String headImage);
}
