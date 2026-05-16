package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据链响应视图。
 */
@Data
@Builder
public class EvidenceChainVO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 链序号。
     */
    private Integer chainSeq;

    /**
     * 事件类型。
     */
    private String eventType;

    /**
     * 事件数据 Hash。
     */
    private String eventDataHash;

    /**
     * 上一条链 Hash。
     */
    private String previousChainHash;

    /**
     * 当前链 Hash。
     */
    private String currentChainHash;

    /**
     * 操作人编号。
     */
    private Long operatorId;

    /**
     * 操作角色。
     */
    private String operatorRole;

    /**
     * 事件时间。
     */
    private LocalDateTime eventTime;

    /**
     * 时间戳值。
     */
    private String timestampValue;

    /**
     * 备注。
     */
    private String remark;
}
