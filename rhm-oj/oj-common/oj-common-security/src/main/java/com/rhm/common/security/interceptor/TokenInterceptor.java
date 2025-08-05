package com.rhm.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.rhm.common.core.constants.Constants;
import com.rhm.common.core.constants.HttpConstants;
import com.rhm.common.core.utils.ThreadLocalUtil;
import com.rhm.common.security.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String token = getToken(request);
        if(StrUtil.isEmpty(token)){
            return true;
        }
        Claims claims = tokenService.getClaims(token,secret);
        Long userId = tokenService.getUserId(claims);
        String userKey = tokenService.getUserKey(claims);
        ThreadLocalUtil.set(Constants.USER_ID,userId);  // 保存用户id
        ThreadLocalUtil.set(Constants.USER_KEY,userKey);  // 保存用户id
        tokenService.extendToken(claims);  // 延长token的有效时间
        return true;
    }

    /**
     * 删除ThreadLocal防止内存泄露
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, "");
        }
        return token;
    }
}
