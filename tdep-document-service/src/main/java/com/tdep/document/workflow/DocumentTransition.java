package com.tdep.document.workflow;

import com.tdep.document.enums.DocumentAction;
import com.tdep.document.enums.DocumentRole;
import com.tdep.document.enums.DocumentStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class DocumentTransition {

    private DocumentStatus fromStatus;

    private DocumentAction action;

    private DocumentStatus toStatus;

    private Set<DocumentRole> allowedRoles;
}
