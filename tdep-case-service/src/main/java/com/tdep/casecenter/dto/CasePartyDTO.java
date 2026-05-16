package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 案件当事人请求参数。
 */
@Data
public class CasePartyDTO {

    /**
     * 当事人类型。
     */
    @NotBlank(message = "不能为空")
    private String partyType;

    /**
     * 当事人名称。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 100, message = "长度不能超过 100 位")
    private String partyName;

    /**
     * 证件类型。
     */
    @Size(max = 32, message = "长度不能超过 32 位")
    private String identityType;

    /**
     * 证件号码。
     */
    @Size(max = 64, message = "长度不能超过 64 位")
    private String identityNo;

    /**
     * 联系电话。
     */
    @Size(max = 20, message = "长度不能超过 20 位")
    private String contactPhone;

    /**
     * 联系地址。
     */
    @Size(max = 255, message = "长度不能超过 255 位")
    private String contactAddress;

    /**
     * 绑定平台用户编号。
     */
    @NotNull(message = "不能为空")
    private Long userId;
}
