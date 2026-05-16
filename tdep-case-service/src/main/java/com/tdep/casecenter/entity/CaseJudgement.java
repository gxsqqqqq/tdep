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
 * 案件裁判文书实体，对应 case_judgement 表。
 */
@Data
@TableName("case_judgement")
public class CaseJudgement implements Serializable {

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
     * 文书编号。
     */
    private String judgementNo;

    /**
     * 文书类型。
     */
    private String judgementType;

    /**
     * 文书标题。
     */
    private String title;

    /**
     * 裁判摘要。
     */
    private String contentSummary;

    /**
     * 文书文件地址。
     */
    private String documentUrl;

    /**
     * 出具法官编号。
     */
    private Long judgeId;

    /**
     * 发布时间。
     */
    private LocalDateTime publishTime;

    /**
     * 生效时间。
     */
    private LocalDateTime effectiveTime;

    /**
     * 上诉截止时间。
     */
    private LocalDateTime appealDeadline;

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
