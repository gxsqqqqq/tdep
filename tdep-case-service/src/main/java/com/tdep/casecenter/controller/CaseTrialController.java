package com.tdep.casecenter.controller;

import com.tdep.casecenter.dto.TrialCreateRequest;
import com.tdep.casecenter.dto.TrialRecordRequest;
import com.tdep.casecenter.service.CaseTrialService;
import com.tdep.casecenter.vo.CaseTrialVO;
import com.tdep.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 案件庭审接口控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/cases/{caseId}/trials", "/cases/{caseId}/trials"})
public class CaseTrialController {

    private final CaseTrialService caseTrialService;

    /**
     * 创建庭审记录。
     *
     * @param caseId  案件主键
     * @param request 庭审请求
     * @return 庭审信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('case:trial:manage')")
    public Result<CaseTrialVO> create(@PathVariable Long caseId, @Valid @RequestBody TrialCreateRequest request) {
        return Result.success(caseTrialService.create(caseId, request));
    }

    /**
     * 更新庭审记录。
     *
     * @param caseId  案件主键
     * @param trialId 庭审主键
     * @param request 庭审记录请求
     * @return 庭审信息
     */
    @PutMapping("/{trialId}/record")
    @PreAuthorize("hasAuthority('case:trial:manage')")
    public Result<CaseTrialVO> updateRecord(@PathVariable Long caseId,
                                            @PathVariable Long trialId,
                                            @Valid @RequestBody TrialRecordRequest request) {
        return Result.success(caseTrialService.updateRecord(caseId, trialId, request));
    }
}
