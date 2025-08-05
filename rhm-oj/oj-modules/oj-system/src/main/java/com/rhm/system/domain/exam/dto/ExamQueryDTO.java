package com.rhm.system.domain.exam.dto;

import com.rhm.common.core.domain.PageQueryDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamQueryDTO extends PageQueryDTO {
    private String title;
    private String startTime;
    private String endTime;
}
