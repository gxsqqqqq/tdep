package com.tdep.evidence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电子证据操作日志实体，对应 evidence_operation_log 表。
 */
@Data
@TableName("evidence_operation_log")
public class EvidenceOperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     */
    @TableId
    private Long id;

    /**
     * 证据主键。
     */
    private Long evidenceId;

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
     * User-Agent。
     */
    private String userAgent;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 逻辑删除标记。
     */
    @TableLogic
    private Integer deleted;
}
