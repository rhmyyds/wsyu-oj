package com.rhm.friend.service.user;

import com.rhm.api.domain.vo.UserQuestionResultVO;
import com.rhm.common.core.domain.R;
import com.rhm.friend.domain.user.dto.UserSubmitDTO;

public interface IUserQuestionService {
    R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);

    boolean rabbitSubmit(UserSubmitDTO submitDTO);

    UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime);
}
