package com.rhm.judge.service;

import com.rhm.api.domain.dto.JudgeSubmitDTO;
import com.rhm.api.domain.vo.UserQuestionResultVO;

public interface IJudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
