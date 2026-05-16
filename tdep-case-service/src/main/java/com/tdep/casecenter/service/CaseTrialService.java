package com.tdep.casecenter.service;

import com.tdep.casecenter.dto.TrialCreateRequest;
import com.tdep.casecenter.dto.TrialRecordRequest;
import com.tdep.casecenter.vo.CaseTrialVO;

/**
 * 案件庭审业务服务。
 */
public interface CaseTrialService {

    /**
     * 创建庭审记录。
     *
     * @param caseId  案件主键
     * @param request 庭审请求
     * @return 庭审信息
     */
    CaseTrialVO create(Long caseId, TrialCreateRequest request);

    /**
     * 更新庭审记录内容。
     *
     * @param caseId   案件主键
     * @param trialId  庭审主键
     * @param request  庭审记录请求
     * @return 庭审信息
     */
    CaseTrialVO updateRecord(Long caseId, Long trialId, TrialRecordRequest request);
}
