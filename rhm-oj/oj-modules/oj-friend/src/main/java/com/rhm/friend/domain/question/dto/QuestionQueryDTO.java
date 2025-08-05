package com.rhm.friend.domain.question.dto;

import com.rhm.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class QuestionQueryDTO extends PageQueryDTO {
    private String keyword;
    private Integer difficulty;

}
