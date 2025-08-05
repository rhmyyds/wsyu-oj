package com.rhm.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExamAddDTO {
    private String title;
    // 加上之后前端传过来的的字符串类型就会转换成java的 LocalDateTime 类型
    // 后端传给前端的startTime可以从 LocalDateTime 类型自动转换成 String 类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
