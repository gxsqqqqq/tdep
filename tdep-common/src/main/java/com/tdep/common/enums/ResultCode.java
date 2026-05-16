package com.tdep.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 平台统一响应码，前后端通过该枚举保持错误语义一致。
 */
@Getter
@RequiredArgsConstructor
public enum ResultCode {

    /**
     * 请求处理成功。
     */
    SUCCESS(0, "成功"),

    /**
     * 请求参数格式或语义错误。
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 用户未登录或令牌无效。
     */
    UNAUTHORIZED(401, "未认证或认证已失效"),

    /**
     * 用户已登录但无权访问目标资源。
     */
    FORBIDDEN(403, "无访问权限"),

    /**
     * 请求的资源不存在。
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不被当前接口支持。
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求数据与当前系统状态冲突。
     */
    CONFLICT(409, "资源状态冲突"),

    /**
     * 参数校验失败。
     */
    VALIDATION_ERROR(422, "参数校验失败"),

    /**
     * 服务端处理异常。
     */
    INTERNAL_ERROR(500, "系统内部错误"),

    /**
     * 依赖服务暂时不可用。
     */
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    /**
     * 面向前端和调用方的业务码。
     */
    private final int code;

    /**
     * 面向用户或调用方的默认提示。
     */
    private final String message;
}
