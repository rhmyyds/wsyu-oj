package com.rhm.friend.controller.user;

import com.rhm.common.core.constants.HttpConstants;
import com.rhm.common.core.controller.BaseController;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.friend.aspect.CheckUserStatus;
import com.rhm.friend.domain.exam.dto.ExamDTO;
import com.rhm.friend.domain.exam.dto.ExamQueryDTO;
import com.rhm.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {

    @Autowired
    private IUserExamService userExamService;

    /**
     * 竞赛报名
     */
    @CheckUserStatus
    @PostMapping("/enter")
    public R<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO){
        return toR(userExamService.enter(token,examDTO.getExamId()));
    }

    /**
     * 用户查询参加自己参加的竞赛
     * @param examQueryDTO
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return userExamService.list(examQueryDTO);
    }
}
