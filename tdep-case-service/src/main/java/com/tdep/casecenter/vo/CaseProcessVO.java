package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案件流程记录响应视图。
 */
@Data
@Builder
public class CaseProcessVO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 原状态。
     */
    private String fromStatus;

    /**
     * 目标状态。
     */
    private String toStatus;

    /**
     * 流程动作。
     */
    private String action;

    /**
     * 操作人编号。
     */
    private Long operatorId;

    /**
     * 操作角色。
     */
    private String operatorRole;

    /**
     * 处理意见。
     */
    private String comment;

    /**
     * 处理时间。
     */
    private LocalDateTime processTime;
}
