package com.rhm.friend.domain.exam.dto;

import com.rhm.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class ExamQueryDTO extends PageQueryDTO {
    private String title;
    private String startTime;
    private String endTime;
    private Integer type;   // 0表示未结束   1表示已结束 2表示
}
