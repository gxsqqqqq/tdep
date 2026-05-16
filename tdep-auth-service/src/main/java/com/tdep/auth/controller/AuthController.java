package com.tdep.auth.controller;

import com.tdep.auth.dto.LoginRequest;
import com.tdep.auth.dto.RegisterRequest;
import com.tdep.auth.service.AuthService;
import com.tdep.auth.vo.LoginVO;
import com.tdep.auth.vo.UserInfoVO;
import com.tdep.common.api.Result;
import com.tdep.common.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证授权接口控制器，只负责请求接收、参数校验和统一响应封装。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {

    private final AuthService authService;

    private final JwtTokenService jwtTokenService;

    @Value("${tdep.security.jwt.header:Authorization}")
    private String jwtHeader;

    /**
     * 用户注册接口。
     *
     * @param request 注册参数
     * @return 注册后的用户信息
     */
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    /**
     * 用户登录接口。
     *
     * @param request 登录参数
     * @return 登录 Token 和用户信息
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 获取当前用户信息接口，需要具备用户信息读取权限。
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('auth:user:read')")
    public Result<UserInfoVO> me() {
        return Result.success(authService.currentUser());
    }

    /**
     * 退出登录接口。
     *
     * @param request 当前 HTTP 请求
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = jwtTokenService.resolveBearerToken(request.getHeader(jwtHeader));
        authService.logout(token);
        return Result.success();
    }
}
