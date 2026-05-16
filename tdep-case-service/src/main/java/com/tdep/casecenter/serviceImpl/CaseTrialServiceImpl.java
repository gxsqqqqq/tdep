package com.tdep.casecenter.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.casecenter.dto.TrialCreateRequest;
import com.tdep.casecenter.dto.TrialRecordRequest;
import com.tdep.casecenter.entity.CaseInfo;
import com.tdep.casecenter.entity.CaseTrial;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.TrialStatus;
import com.tdep.casecenter.mapper.CaseInfoMapper;
import com.tdep.casecenter.mapper.CaseTrialMapper;
import com.tdep.casecenter.service.CaseTrialService;
import com.tdep.casecenter.vo.CaseTrialVO;
import com.tdep.casecenter.workflow.CasePermissionChecker;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * 案件庭审业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class CaseTrialServiceImpl implements CaseTrialService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final CaseInfoMapper caseInfoMapper;

    private final CaseTrialMapper caseTrialMapper;

    private final CasePermissionChecker casePermissionChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTrialVO create(Long caseId, TrialCreateRequest request) {
        requireCase(caseId);
        ensureTrialManageAllowed();
        CaseTrial trial = new CaseTrial();
        trial.setCaseId(caseId);
        trial.setTrialNo(generateTrialNo());
        trial.setTrialType(request.getTrialType());
        trial.setTrialTime(request.getTrialTime());
        trial.setTrialLocation(request.getTrialLocation());
        trial.setJudgeId(request.getJudgeId());
        trial.setClerkId(request.getClerkId());
        trial.setTrialStatus(TrialStatus.SCHEDULED.name());
        trial.setTrialRecord(request.getTrialRecord());
        trial.setRecordUrl(request.getRecordUrl());
        caseTrialMapper.insert(trial);
        return toVO(trial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTrialVO updateRecord(Long caseId, Long trialId, TrialRecordRequest request) {
        requireCase(caseId);
        ensureTrialManageAllowed();
        CaseTrial trial = caseTrialMapper.selectOne(new LambdaQueryWrapper<CaseTrial>()
                .eq(CaseTrial::getId, trialId)
                .eq(CaseTrial::getCaseId, caseId));
        if (trial == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "庭审记录不存在");
        }
        trial.setTrialRecord(request.getTrialRecord());
        trial.setRecordUrl(request.getRecordUrl());
        trial.setTrialStatus(TrialStatus.FINISHED.name());
        caseTrialMapper.updateById(trial);
        return toVO(trial);
    }

    /**
     * 查询案件，不存在则抛出异常。
     *
     * @param caseId 案件主键
     */
    private void requireCase(Long caseId) {
        if (caseInfoMapper.selectById(caseId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "案件不存在");
        }
    }

    /**
     * 校验庭审管理权限。
     */
    private void ensureTrialManageAllowed() {
        Set<CaseRole> roles = casePermissionChecker.currentCaseRoles();
        if (!(roles.contains(CaseRole.JUDGE) || roles.contains(CaseRole.CLERK) || roles.contains(CaseRole.ADMIN))) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权维护庭审记录");
        }
    }

    /**
     * 生成庭审编号。
     *
     * @return 庭审编号
     */
    private String generateTrialNo() {
        return "TRIAL-" + LocalDate.now().format(DATE_FORMATTER) + "-" + System.nanoTime();
    }

    /**
     * 转换庭审响应视图。
     *
     * @param trial 庭审实体
     * @return 庭审响应
     */
    private CaseTrialVO toVO(CaseTrial trial) {
        return CaseTrialVO.builder()
                .id(trial.getId())
                .trialNo(trial.getTrialNo())
                .trialType(trial.getTrialType())
                .trialTime(trial.getTrialTime())
                .trialLocation(trial.getTrialLocation())
                .judgeId(trial.getJudgeId())
                .clerkId(trial.getClerkId())
                .trialStatus(trial.getTrialStatus())
                .trialRecord(trial.getTrialRecord())
                .recordUrl(trial.getRecordUrl())
                .build();
    }
}
