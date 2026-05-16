package com.tdep.document.audit;

import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentOperationLog;
import com.tdep.document.enums.DocumentOperationType;
import com.tdep.document.enums.OperationResult;
import com.tdep.document.mapper.DocumentOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentAuditRecorder {

    private final DocumentOperationLogMapper operationLogMapper;

    public void record(DocumentFile documentFile, DocumentOperationType operationType, String beforeStatus,
                       String afterStatus, OperationResult result, String remark) {
        TdepUserPrincipal principal = SecurityContextUtil.currentUser().orElse(null);
        DocumentOperationLog log = new DocumentOperationLog();
        log.setDocumentId(documentFile == null ? null : documentFile.getId());
        log.setCaseId(documentFile == null ? null : documentFile.getCaseId());
        log.setOperationType(operationType.name());
        log.setOperatorId(principal == null ? null : principal.getUserId());
        log.setOperatorRole(principal == null || principal.getRoles() == null ? null : String.join(",", principal.getRoles()));
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setResult(result.name());
        log.setRemark(remark);
        operationLogMapper.insert(log);
    }
}
