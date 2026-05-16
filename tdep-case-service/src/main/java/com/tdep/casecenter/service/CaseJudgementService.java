package com.tdep.casecenter.service;

import com.tdep.casecenter.dto.JudgementCreateRequest;
import com.tdep.casecenter.vo.CaseJudgementVO;

/**
 * 案件判决业务服务。
 */
public interface CaseJudgementService {

    /**
     * 创建判决记录。
     *
     * @param caseId  案件主键
     * @param request 判决请求
     * @return 判决信息
     */
    CaseJudgementVO create(Long caseId, JudgementCreateRequest request);
}
