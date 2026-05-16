package com.tdep.casecenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.casecenter.dto.AssignJudgeRequest;
import com.tdep.casecenter.dto.CaseCreateRequest;
import com.tdep.casecenter.dto.CasePageRequest;
import com.tdep.casecenter.dto.CaseTransitionRequest;
import com.tdep.casecenter.vo.CaseDetailVO;
import com.tdep.casecenter.vo.CaseListVO;

/**
 * 案件基础业务服务。
 */
public interface CaseService {

    /**
     * 创建案件草稿。
     *
     * @param request 创建请求
     * @return 案件详情
     */
    CaseDetailVO create(CaseCreateRequest request);

    /**
     * 分页查询案件列表。
     *
     * @param request 分页查询参数
     * @return 案件分页数据
     */
    Page<CaseListVO> page(CasePageRequest request);

    /**
     * 查询案件详情。
     *
     * @param id 案件主键
     * @return 案件详情
     */
    CaseDetailVO detail(Long id);

    /**
     * 案件状态流转。
     *
     * @param id      案件主键
     * @param request 状态流转参数
     * @return 案件详情
     */
    CaseDetailVO transition(Long id, CaseTransitionRequest request);

    /**
     * 指派承办法官。
     *
     * @param id      案件主键
     * @param request 指派请求
     * @return 案件详情
     */
    CaseDetailVO assignJudge(Long id, AssignJudgeRequest request);
}
