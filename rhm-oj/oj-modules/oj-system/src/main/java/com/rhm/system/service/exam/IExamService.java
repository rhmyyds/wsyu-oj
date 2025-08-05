package com.rhm.system.service.exam;

import com.rhm.system.domain.exam.dto.ExamAddDTO;
import com.rhm.system.domain.exam.dto.ExamEditDTO;
import com.rhm.system.domain.exam.dto.ExamQueryDTO;
import com.rhm.system.domain.exam.dto.ExamQuestionAddDTO;
import com.rhm.system.domain.exam.vo.ExamDetailVO;
import com.rhm.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    String add(ExamAddDTO examAddDTO);

    boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO);

    ExamDetailVO detail(Long examId);

    int edit(ExamEditDTO examEditDTO);

    int questionDelete(Long examId, Long questionId);

    int delete(Long examId);

    int publish(Long examId);

    int cancelPublish(Long examId);
}
