package com.tdep.casecenter.serviceImpl;

import com.tdep.casecenter.dto.JudgementCreateRequest;
import com.tdep.casecenter.entity.CaseInfo;
import com.tdep.casecenter.entity.CaseJudgement;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.mapper.CaseInfoMapper;
import com.tdep.casecenter.mapper.CaseJudgementMapper;
import com.tdep.casecenter.service.CaseJudgementService;
import com.tdep.casecenter.vo.CaseJudgementVO;
import com.tdep.casecenter.workflow.CasePermissionChecker;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * 案件判决业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class CaseJudgementServiceImpl implements CaseJudgementService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final CaseInfoMapper caseInfoMapper;

    private final CaseJudgementMapper caseJudgementMapper;

    private final CasePermissionChecker casePermissionChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseJudgementVO create(Long caseId, JudgementCreateRequest request) {
        requireCase(caseId);
        ensureJudgementAllowed();
        CaseJudgement judgement = new CaseJudgement();
        judgement.setCaseId(caseId);
        judgement.setJudgementNo(generateJudgementNo());
        judgement.setJudgementType(request.getJudgementType());
        judgement.setTitle(request.getTitle());
        judgement.setContentSummary(request.getContentSummary());
        judgement.setDocumentUrl(request.getDocumentUrl());
        judgement.setJudgeId(request.getJudgeId());
        judgement.setPublishTime(LocalDateTime.now());
        judgement.setEffectiveTime(request.getEffectiveTime());
        judgement.setAppealDeadline(request.getAppealDeadline());
        caseJudgementMapper.insert(judgement);
        return toVO(judgement);
    }

    /**
     * 查询案件，不存在则抛出异常。
     *
     * @param caseId 案件主键
     */
    private void requireCase(Long caseId) {
        CaseInfo caseInfo = caseInfoMapper.selectById(caseId);
        if (caseInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "案件不存在");
        }
    }

    /**
     * 校验判决维护权限。
     */
    private void ensureJudgementAllowed() {
        Set<CaseRole> roles = casePermissionChecker.currentCaseRoles();
        if (!(roles.contains(CaseRole.JUDGE) || roles.contains(CaseRole.ADMIN))) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权维护判决记录");
        }
    }

    /**
     * 生成文书编号。
     *
     * @return 文书编号
     */
    private String generateJudgementNo() {
        return "JDG-" + LocalDate.now().format(DATE_FORMATTER) + "-" + System.nanoTime();
    }

    /**
     * 转换判决响应视图。
     *
     * @param judgement 判决实体
     * @return 判决响应
     */
    private CaseJudgementVO toVO(CaseJudgement judgement) {
        return CaseJudgementVO.builder()
                .id(judgement.getId())
                .judgementNo(judgement.getJudgementNo())
                .judgementType(judgement.getJudgementType())
                .title(judgement.getTitle())
                .contentSummary(judgement.getContentSummary())
                .documentUrl(judgement.getDocumentUrl())
                .judgeId(judgement.getJudgeId())
                .publishTime(judgement.getPublishTime())
                .effectiveTime(judgement.getEffectiveTime())
                .appealDeadline(judgement.getAppealDeadline())
                .build();
    }
}
