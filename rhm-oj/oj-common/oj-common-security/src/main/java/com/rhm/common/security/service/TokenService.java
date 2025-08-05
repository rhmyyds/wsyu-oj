package com.rhm.common.security.service;

import cn.hutool.core.lang.UUID;
import com.rhm.common.core.constants.CacheConstants;
import com.rhm.common.core.constants.JwtConstants;
import com.rhm.common.core.enums.UserIdentity;
import com.rhm.common.redis.service.RedisService;
import com.rhm.common.core.domain.LoginUser;
import com.rhm.common.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenService {
    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     */
    public String createToken(Long userId ,String secret,Integer identity,String nikeName,String headImage) {
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString();

        // 放在令牌中，这两个数据是用于解析的认证的时候要用
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims,secret);
        // key logintoken:唯一标识可以使用用户id或者唯一值  value 存储用户身份

        String tokenKey = getTokenKey(userKey);
        LoginUser loginUser = new LoginUser();
        // loginUser.setIdentity(UserIdentity.ADMIN.getValue());
        loginUser.setIdentity(identity);
        loginUser.setNickName(nikeName);
        loginUser.setHeadImage(headImage);
        // reids 里面存唯一标识key和身份类型value
        redisService.setCacheObject(tokenKey,loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

        return token;
    }

    /**
     * 延长token的有效时间
     * @param
     */
    public void extendToken(Claims claims) {
//        Claims claims;
//        try {
//            claims = JwtUtils.parseToken(token, secret);
//            if (claims == null) {
//                log.error("解析token{},出现异常",token);
//                return;
//            }
//        } catch (Exception e) {
//            log.error("解析token{},出现异常",token,e);
//            return;
//        }
//        String userKey = JwtUtils.getUserKey(claims);
        String userKey = getUserKey(claims);
        if(userKey == null){
            return;
        }
        String tokenKey = getTokenKey(userKey);

        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if(expire != null && expire < CacheConstants.REFRESH_TIME) {
            redisService.expire(tokenKey, CacheConstants.EXP, TimeUnit.MINUTES);
        }
    }

    public LoginUser getLoginUser(String token,String secret) {
        String userKey = getUserKey(token, secret); // 得到redis中储存数据的 key 就是userKey
        if(userKey == null){
            return null;
        }
        return redisService.getCacheObject(getTokenKey(userKey), LoginUser.class); // redis中的数据被序列化了，所以要传类型过去，才能转换为我们需要的对象
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(JwtUtils.getUserId(claims));
    }

    public String getUserKey(String token,String secret) {
        Claims claims = getClaims(token,secret);
        return JwtUtils.getUserKey(claims);
    }

    public String getUserKey(Claims claims){
        return JwtUtils.getUserKey(claims);
    }

    public Claims getClaims(String token,String secret) {
        Claims claims;
        try {
            // 解析出token在登录时存的参数
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                log.error("解析token{},出现异常",token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token{},出现异常",token,e);
            return null;
        }
        return claims;
    }

    public boolean deleteLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret); // 得到redis中储存数据的 key 就是userKey
        if(userKey == null){
            return false;
        }
        return redisService.deleteObject(getTokenKey(userKey));
    }

    public void refreshLoginUser(String nickName, String headImage, String userKey) {
        String tokenKey = getTokenKey(userKey);
        LoginUser loginUser = redisService.getCacheObject(tokenKey, LoginUser.class);
        loginUser.setNickName(nickName);
        loginUser.setHeadImage(headImage);
        redisService.setCacheObject(tokenKey,loginUser);
    }

    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }
}
