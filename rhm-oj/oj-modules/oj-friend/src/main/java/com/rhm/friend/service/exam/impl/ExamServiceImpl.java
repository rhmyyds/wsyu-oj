package com.rhm.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.domain.TableDataInfo;
import com.rhm.common.core.utils.ThreadLocalUtil;
import com.rhm.friend.domain.exam.dto.ExamQueryDTO;
import com.rhm.friend.domain.exam.dto.ExamRankDTO;
import com.rhm.friend.domain.exam.vo.ExamRankVO;
import com.rhm.friend.domain.exam.vo.ExamVO;
import com.rhm.friend.domain.user.vo.UserVO;
import com.rhm.friend.manager.ExamCacheManager;
import com.rhm.friend.manager.UserCacheManager;
import com.rhm.friend.mapper.exam.ExamMapper;
import com.rhm.friend.mapper.user.UserExamMapper;
import com.rhm.friend.service.exam.IExamService;
import com.rhm.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private UserCacheManager userCacheManager;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper. selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redislist(ExamQueryDTO examQueryDTO) {
        Long total = examCacheManager.getListSize(examQueryDTO.getType(),null);  // 检查缓存列表是否有数据，有数据直接返回，没有数据从数据库加到缓存在返回
        List<ExamVO> examVOList;
        if(total == null || total <= 0){
            examVOList = list(examQueryDTO);  // 查询对应的竞赛列表 (对应指的是未结束和已结束)
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
            total = new PageInfo<>(examVOList).getTotal();
        }else {
            // 从redis查询数据
            examVOList = examCacheManager.getExamVOList(examQueryDTO,null);
            total = examCacheManager.getListSize(examQueryDTO.getType(),null);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();  // 如果在数据库查询了还是空的，那就真的没有数据
        }
        assembleExamVOList(examVOList);
        return TableDataInfo.success(examVOList, total);
    }

    @Override
    public String getFirstQuestion(Long examId) {
        checkAndRefresh(examId);
        return examCacheManager.getFirstQuestion(examId).toString();
    }

    @Override
    public String preQuestion(Long examId, Long questionId) {
        checkAndRefresh(examId);
        return examCacheManager.preQuestion(examId, questionId).toString();
    }

    @Override
    public String nextQuestion(Long examId, Long questionId) {
        checkAndRefresh(examId);
        return examCacheManager.nextQuestion(examId, questionId).toString();
    }

    @Override
    public TableDataInfo rankList(ExamRankDTO examRankDTO) {
        Long total = examCacheManager.getRankListSize(examRankDTO.getExamId());
        List<ExamRankVO> examRankVOList;
        if (total == null || total <= 0) {
            PageHelper.startPage(examRankDTO.getPageNum(), examRankDTO.getPageSize());
            examRankVOList = userExamMapper.selectExamRankList(examRankDTO.getExamId());
            examCacheManager.refreshExamRankCache(examRankDTO.getExamId());
            total = new PageInfo<>(examRankVOList).getTotal();
        } else {
            examRankVOList = examCacheManager.getExamRankList(examRankDTO);
        }
        if (CollectionUtil.isEmpty(examRankVOList)) {
            return TableDataInfo.empty();
        }
        assembleExamRankVOList(examRankVOList);  // 缓存里面的存的是用户id这里要查出用户的名称，所以要改造一下数据
        return TableDataInfo.success(examRankVOList, total);
    }

    /**
     * 检查当前用户是否参加这个竞赛，用于给前端渲染数据
     * @param examVOList
     */
    private void assembleExamVOList(List<ExamVO> examVOList){
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamIdList = examCacheManager.getAllUserExamList(userId);
        if(CollectionUtil.isEmpty(userExamIdList)){
            return;
        }
        for (ExamVO examVO : examVOList) {
            if (userExamIdList.contains(examVO.getExamId())) {
                examVO.setEnter(true);
            }
        }
    }

    private void assembleExamRankVOList(List<ExamRankVO> examRankVOList) {
        if (CollectionUtil.isEmpty(examRankVOList)) {
            return;
        }
        for (ExamRankVO examRankVO : examRankVOList) {
            Long userId = examRankVO.getUserId();
            UserVO user = userCacheManager.getUserById(userId);
            examRankVO.setNickName(user.getNickName());
        }
    }

    private void checkAndRefresh(Long examId) {
        Long listSize = examCacheManager.getExamQuestionListSize(examId);
        if (listSize == null || listSize <= 0) {
            examCacheManager.refreshExamQuestionCache(examId);
        }
    }
}
