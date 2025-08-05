package com.rhm.friend.controller.user;

import com.rhm.api.domain.vo.UserQuestionResultVO;
import com.rhm.common.core.controller.BaseController;
import com.rhm.common.core.domain.R;
import com.rhm.friend.domain.user.dto.UserSubmitDTO;
import com.rhm.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController {

    @Autowired
    private IUserQuestionService userQuestionService;

    //用户代码提交   请求方法  地址  参数  响应数据结构
    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.submit(submitDTO);
    }

    @PostMapping("/rabbit/submit")
    public R<Void> rabbitSubmit(@RequestBody UserSubmitDTO submitDTO) {
        return toR(userQuestionService.rabbitSubmit(submitDTO));
    }

    /**
     * 传当前时间是为了，为了拿到最新的判题结果，避免拿到之前的判题结果
     * @param examId
     * @param questionId
     * @param currentTime
     * @return
     */
    @GetMapping("/exe/result")
    public  R<UserQuestionResultVO> exeResult(Long examId, Long questionId, String currentTime) {
        return R.ok(userQuestionService.exeResult(examId, questionId, currentTime));
    }
}
