package com.rhm.friend.service.exam;

import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.friend.domain.exam.dto.ExamQueryDTO;
import com.rhm.friend.domain.exam.dto.ExamRankDTO;
import com.rhm.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redislist(ExamQueryDTO examQueryDTO);

    String getFirstQuestion(Long examId);

    String preQuestion(Long examId, Long questionId);

    String nextQuestion(Long examId, Long questionId);

    TableDataInfo rankList(ExamRankDTO examRankDTO);
}
