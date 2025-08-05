package com.rhm.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.enums.ResultCode;
import com.rhm.common.security.exception.ServiceException;
import com.rhm.system.domain.exam.Exam;
import com.rhm.system.domain.exam.ExamQuestion;
import com.rhm.system.domain.exam.dto.ExamAddDTO;
import com.rhm.system.domain.exam.dto.ExamEditDTO;
import com.rhm.system.domain.exam.dto.ExamQueryDTO;
import com.rhm.system.domain.exam.dto.ExamQuestionAddDTO;
import com.rhm.system.domain.exam.vo.ExamDetailVO;
import com.rhm.system.domain.exam.vo.ExamVO;
import com.rhm.system.domain.question.Question;
import com.rhm.system.domain.question.vo.QuestionVO;
import com.rhm.system.manager.ExamCacheManager;
import com.rhm.system.mapper.exam.ExamMapper;
import com.rhm.system.mapper.exam.ExamQuestionMapper;
import com.rhm.system.mapper.question.QuestionMapper;
import com.rhm.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestionMapper,ExamQuestion> implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());  //这句代码的意思是下面的sql语句会自带limit条件
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
        checkExamSaveParams(examAddDTO,null);

        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        examMapper.insert(exam);
        return exam.getExamId().toString();
    }

    @Override
    public boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
        Exam exam = getExam(examQuestionAddDTO.getExamId());
        checkExam(exam);
        if(Constants.TRUE.equals(exam.getStatus())){
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        Set<Long> questionIdSet = examQuestionAddDTO.getQuestionIdSet();
        if(CollectionUtil.isEmpty(questionIdSet)){
            return true;
        }
        List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
        if(CollectionUtil.isEmpty(questionList) || questionList.size() < questionIdSet.size()){
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }
        return saveExamQuestion(examQuestionAddDTO, questionIdSet);
    }

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        if(Constants.TRUE.equals(exam.getStatus())){
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));
    }

    @Override
    public ExamDetailVO detail(Long examId) {
        ExamDetailVO examDetailVO = new ExamDetailVO();
        Exam exam = getExam(examId);
        BeanUtil.copyProperties(exam, examDetailVO);
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if(CollectionUtil.isEmpty(examQuestionList)){
            return examDetailVO;
        }
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();// 竞赛中所有题目的id
        // 需要使用题目id查出 Question --> QuestionVO
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionList, QuestionVO.class);
        examDetailVO.setExamQuestionList(questionVOList);
        return examDetailVO;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
        checkExamSaveParams(examEditDTO, examEditDTO.getExamId());
        Exam exam = getExam(examEditDTO.getExamId());
        if(Constants.TRUE.equals(exam.getStatus())){
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        checkExam(exam);
        exam.setTitle(examEditDTO.getTitle());
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        return examMapper.updateById(exam);
    }

    @Override
    public int delete(Long examId) {
        Exam exam = getExam(examId);
        if(Constants.TRUE.equals(exam.getStatus())){
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        checkExam(exam);
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));
        return examMapper.deleteById(examId);
    }

    @Override
    public int publish(Long examId) {
        Exam exam = getExam(examId);
        if(exam.getEndTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_IS_FINISH);
        }
        Long count = examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));
        if(count == null || count <= 0){
            throw new ServiceException(ResultCode.EXAM_NOT_HAS_QUESTION);
        }
        exam.setStatus(Constants.TRUE);

        examCacheManager.addCache(exam);

        return examMapper.updateById(exam);
    }

    @Override
    public int cancelPublish(Long examId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        if(exam.getEndTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_IS_FINISH);
        }
        exam.setStatus(Constants.FALSE);
        examCacheManager.deleteCache(examId);
        return examMapper.updateById(exam);
    }

    // 检查竞赛名字是否重复和时间是否合理
    private void checkExamSaveParams(ExamAddDTO examSaveDTO,Long examId) {
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examSaveDTO.getTitle())
                .ne(examId != null ,Exam::getExamId, examId));
        if(CollectionUtil.isNotEmpty(examList)){
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        if (examSaveDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
    }

    // 编辑竞赛的时候检查是否开始,开始则抛出异常
    private void checkExam(Exam exam){
        if(exam.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }

    private boolean saveExamQuestion(ExamQuestionAddDTO examQuestionAddDTO, Set<Long> questionIdSet) {
        int num = 1; // TODO 有问题
        List<ExamQuestion> examQuestionList = new ArrayList<>();
        for(Long questionId : questionIdSet){
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setQuestionId(questionId);
            examQuestion.setExamId(examQuestionAddDTO.getExamId());
            examQuestion.setQuestionOrder(num++);
            examQuestionList.add(examQuestion);
        }
        // 查看这些 ExamQuestion 是否都存在
        return saveBatch(examQuestionList);   // 这 mybatis-plus 的拓展方法
    }

    // 查询竞赛
    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if(exam == null){
            throw new ServiceException(ResultCode.EXAM_NOT_EXISTS);
        }
        return exam;
    }
}
