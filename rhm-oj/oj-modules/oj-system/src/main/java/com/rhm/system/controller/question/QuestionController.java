package com.rhm.system.controller.question;

import com.rhm.common.core.controller.BaseController;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.system.domain.question.dto.QuestionAddDTO;
import com.rhm.system.domain.question.dto.QuestionEditDTO;
import com.rhm.system.domain.question.dto.QuestionQueryDTO;
import com.rhm.system.domain.question.vo.QuestionDetailVO;
import com.rhm.system.domain.question.vo.QuestionVO;
import com.rhm.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
        List<QuestionVO> questionVOList = questionService.list(questionQueryDTO);
        return getTableDataInfo(questionVOList);
    }

    @PostMapping("/add")
    public R<Void> add(@RequestBody QuestionAddDTO questionAddDTO){
        return toR(questionService.add(questionAddDTO));
    }

    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId){
        return R.ok(questionService.detail(questionId));
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody QuestionEditDTO questionEditDTO){
        return toR(questionService.edit(questionEditDTO));
    }

    @DeleteMapping("/delete")
    public R<Void> delete(Long questionId){
        return toR(questionService.delete(questionId));
    }
}
