package com.rhm.common.core.domain;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer pageSize = 10;  // 每页多少调数据
    private Integer pageNum = 1;  // 第几页
}
