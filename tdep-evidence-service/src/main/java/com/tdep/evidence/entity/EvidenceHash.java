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
 * 电子证据 Hash 实体，对应 evidence_hash 表。
 */
@Data
@TableName("evidence_hash")
public class EvidenceHash implements Serializable {

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
     * Hash 算法。
     */
    private String hashAlgorithm;

    /**
     * Hash 值。
     */
    private String hashValue;

    /**
     * 文件唯一指纹。
     */
    private String fileFingerprint;

    /**
     * 文件大小。
     */
    private Long fileSize;

    /**
     * Hash 计算时间。
     */
    private LocalDateTime calculatedAt;

    /**
     * 最近校验时间。
     */
    private LocalDateTime verifiedAt;

    /**
     * 最近校验结果：1 通过，0 失败。
     */
    private Integer verifyResult;

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
