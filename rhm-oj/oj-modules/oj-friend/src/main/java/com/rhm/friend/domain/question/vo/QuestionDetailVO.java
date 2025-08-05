package com.rhm.friend.domain.question.vo;

import lombok.Data;

@Data
public class QuestionDetailVO extends QuestionVO{
    private Long timeLimit;
    private Long spaceLimit;
    private String content;
    private String defaultCode;
}
