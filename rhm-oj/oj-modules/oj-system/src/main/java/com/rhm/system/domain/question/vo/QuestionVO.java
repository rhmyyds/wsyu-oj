package com.rhm.system.domain.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 仅返回非空字段
public class QuestionVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId; // 由于前端number接受不了雪花算法生成的这么大的数字，所以转换成字符串类型
    private String title;
    private Integer difficulty;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String createName;
}
