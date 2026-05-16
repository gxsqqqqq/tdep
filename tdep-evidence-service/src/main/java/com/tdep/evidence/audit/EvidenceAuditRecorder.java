package com.tdep.evidence.audit;

import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.evidence.entity.EvidenceOperationLog;
import com.tdep.evidence.enums.EvidenceOperationType;
import com.tdep.evidence.enums.OperationResult;
import com.tdep.evidence.mapper.EvidenceOperationLogMapper;
import com.tdep.evidence.security.EvidencePermissionChecker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 证据操作审计记录器。
 */
@Component
@RequiredArgsConstructor
public class EvidenceAuditRecorder {

    private final EvidenceOperationLogMapper operationLogMapper;

    private final EvidencePermissionChecker permissionChecker;

    /**
     * 写入操作审计日志。
     *
     * @param evidenceId 证据主键
     * @param type       操作类型
     * @param result     操作结果
     * @param detail     操作详情
     * @param request    HTTP 请求
     */
    public void record(Long evidenceId,
                       EvidenceOperationType type,
                       OperationResult result,
                       String detail,
                       HttpServletRequest request) {
        TdepUserPrincipal principal = permissionChecker.currentPrincipal();
        EvidenceOperationLog log = new EvidenceOperationLog();
        log.setEvidenceId(evidenceId);
        log.setOperationType(type.name());
        log.setOperatorId(principal.getUserId());
        log.setOperatorRole(permissionChecker.currentRole());
        log.setRequestIp(resolveIp(request));
        log.setUserAgent(request == null ? null : request.getHeader("User-Agent"));
        log.setOperationResult(result.name());
        log.setOperationDetail(detail);
        operationLogMapper.insert(log);
    }

    /**
     * 解析请求 IP。
     *
     * @param request HTTP 请求
     * @return 请求 IP
     */
    private String resolveIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
