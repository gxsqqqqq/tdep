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
 * 电子证据文件实体，对应 evidence_file 表。
 */
@Data
@TableName("evidence_file")
public class EvidenceFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 证据主键。
     */
    @TableId
    private Long id;

    /**
     * 证据编号。
     */
    private String evidenceNo;

    /**
     * 关联案件编号。
     */
    private Long caseId;

    /**
     * 上传人编号。
     */
    private Long uploaderId;

    /**
     * 原始文件名。
     */
    private String fileName;

    /**
     * 文件扩展名。
     */
    private String fileExt;

    /**
     * MIME 类型。
     */
    private String contentType;

    /**
     * 文件大小，单位字节。
     */
    private Long fileSize;

    /**
     * 原始文件 bucket。
     */
    private String bucketName;

    /**
     * 原始文件 object key。
     */
    private String objectKey;

    /**
     * 固化 PDF bucket。
     */
    private String sealedBucketName;

    /**
     * 固化 PDF object key。
     */
    private String sealedObjectKey;

    /**
     * 证据状态。
     */
    private String status;

    /**
     * 证据说明。
     */
    private String description;

    /**
     * 上传时间。
     */
    private LocalDateTime uploadedAt;

    /**
     * 固化时间。
     */
    private LocalDateTime sealedAt;

    /**
     * 删除时间。
     */
    private LocalDateTime deletedAt;

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
