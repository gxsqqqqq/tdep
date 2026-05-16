package com.tdep.casecenter.workflow;

import com.tdep.casecenter.enums.CaseAction;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.CaseStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 案件状态机，集中维护案件全生命周期允许的状态流转关系。
 */
@Component
public class CaseStateMachine {

    private final Map<CaseStatus, Map<CaseAction, CaseTransition>> transitions = new EnumMap<>(CaseStatus.class);

    /**
     * 初始化案件状态流转表。
     */
    public CaseStateMachine() {
        register(CaseStatus.DRAFT, CaseAction.SUBMIT, CaseRole.PARTY, CaseRole.ADMIN);
        register(CaseStatus.SUBMITTED, CaseAction.START_REVIEW, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.FILING_REVIEW, CaseAction.REQUEST_SUPPLEMENT, CaseRole.CLERK, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.NEED_SUPPLEMENT, CaseAction.SUBMIT, CaseRole.PARTY, CaseRole.ADMIN);
        register(CaseStatus.FILING_REVIEW, CaseAction.REJECT, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.FILING_REVIEW, CaseAction.ACCEPT, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.ACCEPTED, CaseAction.PAYMENT_NOTICE, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.PAYMENT_PENDING, CaseAction.CONFIRM_PAYMENT, CaseRole.PARTY, CaseRole.ADMIN);
        register(CaseStatus.PAID, CaseAction.OPEN_EVIDENCE, CaseRole.CLERK, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.EVIDENCE_SUBMITTING, CaseAction.SCHEDULE_TRIAL, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.SCHEDULED, CaseAction.START_TRIAL, CaseRole.JUDGE, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.IN_TRIAL, CaseAction.START_MEDIATION, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.MEDIATION, CaseAction.MEDIATION_SUCCESS, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.MEDIATION, CaseAction.MEDIATION_FAILED, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.MEDIATION_FAILED, CaseAction.JUDGEMENT_PENDING, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.IN_TRIAL, CaseAction.JUDGEMENT_PENDING, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.JUDGEMENT_PENDING, CaseAction.PUBLISH_JUDGEMENT, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.JUDGED, CaseAction.ENTER_APPEAL_PERIOD, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.APPEAL_PERIOD, CaseAction.SUBMIT_APPEAL, CaseRole.PARTY, CaseRole.ADMIN);
        register(CaseStatus.APPEAL_PERIOD, CaseAction.CONFIRM_EFFECTIVE, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.MEDIATION_SUCCESS, CaseAction.CONFIRM_EFFECTIVE, CaseRole.JUDGE, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.EFFECTIVE, CaseAction.APPLY_EXECUTION, CaseRole.PARTY, CaseRole.ADMIN);
        register(CaseStatus.EXECUTION_PENDING, CaseAction.START_EXECUTION, CaseRole.JUDGE, CaseRole.ADMIN);
        register(CaseStatus.EXECUTING, CaseAction.CLOSE, CaseRole.JUDGE, CaseRole.CLERK, CaseRole.ADMIN);
        register(CaseStatus.EFFECTIVE, CaseAction.CLOSE, CaseRole.JUDGE, CaseRole.CLERK, CaseRole.ADMIN);
        registerWithdrawAndTerminate();
    }

    /**
     * 查找状态流转定义。
     *
     * @param fromStatus 原状态
     * @param action     流转动作
     * @return 状态流转定义
     */
    public Optional<CaseTransition> findTransition(CaseStatus fromStatus, CaseAction action) {
        return Optional.ofNullable(transitions.getOrDefault(fromStatus, Map.of()).get(action));
    }

    /**
     * 注册状态流转关系。
     *
     * @param fromStatus   原状态
     * @param action       流转动作
     * @param allowedRoles 允许操作角色
     */
    private void register(CaseStatus fromStatus, CaseAction action, CaseRole... allowedRoles) {
        transitions.computeIfAbsent(fromStatus, key -> new EnumMap<>(CaseAction.class))
                .put(action, CaseTransition.builder()
                        .fromStatus(fromStatus)
                        .action(action)
                        .toStatus(action.getTargetStatus())
                        .allowedRoles(Set.of(allowedRoles))
                        .build());
    }

    /**
     * 注册撤诉和终结这类可从多个非终态触发的特殊流转。
     */
    private void registerWithdrawAndTerminate() {
        for (CaseStatus status : CaseStatus.values()) {
            if (status.terminal()) {
                continue;
            }
            register(status, CaseAction.WITHDRAW, CaseRole.PARTY, CaseRole.JUDGE, CaseRole.ADMIN);
            register(status, CaseAction.TERMINATE, CaseRole.JUDGE, CaseRole.ADMIN);
        }
    }
}
