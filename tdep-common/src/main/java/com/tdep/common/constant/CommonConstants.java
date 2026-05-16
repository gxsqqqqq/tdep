package com.tdep.common.constant;

/**
 * 平台通用常量，集中维护跨模块共享的协议字段和上下文键。
 */
public final class CommonConstants {

    /**
     * 链路追踪编号请求头。
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 日志上下文中的链路追踪编号键。
     */
    public static final String TRACE_ID_MDC_KEY = "traceId";

    /**
     * 网关向下游服务透传的用户编号请求头。
     */
    public static final String USER_ID_HEADER = "X-TDEP-User-Id";

    /**
     * 网关向下游服务透传的用户名请求头。
     */
    public static final String USERNAME_HEADER = "X-TDEP-Username";

    /**
     * 网关向下游服务透传的角色集合请求头。
     */
    public static final String ROLES_HEADER = "X-TDEP-Roles";

    /**
     * 网关向下游服务透传的权限集合请求头。
     */
    public static final String PERMISSIONS_HEADER = "X-TDEP-Permissions";

    private CommonConstants() {
        // 工具类禁止实例化。
    }
}
