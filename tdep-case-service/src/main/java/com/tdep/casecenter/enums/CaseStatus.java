package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 案件状态枚举，统一管理案件生命周期状态。
 */
@Getter
@RequiredArgsConstructor
public enum CaseStatus {

    /**
     * 草稿。
     */
    DRAFT("草稿"),

    /**
     * 已提交立案。
     */
    SUBMITTED("已提交立案"),

    /**
     * 立案审核中。
     */
    FILING_REVIEW("立案审核中"),

    /**
     * 待补正。
     */
    NEED_SUPPLEMENT("待补正"),

    /**
     * 立案驳回。
     */
    FILING_REJECTED("立案驳回"),

    /**
     * 已受理。
     */
    ACCEPTED("已受理"),

    /**
     * 待缴费。
     */
    PAYMENT_PENDING("待缴费"),

    /**
     * 已缴费。
     */
    PAID("已缴费"),

    /**
     * 举证中。
     */
    EVIDENCE_SUBMITTING("举证中"),

    /**
     * 已排期。
     */
    SCHEDULED("已排期"),

    /**
     * 庭审中。
     */
    IN_TRIAL("庭审中"),

    /**
     * 调解中。
     */
    MEDIATION("调解中"),

    /**
     * 调解成功。
     */
    MEDIATION_SUCCESS("调解成功"),

    /**
     * 调解失败。
     */
    MEDIATION_FAILED("调解失败"),

    /**
     * 待判决。
     */
    JUDGEMENT_PENDING("待判决"),

    /**
     * 已判决。
     */
    JUDGED("已判决"),

    /**
     * 上诉期。
     */
    APPEAL_PERIOD("上诉期"),

    /**
     * 已上诉。
     */
    APPEALED("已上诉"),

    /**
     * 已生效。
     */
    EFFECTIVE("已生效"),

    /**
     * 待执行。
     */
    EXECUTION_PENDING("待执行"),

    /**
     * 执行中。
     */
    EXECUTING("执行中"),

    /**
     * 已结案。
     */
    CLOSED("已结案"),

    /**
     * 已撤诉。
     */
    WITHDRAWN("已撤诉"),

    /**
     * 已终结。
     */
    TERMINATED("已终结");

    /**
     * 状态中文描述。
     */
    private final String description;

    /**
     * 判断是否为终态。
     *
     * @return true 表示终态
     */
    public boolean terminal() {
        return this == CLOSED || this == WITHDRAWN || this == TERMINATED || this == FILING_REJECTED;
    }
}
