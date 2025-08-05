package com.rhm.system.service.question;

import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.system.domain.question.dto.QuestionAddDTO;
import com.rhm.system.domain.question.dto.QuestionEditDTO;
import com.rhm.system.domain.question.dto.QuestionQueryDTO;
import com.rhm.system.domain.question.vo.QuestionDetailVO;
import com.rhm.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {

    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    boolean add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(Long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int delete(Long questionId);
}
