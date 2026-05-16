package com.tdep.auth.service;

import com.tdep.auth.dto.LoginRequest;
import com.tdep.auth.dto.RegisterRequest;
import com.tdep.auth.vo.LoginVO;
import com.tdep.auth.vo.UserInfoVO;

/**
 * 认证授权业务服务。
 */
public interface AuthService {

    /**
     * 注册新用户。
     *
     * @param request 注册参数
     * @return 注册后的用户信息
     */
    UserInfoVO register(RegisterRequest request);

    /**
     * 用户登录并签发 JWT。
     *
     * @param request 登录参数
     * @return 登录令牌和用户信息
     */
    LoginVO login(LoginRequest request);

    /**
     * 获取当前登录用户信息。
     *
     * @return 当前用户信息
     */
    UserInfoVO currentUser();

    /**
     * 退出当前登录态。
     *
     * @param token 当前请求携带的 JWT
     */
    void logout(String token);
}
