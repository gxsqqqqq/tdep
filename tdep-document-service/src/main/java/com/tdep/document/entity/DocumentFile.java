package com.tdep.document.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("document_file")
public class DocumentFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private Long caseId;

    private String documentNo;

    private String documentType;

    private String title;

    private String status;

    private Long templateId;

    private String wordBucket;

    private String wordObjectKey;

    private String pdfBucket;

    private String pdfObjectKey;

    private String signedBucket;

    private String signedObjectKey;

    private String fileHash;

    private Long createdBy;

    private Long reviewedBy;

    private Long signedBy;

    private LocalDateTime generatedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime signedAt;

    private LocalDateTime effectiveAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
