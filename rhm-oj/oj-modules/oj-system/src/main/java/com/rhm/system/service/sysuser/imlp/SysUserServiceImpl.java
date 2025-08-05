package com.rhm.system.service.sysuser.imlp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rhm.common.core.constants.HttpConstants;
import com.rhm.common.core.domain.LoginUser;
import com.rhm.common.core.domain.R;
import com.rhm.common.core.domain.vo.LoginUserVO;
import com.rhm.common.core.enums.ResultCode;
import com.rhm.common.core.enums.UserIdentity;
import com.rhm.common.security.exception.ServiceException;
import com.rhm.common.security.service.TokenService;
import com.rhm.system.domain.sysuser.SysUser;
import com.rhm.system.domain.sysuser.dto.SysUserSaveDTO;
import com.rhm.system.mapper.sysuser.SysUserMapper;
import com.rhm.system.service.sysuser.ISysUserService;
import com.rhm.system.utils.BCryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RefreshScope
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public R<String> login(String userAccount, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getUserId , SysUser::getPassword,SysUser::getNickName)
                .eq(SysUser::getUserAccount, userAccount));
        if(sysUser == null){
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if(BCryptUtils.matchesPassword(password,sysUser.getPassword())){
            String token = tokenService.createToken(sysUser.getUserId(),secret,UserIdentity.ADMIN.getValue(),sysUser.getNickName(),null);
            log.info("登录成功token为,token:{}",token);
            return R.ok(token);
        }
        return R.fail(ResultCode.FAILED_LOGIN);
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        boolean b = tokenService.deleteLoginUser(token, secret);
        if(b){
            log.info("退出登录成功");
        }
        return b;
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
        List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserAccount,sysUserSaveDTO.getUserAccount()));
        // boolean empty = CollectionUtil.isEmpty(sysUsers);
        if(!CollectionUtil.isEmpty(sysUsers)){
            throw new ServiceException(ResultCode.AILED_USER_EXISTS);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
        // TODO 参数的合法性判断和获取当前用户id
        return sysUserMapper.insert(sysUser);
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
        return R.ok(loginUserVO);
    }


}
