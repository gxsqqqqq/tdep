package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案件裁判文书响应视图。
 */
@Data
@Builder
public class CaseJudgementVO {

    /**
     * 主键。
     */
    private Long id;

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
}
