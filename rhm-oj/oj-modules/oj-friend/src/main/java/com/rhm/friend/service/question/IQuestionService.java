package com.rhm.friend.service.question;

import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.friend.domain.question.dto.QuestionQueryDTO;
import com.rhm.friend.domain.question.vo.QuestionDetailVO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}
