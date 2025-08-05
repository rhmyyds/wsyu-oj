package com.rhm.system.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhm.system.domain.question.Question;
import com.rhm.system.domain.question.dto.QuestionQueryDTO;
import com.rhm.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
