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
 * 案件庭审实体，对应 case_trial 表。
 */
@Data
@TableName("case_trial")
public class CaseTrial implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 庭审主键。
     */
    @TableId
    private Long id;

    /**
     * 案件主键。
     */
    private Long caseId;

    /**
     * 庭审编号。
     */
    private String trialNo;

    /**
     * 庭审类型。
     */
    private String trialType;

    /**
     * 开庭时间。
     */
    private LocalDateTime trialTime;

    /**
     * 开庭地点或线上地址。
     */
    private String trialLocation;

    /**
     * 承办法官编号。
     */
    private Long judgeId;

    /**
     * 书记员编号。
     */
    private Long clerkId;

    /**
     * 庭审状态。
     */
    private String trialStatus;

    /**
     * 庭审记录内容。
     */
    private String trialRecord;

    /**
     * 庭审记录文件地址。
     */
    private String recordUrl;

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
