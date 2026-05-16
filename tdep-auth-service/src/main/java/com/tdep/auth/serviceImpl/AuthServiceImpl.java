package com.tdep.auth.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.auth.dto.LoginRequest;
import com.tdep.auth.dto.RegisterRequest;
import com.tdep.auth.entity.SysRole;
import com.tdep.auth.entity.SysUser;
import com.tdep.auth.entity.SysUserRole;
import com.tdep.auth.mapper.SysRoleMapper;
import com.tdep.auth.mapper.SysUserMapper;
import com.tdep.auth.mapper.SysUserRoleMapper;
import com.tdep.auth.service.AuthService;
import com.tdep.auth.service.RbacService;
import com.tdep.auth.service.TokenCacheService;
import com.tdep.auth.vo.LoginVO;
import com.tdep.auth.vo.UserInfoVO;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.SecurityProperties;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.common.security.jwt.JwtClaims;
import com.tdep.common.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证授权业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int ENABLED = 1;

    private static final String DEFAULT_ROLE_CODE = "USER";

    private final SysUserMapper sysUserMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final RbacService rbacService;

    private final JwtTokenService jwtTokenService;

    private final TokenCacheService tokenCacheService;

    private final SecurityProperties securityProperties;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(RegisterRequest request) {
        assertUsernameAvailable(request.getUsername());

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(ENABLED);
        sysUserMapper.insert(user);

        bindDefaultRole(user.getId());
        return buildUserInfo(user, rbacService.listRoleCodesByUserId(user.getId()), rbacService.listPermissionCodesByUserId(user.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginRequest request) {
        SysUser user = selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (!Integer.valueOf(ENABLED).equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        List<String> roles = rbacService.listRoleCodesByUserId(user.getId());
        List<String> permissions = rbacService.listPermissionCodesByUserId(user.getId());
        JwtClaims claims = JwtClaims.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .permissions(permissions)
                .build();
        String token = jwtTokenService.generateAccessToken(claims);
        long expiresIn = securityProperties.getJwt().getAccessTokenSeconds();
        tokenCacheService.cacheToken(token, claims, expiresIn);

        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        return LoginVO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(buildUserInfo(user, roles, permissions))
                .build();
    }

    @Override
    public UserInfoVO currentUser() {
        TdepUserPrincipal principal = SecurityContextUtil.currentUser()
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED));
        SysUser user = sysUserMapper.selectById(principal.getUserId());
        if (user == null || !Integer.valueOf(ENABLED).equals(user.getStatus())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前用户不存在或已禁用");
        }
        return buildUserInfo(user, rbacService.listRoleCodesByUserId(user.getId()), rbacService.listPermissionCodesByUserId(user.getId()));
    }

    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        JwtClaims claims = jwtTokenService.parseAccessToken(token);
        tokenCacheService.removeToken(token, claims);
    }

    /**
     * 校验用户名是否可注册。
     *
     * @param username 登录用户名
     */
    private void assertUsernameAvailable(String username) {
        if (selectByUsername(username) != null) {
            throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
        }
    }

    /**
     * 根据用户名查询用户。
     *
     * @param username 登录用户名
     * @return 用户实体
     */
    private SysUser selectByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
    }

    /**
     * 为新用户绑定默认角色。
     *
     * @param userId 用户主键
     */
    private void bindDefaultRole(Long userId) {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, DEFAULT_ROLE_CODE)
                .eq(SysRole::getStatus, ENABLED)
                .last("LIMIT 1"));
        if (role == null) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "默认角色未初始化");
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        sysUserRoleMapper.insert(userRole);
    }

    /**
     * 组装用户信息响应。
     *
     * @param user        用户实体
     * @param roles       角色编码集合
     * @param permissions 权限编码集合
     * @return 用户信息响应
     */
    private UserInfoVO buildUserInfo(SysUser user, List<String> roles, List<String> permissions) {
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .roles(roles)
                .permissions(permissions)
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
