package com.rhm.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.common.core.enums.ExamListType;
import com.rhm.common.core.enums.ResultCode;
import com.rhm.common.core.utils.ThreadLocalUtil;
import com.rhm.common.security.exception.ServiceException;
import com.rhm.common.security.service.TokenService;
import com.rhm.friend.domain.exam.Exam;
import com.rhm.friend.domain.exam.dto.ExamQueryDTO;
import com.rhm.friend.domain.exam.vo.ExamVO;
import com.rhm.friend.domain.user.UserExam;
import com.rhm.friend.manager.ExamCacheManager;
import com.rhm.friend.mapper.exam.ExamMapper;
import com.rhm.friend.mapper.user.UserExamMapper;
import com.rhm.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserExamServiceImpl implements IUserExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private UserExamMapper userExamMapper;

//    @Autowired
//    private TokenService tokenService;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public int enter(String token, Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        if(exam.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
        // Long userId = tokenService.getUserId(token, secret);
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        UserExam userExam = userExamMapper.selectOne(new LambdaQueryWrapper<UserExam>()
                .eq(UserExam::getExamId, examId)
                .eq(UserExam::getUserId, userId));
        if(userExam != null){
            throw new ServiceException(ResultCode.USER_EXAM_HAS_ENTER);
        }
        examCacheManager.addUserExamCache(userId, examId); // 把数据存入redis ， 注意因为redis中已经有了详细信息，这里只用存 u:e:l:用户id 这个键值对就可以了
        userExam = new UserExam();
        userExam.setUserId(userId);
        userExam.setExamId(examId);
        return userExamMapper.insert(userExam);  // 把数据存入mysql
    }

    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        Long total = examCacheManager.getListSize(ExamListType.USER_EXAM_LIST.getValue(),userId);  // 检查缓存列表是否有数据，有数据直接返回，没有数据从数据库加到缓存在返回
        List<ExamVO> examVOList;
        if(total == null || total <= 0){
            PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
            examVOList = userExamMapper.selectUserExamList(userId);
            examCacheManager.refreshCache(ExamListType.USER_EXAM_LIST.getValue(),userId);
            total = new PageInfo<>(examVOList).getTotal();
        }else {
            // 从redis查询数据
            examQueryDTO.setType(ExamListType.USER_EXAM_LIST.getValue());
            examVOList = examCacheManager.getExamVOList(examQueryDTO,userId);
            total = examCacheManager.getListSize(examQueryDTO.getType(),userId);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();  // 如果在数据库查询了还是空的，那就真的没有数据
        }
        return TableDataInfo.success(examVOList, total);
    }
}
