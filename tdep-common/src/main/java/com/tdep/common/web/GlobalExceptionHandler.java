package com.tdep.common.web;

import com.tdep.common.api.ApiResponse;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，将框架异常和业务异常统一转换为标准响应。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理平台业务异常。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        log.warn("业务异常：{}", exception.getMessage());
        ResultCode resultCode = exception.getResultCode();
        return ResponseEntity.status(resolveHttpStatus(resultCode))
                .body(ApiResponse.fail(resultCode, exception.getMessage()));
    }

    /**
     * 处理请求体参数校验异常。
     *
     * @param exception 参数校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.fail(ResultCode.VALIDATION_ERROR, message));
    }

    /**
     * 处理路径变量或查询参数校验异常。
     *
     * @param exception 参数校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.fail(ResultCode.VALIDATION_ERROR, exception.getMessage()));
    }

    /**
     * 处理缺少必填请求参数异常。
     *
     * @param exception 缺参异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        String message = "缺少必要请求参数：" + exception.getParameterName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ResultCode.BAD_REQUEST, message));
    }

    /**
     * 处理参数类型转换异常。
     *
     * @param exception 类型转换异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = "请求参数类型错误：" + exception.getName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ResultCode.BAD_REQUEST, message));
    }

    /**
     * 处理不支持的 HTTP 方法。
     *
     * @param exception 方法不支持异常
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(ResultCode.METHOD_NOT_ALLOWED, exception.getMessage()));
    }

    /**
     * 处理认证异常。
     *
     * @param exception 认证异常
     * @return 统一错误响应
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(ResultCode.UNAUTHORIZED, exception.getMessage()));
    }

    /**
     * 处理授权异常。
     *
     * @param exception 授权异常
     * @return 统一错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(ResultCode.FORBIDDEN, exception.getMessage()));
    }

    /**
     * 处理未预期异常，隐藏内部细节并保留服务端日志。
     *
     * @param exception 未预期异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("系统内部异常", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ResultCode.INTERNAL_ERROR));
    }

    /**
     * 格式化字段校验错误，便于前端直接展示。
     *
     * @param fieldError 字段错误
     * @return 错误描述
     */
    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }

    /**
     * 将业务响应码映射为 HTTP 状态码。
     *
     * @param resultCode 业务响应码
     * @return HTTP 状态码
     */
    private HttpStatus resolveHttpStatus(ResultCode resultCode) {
        return switch (resultCode) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case CONFLICT -> HttpStatus.CONFLICT;
            case VALIDATION_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
