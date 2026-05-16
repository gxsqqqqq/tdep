package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案件庭审响应视图。
 */
@Data
@Builder
public class CaseTrialVO {

    /**
     * 庭审主键。
     */
    private Long id;

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
}
