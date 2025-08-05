package com.rhm.system.controller.user;

import com.rhm.common.core.controller.BaseController;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.system.domain.user.dto.UserDTO;
import com.rhm.system.domain.user.dto.UserQueryDTO;
import com.rhm.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO){
        return getTableDataInfo(userService.list(userQueryDTO));
    }

    @PutMapping("/updateStatus")
    public R<Void> update(@RequestBody UserDTO userDTO){
        return toR(userService.updateStatus(userDTO));
    }
}
