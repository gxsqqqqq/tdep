package com.tdep.document.workflow;

import com.tdep.document.enums.DocumentAction;
import com.tdep.document.enums.DocumentRole;
import com.tdep.document.enums.DocumentStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DocumentStateMachine {

    private final Map<DocumentStatus, Map<DocumentAction, DocumentTransition>> transitions = new EnumMap<>(DocumentStatus.class);

    public DocumentStateMachine() {
        register(DocumentStatus.DRAFT, DocumentAction.SAVE_DRAFT, DocumentRole.PARTY, DocumentRole.CLERK, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.DRAFT, DocumentAction.GENERATE, DocumentRole.CLERK, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.REVIEW_REJECTED, DocumentAction.GENERATE, DocumentRole.CLERK, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.GENERATED, DocumentAction.SUBMIT_REVIEW, DocumentRole.CLERK, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.UNDER_REVIEW, DocumentAction.APPROVE_REVIEW, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.UNDER_REVIEW, DocumentAction.REJECT_REVIEW, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.GENERATED, DocumentAction.SIGN, DocumentRole.JUDGE, DocumentRole.ADMIN);
        register(DocumentStatus.SIGNED, DocumentAction.EFFECTIVE, DocumentRole.JUDGE, DocumentRole.CLERK, DocumentRole.ADMIN);
        register(DocumentStatus.EFFECTIVE, DocumentAction.ARCHIVE, DocumentRole.CLERK, DocumentRole.ADMIN);
        registerVoidTransitions();
    }

    public Optional<DocumentTransition> findTransition(DocumentStatus fromStatus, DocumentAction action) {
        return Optional.ofNullable(transitions.getOrDefault(fromStatus, Map.of()).get(action));
    }

    public boolean canTransit(DocumentStatus fromStatus, DocumentAction action, Set<DocumentRole> roles) {
        return findTransition(fromStatus, action)
                .map(transition -> transition.getAllowedRoles().stream().anyMatch(roles::contains))
                .orElse(false);
    }

    private void register(DocumentStatus fromStatus, DocumentAction action, DocumentRole... allowedRoles) {
        transitions.computeIfAbsent(fromStatus, key -> new EnumMap<>(DocumentAction.class))
                .put(action, DocumentTransition.builder()
                        .fromStatus(fromStatus)
                        .action(action)
                        .toStatus(action.getTargetStatus())
                        .allowedRoles(Set.of(allowedRoles))
                        .build());
    }

    private void registerVoidTransitions() {
        for (DocumentStatus status : DocumentStatus.values()) {
            if (!status.terminal()) {
                register(status, DocumentAction.VOID, DocumentRole.ADMIN);
            }
        }
    }
}
