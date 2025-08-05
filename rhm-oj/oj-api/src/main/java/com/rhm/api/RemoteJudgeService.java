package com.rhm.api;

import com.rhm.api.domain.dto.JudgeSubmitDTO;
import com.rhm.api.domain.vo.UserQuestionResultVO;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "RemoteJudgeService", value = Constants.JUDGE_SERVICE)
public interface RemoteJudgeService {
    @PostMapping("/judge/doJudgeJavaCode")
    R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);
}
