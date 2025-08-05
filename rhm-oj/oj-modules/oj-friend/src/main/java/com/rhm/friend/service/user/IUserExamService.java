package com.rhm.friend.service.user;

import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.friend.domain.exam.dto.ExamDTO;
import com.rhm.friend.domain.exam.dto.ExamQueryDTO;

public interface IUserExamService {
    int enter(String token, Long examId);

    TableDataInfo list(ExamQueryDTO examQueryDTO);
}
