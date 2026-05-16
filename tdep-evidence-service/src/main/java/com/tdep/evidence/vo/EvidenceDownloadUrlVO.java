package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据预签名下载地址响应视图。
 */
@Data
@Builder
public class EvidenceDownloadUrlVO {

    /**
     * 证据主键。
     */
    private Long evidenceId;

    /**
     * 下载地址。
     */
    private String url;

    /**
     * 过期时间。
     */
    private LocalDateTime expireAt;
}
