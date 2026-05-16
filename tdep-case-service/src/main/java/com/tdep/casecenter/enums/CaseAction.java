package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 案件状态流转动作枚举。
 */
@Getter
@RequiredArgsConstructor
public enum CaseAction {

    /**
     * 提交立案。
     */
    SUBMIT(CaseStatus.SUBMITTED, "提交立案"),

    /**
     * 进入立案审核。
     */
    START_REVIEW(CaseStatus.FILING_REVIEW, "进入立案审核"),

    /**
     * 要求补正。
     */
    REQUEST_SUPPLEMENT(CaseStatus.NEED_SUPPLEMENT, "要求补正"),

    /**
     * 驳回立案。
     */
    REJECT(CaseStatus.FILING_REJECTED, "驳回立案"),

    /**
     * 审核受理。
     */
    ACCEPT(CaseStatus.ACCEPTED, "审核受理"),

    /**
     * 生成缴费通知。
     */
    PAYMENT_NOTICE(CaseStatus.PAYMENT_PENDING, "生成缴费通知"),

    /**
     * 确认缴费。
     */
    CONFIRM_PAYMENT(CaseStatus.PAID, "确认缴费"),

    /**
     * 开启举证。
     */
    OPEN_EVIDENCE(CaseStatus.EVIDENCE_SUBMITTING, "开启举证"),

    /**
     * 排期开庭。
     */
    SCHEDULE_TRIAL(CaseStatus.SCHEDULED, "排期开庭"),

    /**
     * 开始庭审。
     */
    START_TRIAL(CaseStatus.IN_TRIAL, "开始庭审"),

    /**
     * 发起调解。
     */
    START_MEDIATION(CaseStatus.MEDIATION, "发起调解"),

    /**
     * 调解成功。
     */
    MEDIATION_SUCCESS(CaseStatus.MEDIATION_SUCCESS, "调解成功"),

    /**
     * 调解失败。
     */
    MEDIATION_FAILED(CaseStatus.MEDIATION_FAILED, "调解失败"),

    /**
     * 转入待判决。
     */
    JUDGEMENT_PENDING(CaseStatus.JUDGEMENT_PENDING, "转入待判决"),

    /**
     * 发布判决。
     */
    PUBLISH_JUDGEMENT(CaseStatus.JUDGED, "发布判决"),

    /**
     * 进入上诉期。
     */
    ENTER_APPEAL_PERIOD(CaseStatus.APPEAL_PERIOD, "进入上诉期"),

    /**
     * 提交上诉。
     */
    SUBMIT_APPEAL(CaseStatus.APPEALED, "提交上诉"),

    /**
     * 确认生效。
     */
    CONFIRM_EFFECTIVE(CaseStatus.EFFECTIVE, "确认生效"),

    /**
     * 申请执行。
     */
    APPLY_EXECUTION(CaseStatus.EXECUTION_PENDING, "申请执行"),

    /**
     * 开始执行。
     */
    START_EXECUTION(CaseStatus.EXECUTING, "开始执行"),

    /**
     * 结案。
     */
    CLOSE(CaseStatus.CLOSED, "结案"),

    /**
     * 撤诉。
     */
    WITHDRAW(CaseStatus.WITHDRAWN, "撤诉"),

    /**
     * 终结案件。
     */
    TERMINATE(CaseStatus.TERMINATED, "终结案件");

    /**
     * 动作对应的目标状态。
     */
    private final CaseStatus targetStatus;

    /**
     * 动作中文描述。
     */
    private final String description;
}
