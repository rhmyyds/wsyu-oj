package com.rhm.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhm.friend.domain.exam.vo.ExamRankVO;
import com.rhm.friend.domain.exam.vo.ExamVO;
import com.rhm.friend.domain.user.UserExam;

import java.util.List;

public interface UserExamMapper extends BaseMapper<UserExam> {
    List<ExamVO> selectUserExamList(Long userId);

    List<ExamRankVO> selectExamRankList(Long examId);
}
