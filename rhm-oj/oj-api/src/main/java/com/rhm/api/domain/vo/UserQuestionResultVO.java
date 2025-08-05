package com.rhm.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rhm.api.domain.UserExeResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserQuestionResultVO {
    //是够通过标识
    private Integer pass; // 0  未通过  1 通过

    private String exeMessage; //异常信息

    private List<UserExeResult> userExeResultList;  // 代码的输入/输出/要求输出

    @JsonIgnore
    private Integer score;
}
