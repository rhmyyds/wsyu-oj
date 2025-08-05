package com.rhm.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhm.system.domain.exam.Exam;
import com.rhm.system.domain.exam.dto.ExamQueryDTO;
import com.rhm.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamMapper extends BaseMapper<Exam> {

    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
