package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 案件当事人响应视图。
 */
@Data
@Builder
public class CasePartyVO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 当事人类型。
     */
    private String partyType;

    /**
     * 当事人名称。
     */
    private String partyName;

    /**
     * 证件类型。
     */
    private String identityType;

    /**
     * 证件号码。
     */
    private String identityNo;

    /**
     * 联系电话。
     */
    private String contactPhone;

    /**
     * 联系地址。
     */
    private String contactAddress;

    /**
     * 绑定用户编号。
     */
    private Long userId;
}
