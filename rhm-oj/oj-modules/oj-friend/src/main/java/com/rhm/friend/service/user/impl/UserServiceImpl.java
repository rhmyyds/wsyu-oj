package com.rhm.friend.service.user.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rhm.common.core.constants.CacheConstants;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.constants.HttpConstants;
import com.rhm.common.core.domain.LoginUser;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.vo.LoginUserVO;
import com.rhm.common.core.enums.ResultCode;
import com.rhm.common.core.enums.UserIdentity;
import com.rhm.common.core.enums.UserStatus;
import com.rhm.common.core.utils.ThreadLocalUtil;
import com.rhm.common.redis.service.RedisService;
import com.rhm.common.security.exception.ServiceException;
import com.rhm.common.security.service.TokenService;
import com.rhm.friend.domain.user.User;
import com.rhm.friend.domain.user.dto.UserDTO;
import com.rhm.friend.domain.user.dto.UserUpdateDTO;
import com.rhm.friend.domain.user.vo.UserVO;
import com.rhm.friend.manager.UserCacheManager;
import com.rhm.friend.mapper.user.UserMapper;
import com.rhm.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.send-limit:50}")
    private Integer sendLimit;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;

    @Override
    public boolean sendCode(UserDTO userDTO) {
        if(!checkPhone(userDTO.getPhone())){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        // 检查是否一分钟内频繁获取
        String phoneCodeKey = getPhoneCodeKey(userDTO.getPhone());
        Long expire = redisService.getExpire(phoneCodeKey, TimeUnit.SECONDS);
        if(expire != null && (phoneCodeExpiration * 60 -expire) <60){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        // 限制每天获取验证码的次数
        String codeTimeKey = getCodeTimeKey(userDTO.getPhone());
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit) {
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }
        // 发送验证码并设置验证码过期时间
        String code = RandomUtil.randomNumbers(6);  // 生成验证码
        redisService.setCacheObject(phoneCodeKey,code,phoneCodeExpiration, TimeUnit.MINUTES);
        System.out.println("发送验证码:" + code);    // 模拟发送验证码

        redisService.increment(codeTimeKey);
        if(sendTimes == null){
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
        }

        return true;
    }

    @Override
    public String codeLogin(String phone, String code) {
        checkCode(phone, code);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if(user == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(UserStatus.Normal.getValue());
            userMapper.insert(user);
        }
        return tokenService.createToken(user.getUserId(),secret, UserIdentity.ORDINARY.getValue(),user.getNickName(),user.getHeadImage());
    }

    @Override
    public boolean logout(String token) {
        if(StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)){
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token,secret);
    }

    @Override
    public R<LoginUserVO> info(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if(loginUser == null){
            return R.fail();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        if(StrUtil.isNotEmpty(loginUser.getHeadImage())){
            loginUserVO.setHeadImage(downloadUrl+loginUser.getHeadImage());
        }
        return R.ok(loginUserVO);
    }

    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if(StrUtil.isNotEmpty(userVO.getHeadImage())){
            userVO.setHeadImage(downloadUrl+userVO.getHeadImage());
        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setNickName(userUpdateDTO.getNickName());
        user.setSex(userUpdateDTO.getSex());
        user.setSchoolName(userUpdateDTO.getSchoolName());
        user.setMajorName(userUpdateDTO.getMajorName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setWechat(userUpdateDTO.getWechat());
        user.setIntroduce(userUpdateDTO.getIntroduce());
        //更新用户缓存
        userCacheManager.refreshUser(user);  // 更新用户的详细信息
        tokenService.refreshLoginUser(user.getNickName(), user.getHeadImage(), ThreadLocalUtil.get(Constants.USER_KEY, String.class));  // 更新用户的基础信息
        return userMapper.updateById(user);
    }

    @Override
    public int updateHeadImage(String headImage) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setHeadImage(headImage);
        //更新用户缓存
        userCacheManager.refreshUser(user);  // 更新用户的详细信息
        tokenService.refreshLoginUser(user.getNickName(), user.getHeadImage(), ThreadLocalUtil.get(Constants.USER_KEY, String.class));  // 更新用户的基础信息
        return userMapper.updateById(user);
    }

    private void checkCode(String phone, String code) {
        String phoneCodeKey = getPhoneCodeKey(phone);
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        if(StrUtil.isEmpty(cacheCode)){
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if(!cacheCode.equals(code)){
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        redisService.deleteObject(phoneCodeKey);
    }

    private String getCodeTimeKey(String phone){
        return CacheConstants.CODE_TIME_KEY + phone;
    }

    private String getPhoneCodeKey(String phone){
        return CacheConstants.PHONE_CODE_KEY+phone;
    }

    private static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[23456789][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
}
