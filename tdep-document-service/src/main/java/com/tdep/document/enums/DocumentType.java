package com.tdep.document.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    COMPLAINT("COMPLAINT", "起诉状", "诉", false, Set.of("DRAFT", "SUBMITTED", "FILING_REVIEW")),
    DEFENSE("DEFENSE", "答辩状", "答", false, Set.of("ACCEPTED", "PAYMENT_PENDING", "PAID", "EVIDENCE_SUBMITTING", "SCHEDULED")),
    EVIDENCE_NOTICE("EVIDENCE_NOTICE", "举证通知书", "举", false, Set.of("PAID", "EVIDENCE_SUBMITTING")),
    TRIAL_NOTICE("TRIAL_NOTICE", "开庭通知书", "庭", false, Set.of("SCHEDULED")),
    MEDIATION_STATEMENT("MEDIATION_STATEMENT", "调解书", "调", true, Set.of("MEDIATION_SUCCESS")),
    JUDGEMENT("JUDGEMENT", "判决书", "判", true, Set.of("JUDGEMENT_PENDING", "JUDGED")),
    EXECUTION_NOTICE("EXECUTION_NOTICE", "执行通知书", "执", true, Set.of("EXECUTION_PENDING", "EXECUTING"));

    private final String code;

    private final String name;

    private final String shortName;

    private final boolean signRequired;

    private final Set<String> allowedCaseStatuses;
}
