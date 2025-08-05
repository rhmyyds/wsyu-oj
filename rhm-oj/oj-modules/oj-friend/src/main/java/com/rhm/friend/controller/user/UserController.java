package com.rhm.friend.controller.user;

import com.rhm.common.core.constants.HttpConstants;
import com.rhm.common.core.controller.BaseController;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.vo.LoginUserVO;
import com.rhm.friend.domain.user.dto.UserDTO;
import com.rhm.friend.domain.user.dto.UserUpdateDTO;
import com.rhm.friend.domain.user.vo.UserVO;
import com.rhm.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        return toR(userService.sendCode(userDTO));
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody UserDTO userDTO){
        return R.ok(userService.codeLogin(userDTO.getPhone(),userDTO.getCode()));
    }

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return toR(userService.logout(token));
    }

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return userService.info(token);
    }

    /**
     * 获取用户详细信息
     * @return
     */
    @GetMapping("/detail")
    public R<UserVO> detail(){
        return R.ok(userService.detail());
    }

    /**
     * 编辑用户信息
     * @param userUpdateDTO
     * @return
     */
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.edit(userUpdateDTO));
    }

    /**
     * 保存头像
     */
    @PutMapping("/head-image/update")
    public R<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO){
        return toR(userService.updateHeadImage(userUpdateDTO.getHeadImage()));
    }
}
