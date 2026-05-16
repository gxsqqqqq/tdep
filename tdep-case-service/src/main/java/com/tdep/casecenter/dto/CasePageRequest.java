package com.tdep.casecenter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 案件分页查询参数。
 */
@Data
public class CasePageRequest {

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
     * 案件状态。
     */
    private String status;

    /**
     * 案号。
     */
    private String caseNo;

    /**
     * 案件标题关键字。
     */
    private String keyword;
}
