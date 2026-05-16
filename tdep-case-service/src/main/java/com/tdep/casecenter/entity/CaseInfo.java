package com.tdep.casecenter.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 案件主表实体，对应 case_info 表。
 */
@Data
@TableName("case_info")
public class CaseInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 案件主键。
     */
    @TableId
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
     * 承办法官用户编号。
     */
    private Long judgeId;

    /**
     * 书记员用户编号。
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
     * 创建人用户编号。
     */
    private Long createdBy;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记。
     */
    @TableLogic
    private Integer deleted;
}
