package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据操作日志响应视图。
 */
@Data
@Builder
public class EvidenceOperationLogVO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 操作类型。
     */
    private String operationType;

    /**
     * 操作人编号。
     */
    private Long operatorId;

    /**
     * 操作角色。
     */
    private String operatorRole;

    /**
     * 请求 IP。
     */
    private String requestIp;

    /**
     * 操作结果。
     */
    private String operationResult;

    /**
     * 操作详情。
     */
    private String operationDetail;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;
}
