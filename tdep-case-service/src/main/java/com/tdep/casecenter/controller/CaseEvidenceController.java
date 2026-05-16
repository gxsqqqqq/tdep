package com.tdep.casecenter.controller;

import com.tdep.casecenter.dto.EvidenceUploadRequest;
import com.tdep.casecenter.service.CaseEvidenceService;
import com.tdep.casecenter.vo.CaseEvidenceVO;
import com.tdep.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 案件证据接口控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/cases/{caseId}/evidences", "/cases/{caseId}/evidences"})
public class CaseEvidenceController {

    private final CaseEvidenceService caseEvidenceService;

    /**
     * 上传案件证据元数据。
     *
     * @param caseId  案件主键
     * @param request 证据请求
     * @return 证据信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('case:evidence:upload')")
    public Result<CaseEvidenceVO> upload(@PathVariable Long caseId, @Valid @RequestBody EvidenceUploadRequest request) {
        return Result.success(caseEvidenceService.upload(caseId, request));
    }
}
