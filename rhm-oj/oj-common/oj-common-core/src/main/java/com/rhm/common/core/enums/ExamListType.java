package com.rhm.common.core.enums;

import lombok.Getter;

@Getter
public enum ExamListType {
    EXAM_UN_FINISH_LIST( 0), // 没有结束的竞赛

    EXAM_HISTORY_LIST( 1),  // 结束的竞赛
    USER_EXAM_LIST(2);  // 用户报名的竞赛列表

    private final Integer value;

    ExamListType(Integer value) { this.value = value; }
}
