package com.rhm.system.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhm.system.domain.user.User;
import com.rhm.system.domain.user.dto.UserQueryDTO;
import com.rhm.system.domain.user.vo.UserVO;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<UserVO> selectUserList(UserQueryDTO userQueryDTO);

}
