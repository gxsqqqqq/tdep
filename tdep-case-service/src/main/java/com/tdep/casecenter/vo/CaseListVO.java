package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 案件列表响应视图。
 */
@Data
@Builder
public class CaseListVO {

    /**
     * 案件主键。
     */
    private Long id;

    /**
     * 案号。
     */
    private String caseNo;

    /**
     * 案件标题。
     */
    private String caseTitle;

    /**
     * 案件类型。
     */
    private String caseType;

    /**
     * 案由。
     */
    private String causeAction;

    /**
     * 标的金额。
     */
    private BigDecimal claimAmount;

    /**
     * 案件状态。
     */
    private String status;

    /**
     * 承办法官编号。
     */
    private Long judgeId;

    /**
     * 创建人编号。
     */
    private Long createdBy;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;
}
