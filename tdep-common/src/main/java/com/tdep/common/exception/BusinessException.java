package com.tdep.common.exception;

import com.tdep.common.enums.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 平台业务异常，业务层后续只抛出该异常即可获得统一错误响应。
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统一响应码。
     */
    private final ResultCode resultCode;

    /**
     * 使用标准响应码构造业务异常。
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 使用标准响应码和自定义消息构造业务异常。
     *
     * @param resultCode 响应码枚举
     * @param message    自定义异常消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
