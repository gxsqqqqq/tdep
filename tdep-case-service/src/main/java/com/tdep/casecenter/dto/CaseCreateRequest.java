package com.tdep.casecenter.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建案件请求参数。
 */
@Data
public class CaseCreateRequest {

    /**
     * 案件标题。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 200, message = "长度不能超过 200 位")
    private String caseTitle;

    /**
     * 案件类型。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 32, message = "长度不能超过 32 位")
    private String caseType;

    /**
     * 案由。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 100, message = "长度不能超过 100 位")
    private String causeAction;

    /**
     * 程序类型。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 32, message = "长度不能超过 32 位")
    private String procedureType;

    /**
     * 诉讼请求。
     */
    private String claimRequest;

    /**
     * 标的金额。
     */
    private BigDecimal claimAmount;

    /**
     * 受理法院。
     */
    @Size(max = 100, message = "长度不能超过 100 位")
    private String courtName;

    /**
     * 案件当事人列表。
     */
    @Valid
    @NotEmpty(message = "不能为空")
    private List<CasePartyDTO> parties;
}
