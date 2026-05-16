package com.tdep.evidence.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 证据分页查询请求。
 */
@Data
public class EvidencePageRequest {

    /**
     * 当前页码。
     */
    @Min(value = 1, message = "不能小于 1")
    private long pageNo = 1;

    /**
     * 每页数量。
     */
    @Min(value = 1, message = "不能小于 1")
    @Max(value = 100, message = "不能超过 100")
    private long pageSize = 10;

    /**
     * 案件编号。
     */
    private Long caseId;

    /**
     * 证据状态。
     */
    private String status;

    /**
     * 文件名关键字。
     */
    private String keyword;
}
