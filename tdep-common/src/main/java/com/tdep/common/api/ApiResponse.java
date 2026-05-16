package com.tdep.common.api;

import com.tdep.common.constant.CommonConstants;
import com.tdep.common.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST API 统一返回结构，所有服务对外响应保持相同字段。
 *
 * @param <T> 业务数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台统一业务码，0 表示成功。
     */
    private Integer code;

    /**
     * 响应提示信息。
     */
    private String message;

    /**
     * 响应数据载荷。
     */
    private T data;

    /**
     * 链路追踪编号，便于日志排查。
     */
    private String traceId;

    /**
     * 服务端响应时间。
     */
    private LocalDateTime timestamp;

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 统一成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return of(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 构造无数据成功响应。
     *
     * @return 统一成功响应
     */
    public static ApiResponse<Void> success() {
        return success(null);
    }

    /**
     * 使用标准响应码构造失败响应。
     *
     * @param resultCode 响应码枚举
     * @return 统一失败响应
     */
    public static ApiResponse<Void> fail(ResultCode resultCode) {
        return fail(resultCode, resultCode.getMessage());
    }

    /**
     * 使用标准响应码和自定义消息构造失败响应。
     *
     * @param resultCode 响应码枚举
     * @param message    自定义提示
     * @return 统一失败响应
     */
    public static ApiResponse<Void> fail(ResultCode resultCode, String message) {
        return of(resultCode.getCode(), message, null);
    }

    /**
     * 构造完整统一响应。
     *
     * @param code    响应码
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 统一响应
     */
    public static <T> ApiResponse<T> of(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .traceId(currentTraceId())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 从日志上下文中读取链路编号；缺失时兜底生成，避免响应字段为空。
     *
     * @return 链路追踪编号
     */
    private static String currentTraceId() {
        String traceId = MDC.get(CommonConstants.TRACE_ID_MDC_KEY);
        return StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
    }
}
