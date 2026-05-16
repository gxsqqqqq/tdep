package com.tdep.casecenter.controller;

import com.tdep.casecenter.dto.JudgementCreateRequest;
import com.tdep.casecenter.service.CaseJudgementService;
import com.tdep.casecenter.vo.CaseJudgementVO;
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
 * 案件判决接口控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/cases/{caseId}/judgements", "/cases/{caseId}/judgements"})
public class CaseJudgementController {

    private final CaseJudgementService caseJudgementService;

    /**
     * 创建判决记录。
     *
     * @param caseId  案件主键
     * @param request 判决请求
     * @return 判决信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('case:judgement:publish')")
    public Result<CaseJudgementVO> create(@PathVariable Long caseId, @Valid @RequestBody JudgementCreateRequest request) {
        return Result.success(caseJudgementService.create(caseId, request));
    }
}
