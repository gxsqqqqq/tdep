package com.tdep.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求参数。
 */
@Data
public class RegisterRequest {

    /**
     * 登录用户名，支持字母、数字和下划线。
     */
    @NotBlank(message = "不能为空")
    @Size(min = 4, max = 32, message = "长度必须在 4 到 32 位之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "只能包含字母、数字和下划线")
    private String username;

    /**
     * 明文密码，服务端入库前统一 BCrypt 加密。
     */
    @NotBlank(message = "不能为空")
    @Size(min = 8, max = 64, message = "长度必须在 8 到 64 位之间")
    private String password;

    /**
     * 用户昵称。
     */
    @Size(max = 64, message = "长度不能超过 64 位")
    private String nickname;

    /**
     * 手机号码。
     */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "格式不正确")
    private String phone;

    /**
     * 邮箱地址。
     */
    @Email(message = "格式不正确")
    @Size(max = 128, message = "长度不能超过 128 位")
    private String email;

    /**
     * 角色编码，如 USER、JUDGE，为空时默认 USER。
     */
    @Size(max = 32, message = "长度不能超过 32 位")
    private String roleCode;
}
