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
 * 案件证据实体，对应 case_evidence 表。
 */
@Data
@TableName("case_evidence")
public class CaseEvidence implements Serializable {

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
     * 证据名称。
     */
    private String evidenceName;

    /**
     * 证据类型。
     */
    private String evidenceType;

    /**
     * 文件地址。
     */
    private String fileUrl;

    /**
     * 文件哈希。
     */
    private String fileHash;

    /**
     * 证据说明。
     */
    private String description;

    /**
     * 上传人编号。
     */
    private Long uploadedBy;

    /**
     * 上传时间。
     */
    private LocalDateTime uploadedAt;

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
