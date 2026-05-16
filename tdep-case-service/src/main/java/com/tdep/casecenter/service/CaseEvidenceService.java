package com.tdep.casecenter.service;

import com.tdep.casecenter.dto.EvidenceUploadRequest;
import com.tdep.casecenter.vo.CaseEvidenceVO;

/**
 * 案件证据业务服务。
 */
public interface CaseEvidenceService {

    /**
     * 上传案件证据元数据。
     *
     * @param caseId  案件主键
     * @param request 证据请求
     * @return 证据信息
     */
    CaseEvidenceVO upload(Long caseId, EvidenceUploadRequest request);
}
