package com.tdep.casecenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.casecenter.dto.AssignJudgeRequest;
import com.tdep.casecenter.dto.CaseCreateRequest;
import com.tdep.casecenter.dto.CasePageRequest;
import com.tdep.casecenter.dto.CaseTransitionRequest;
import com.tdep.casecenter.service.CaseService;
import com.tdep.casecenter.vo.CaseDetailVO;
import com.tdep.casecenter.vo.CaseListVO;
import com.tdep.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 案件基础接口控制器，只负责请求接收、参数校验和统一响应封装。
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/cases", "/cases"})
public class CaseController {

    private final CaseService caseService;

    /**
     * 创建案件草稿。
     *
     * @param request 创建案件请求
     * @return 案件详情
     */
    @PostMapping
    @PreAuthorize("hasAuthority('case:create')")
    public Result<CaseDetailVO> create(@Valid @RequestBody CaseCreateRequest request) {
        return Result.success(caseService.create(request));
    }

    /**
     * 分页查询案件列表。
     *
     * @param request 分页查询参数
     * @return 案件分页数据
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('case:read:self','case:read:all')")
    public Result<Page<CaseListVO>> page(@Valid CasePageRequest request) {
        return Result.success(caseService.page(request));
    }

    /**
     * 查询案件详情。
     *
     * @param id 案件主键
     * @return 案件详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('case:read:self','case:read:all')")
    public Result<CaseDetailVO> detail(@PathVariable Long id) {
        return Result.success(caseService.detail(id));
    }

    /**
     * 案件状态流转。
     *
     * @param id      案件主键
     * @param request 状态流转请求
     * @return 案件详情
     */
    @PostMapping("/{id}/transitions")
    @PreAuthorize("hasAuthority('case:workflow')")
    public Result<CaseDetailVO> transition(@PathVariable Long id, @Valid @RequestBody CaseTransitionRequest request) {
        return Result.success(caseService.transition(id, request));
    }

    /**
     * 指派承办法官。
     *
     * @param id      案件主键
     * @param request 指派法官请求
     * @return 案件详情
     */
    @PutMapping("/{id}/judge")
    @PreAuthorize("hasAuthority('case:assign')")
    public Result<CaseDetailVO> assignJudge(@PathVariable Long id, @Valid @RequestBody AssignJudgeRequest request) {
        return Result.success(caseService.assignJudge(id, request));
    }
}
