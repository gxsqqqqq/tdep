package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件详情响应视图。
 */
@Data
@Builder
public class CaseDetailVO {

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
     * 程序类型。
     */
    private String procedureType;

    /**
     * 诉讼请求。
     */
    private String claimRequest;

    /**
     * 标的金额。
     */
    private BigDecimal claimAmount;

    /**
     * 受理法院。
     */
    private String courtName;

    /**
     * 案件状态。
     */
    private String status;

    /**
     * 承办法官编号。
     */
    private Long judgeId;

    /**
     * 书记员编号。
     */
    private Long clerkId;

    /**
     * 立案提交时间。
     */
    private LocalDateTime filingTime;

    /**
     * 受理时间。
     */
    private LocalDateTime acceptedTime;

    /**
     * 缴费截止时间。
     */
    private LocalDateTime paymentDeadline;

    /**
     * 举证截止时间。
     */
    private LocalDateTime evidenceDeadline;

    /**
     * 结案时间。
     */
    private LocalDateTime closedTime;

    /**
     * 创建人编号。
     */
    private Long createdBy;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 当事人列表。
     */
    private List<CasePartyVO> parties;

    /**
     * 证据列表。
     */
    private List<CaseEvidenceVO> evidences;

    /**
     * 庭审列表。
     */
    private List<CaseTrialVO> trials;

    /**
     * 裁判文书列表。
     */
    private List<CaseJudgementVO> judgements;

    /**
     * 流程记录列表。
     */
    private List<CaseProcessVO> processes;
}
