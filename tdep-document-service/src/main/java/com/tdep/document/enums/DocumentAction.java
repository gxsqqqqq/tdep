package com.tdep.document.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentAction {

    SAVE_DRAFT(DocumentStatus.DRAFT),
    GENERATE(DocumentStatus.GENERATED),
    SUBMIT_REVIEW(DocumentStatus.UNDER_REVIEW),
    APPROVE_REVIEW(DocumentStatus.GENERATED),
    REJECT_REVIEW(DocumentStatus.REVIEW_REJECTED),
    SIGN(DocumentStatus.SIGNED),
    EFFECTIVE(DocumentStatus.EFFECTIVE),
    ARCHIVE(DocumentStatus.ARCHIVED),
    VOID(DocumentStatus.VOIDED);

    private final DocumentStatus targetStatus;
}
