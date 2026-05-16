package com.tdep.casecenter.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.casecenter.dto.EvidenceUploadRequest;
import com.tdep.casecenter.entity.CaseEvidence;
import com.tdep.casecenter.entity.CaseInfo;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.CaseStatus;
import com.tdep.casecenter.mapper.CaseEvidenceMapper;
import com.tdep.casecenter.mapper.CaseInfoMapper;
import com.tdep.casecenter.service.CaseEvidenceService;
import com.tdep.casecenter.vo.CaseEvidenceVO;
import com.tdep.casecenter.workflow.CasePermissionChecker;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.TdepUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 案件证据业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class CaseEvidenceServiceImpl implements CaseEvidenceService {

    private final CaseInfoMapper caseInfoMapper;

    private final CaseEvidenceMapper caseEvidenceMapper;

    private final CasePermissionChecker casePermissionChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseEvidenceVO upload(Long caseId, EvidenceUploadRequest request) {
        CaseInfo caseInfo = requireCase(caseId);
        ensureEvidenceUploadAllowed(caseInfo);
        TdepUserPrincipal principal = casePermissionChecker.currentPrincipal();
        CaseEvidence evidence = new CaseEvidence();
        evidence.setCaseId(caseId);
        evidence.setEvidenceName(request.getEvidenceName());
        evidence.setEvidenceType(request.getEvidenceType());
        evidence.setFileUrl(request.getFileUrl());
        evidence.setFileHash(request.getFileHash());
        evidence.setDescription(request.getDescription());
        evidence.setUploadedBy(principal.getUserId());
        evidence.setUploadedAt(LocalDateTime.now());
        caseEvidenceMapper.insert(evidence);
        return toVO(evidence);
    }

    /**
     * 查询案件，不存在则抛出异常。
     *
     * @param caseId 案件主键
     * @return 案件实体
     */
    private CaseInfo requireCase(Long caseId) {
        CaseInfo caseInfo = caseInfoMapper.selectOne(new LambdaQueryWrapper<CaseInfo>().eq(CaseInfo::getId, caseId));
        if (caseInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "案件不存在");
        }
        return caseInfo;
    }

    /**
     * 校验证据上传权限和案件状态。
     *
     * @param caseInfo 案件实体
     */
    private void ensureEvidenceUploadAllowed(CaseInfo caseInfo) {
        CaseStatus status = CaseStatus.valueOf(caseInfo.getStatus());
        if (status.terminal()) {
            throw new BusinessException(ResultCode.CONFLICT, "终态案件不允许上传证据");
        }
        if (status.ordinal() < CaseStatus.PAID.ordinal()) {
            throw new BusinessException(ResultCode.CONFLICT, "案件缴费前不允许上传证据");
        }
        Set<CaseRole> roles = casePermissionChecker.currentCaseRoles();
        if (!(roles.contains(CaseRole.PARTY) || roles.contains(CaseRole.ADMIN))) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权上传证据");
        }
    }

    /**
     * 转换证据响应视图。
     *
     * @param evidence 证据实体
     * @return 证据响应
     */
    private CaseEvidenceVO toVO(CaseEvidence evidence) {
        return CaseEvidenceVO.builder()
                .id(evidence.getId())
                .evidenceName(evidence.getEvidenceName())
                .evidenceType(evidence.getEvidenceType())
                .fileUrl(evidence.getFileUrl())
                .fileHash(evidence.getFileHash())
                .description(evidence.getDescription())
                .uploadedBy(evidence.getUploadedBy())
                .uploadedAt(evidence.getUploadedAt())
                .build();
    }
}
