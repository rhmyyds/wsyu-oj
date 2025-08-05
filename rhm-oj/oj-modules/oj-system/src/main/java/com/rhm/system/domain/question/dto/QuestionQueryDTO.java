package com.rhm.system.domain.question.dto;

import com.rhm.common.core.domain.PageQueryDTO;
import lombok.Data;

import java.util.Set;

@Data
public class QuestionQueryDTO extends PageQueryDTO {
    private Integer difficulty;
    private String title;
    private String excludeIdStr;  // 拼接好的需要排除的id 字符串
    private Set<Long> excludeIdSet;
}
