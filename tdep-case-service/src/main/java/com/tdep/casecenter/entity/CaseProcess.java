package com.tdep.casecenter.entity;

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
 * 案件流程记录实体，对应 case_process 表。
 */
@Data
@TableName("case_process")
public class CaseProcess implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     */
    @TableId
    private Long id;

    /**
     * 案件主键。
     */
    private Long caseId;

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
