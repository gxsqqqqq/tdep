package com.tdep.casecenter.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.casecenter.dto.AssignJudgeRequest;
import com.tdep.casecenter.dto.CaseCreateRequest;
import com.tdep.casecenter.dto.CasePageRequest;
import com.tdep.casecenter.dto.CasePartyDTO;
import com.tdep.casecenter.dto.CaseTransitionRequest;
import com.tdep.casecenter.entity.CaseEvidence;
import com.tdep.casecenter.entity.CaseInfo;
import com.tdep.casecenter.entity.CaseJudgement;
import com.tdep.casecenter.entity.CaseParty;
import com.tdep.casecenter.entity.CaseProcess;
import com.tdep.casecenter.entity.CaseTrial;
import com.tdep.casecenter.enums.CaseAction;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.CaseStatus;
import com.tdep.casecenter.mapper.CaseEvidenceMapper;
import com.tdep.casecenter.mapper.CaseInfoMapper;
import com.tdep.casecenter.mapper.CaseJudgementMapper;
import com.tdep.casecenter.mapper.CasePartyMapper;
import com.tdep.casecenter.mapper.CaseProcessMapper;
import com.tdep.casecenter.mapper.CaseTrialMapper;
import com.tdep.casecenter.service.CaseService;
import com.tdep.casecenter.vo.CaseDetailVO;
import com.tdep.casecenter.vo.CaseEvidenceVO;
import com.tdep.casecenter.vo.CaseJudgementVO;
import com.tdep.casecenter.vo.CaseListVO;
import com.tdep.casecenter.vo.CasePartyVO;
import com.tdep.casecenter.vo.CaseProcessVO;
import com.tdep.casecenter.vo.CaseTrialVO;
import com.tdep.casecenter.workflow.CasePermissionChecker;
import com.tdep.casecenter.workflow.CaseTransition;
import com.tdep.casecenter.workflow.CaseTransitionValidator;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.TdepUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 案件基础业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private static final DateTimeFormatter CASE_NO_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final CaseInfoMapper caseInfoMapper;

    private final CasePartyMapper casePartyMapper;

    private final CaseProcessMapper caseProcessMapper;

    private final CaseEvidenceMapper caseEvidenceMapper;

    private final CaseTrialMapper caseTrialMapper;

    private final CaseJudgementMapper caseJudgementMapper;

    private final CaseTransitionValidator caseTransitionValidator;

    private final CasePermissionChecker casePermissionChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseDetailVO create(CaseCreateRequest request) {
        TdepUserPrincipal principal = casePermissionChecker.currentPrincipal();
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseNo(generateCaseNo());
        caseInfo.setCaseTitle(request.getCaseTitle());
        caseInfo.setCaseType(request.getCaseType());
        caseInfo.setCauseAction(request.getCauseAction());
        caseInfo.setProcedureType(request.getProcedureType());
        caseInfo.setClaimRequest(request.getClaimRequest());
        caseInfo.setClaimAmount(request.getClaimAmount());
        caseInfo.setCourtName(request.getCourtName());
        caseInfo.setStatus(CaseStatus.DRAFT.name());
        caseInfo.setCreatedBy(principal.getUserId());
        caseInfoMapper.insert(caseInfo);

        for (CasePartyDTO partyDTO : request.getParties()) {
            CaseParty party = new CaseParty();
            party.setCaseId(caseInfo.getId());
            party.setPartyType(partyDTO.getPartyType());
            party.setPartyName(partyDTO.getPartyName());
            party.setIdentityType(partyDTO.getIdentityType());
            party.setIdentityNo(partyDTO.getIdentityNo());
            party.setContactPhone(partyDTO.getContactPhone());
            party.setContactAddress(partyDTO.getContactAddress());
            if (partyDTO.getUserId() == null) {
                party.setUserId(principal.getUserId());
            } else {
                party.setUserId(partyDTO.getUserId());
            }
            casePartyMapper.insert(party);
        }
        recordProcess(caseInfo.getId(), null, CaseStatus.DRAFT, "CREATE", "创建案件草稿", CaseRole.PARTY.name());
        return detail(caseInfo.getId());
    }

    @Override
    public Page<CaseListVO> page(CasePageRequest request) {
        Page<CaseInfo> page = caseInfoMapper.selectPage(Page.of(request.getPageNo(), request.getPageSize()),
                new LambdaQueryWrapper<CaseInfo>()
                        .eq(StringUtils.hasText(request.getStatus()), CaseInfo::getStatus, request.getStatus())
                        .eq(StringUtils.hasText(request.getCaseNo()), CaseInfo::getCaseNo, request.getCaseNo())
                        .like(StringUtils.hasText(request.getKeyword()), CaseInfo::getCaseTitle, request.getKeyword())
                        .orderByDesc(CaseInfo::getCreatedAt));
        Page<CaseListVO> result = Page.of(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toListVO).toList());
        return result;
    }

    @Override
    public CaseDetailVO detail(Long id) {
        CaseInfo caseInfo = requireCase(id);
        return toDetailVO(caseInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseDetailVO transition(Long id, CaseTransitionRequest request) {
        CaseInfo caseInfo = requireCase(id);
        CaseStatus fromStatus = CaseStatus.valueOf(caseInfo.getStatus());
        CaseAction action = CaseAction.valueOf(request.getAction());
        Set<CaseRole> currentRoles = casePermissionChecker.currentCaseRoles();
        CaseTransition transition = caseTransitionValidator.validate(fromStatus, action, currentRoles);

        applyTimeFields(caseInfo, transition.getToStatus());
        caseInfo.setStatus(transition.getToStatus().name());
        caseInfoMapper.updateById(caseInfo);
        recordProcess(caseInfo.getId(), fromStatus, transition.getToStatus(), action.name(), request.getComment(), currentRoles.iterator().next().name());
        return detail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseDetailVO assignJudge(Long id, AssignJudgeRequest request) {
        CaseInfo caseInfo = requireCase(id);
        if (!casePermissionChecker.hasPermission("case:assign") && !casePermissionChecker.currentCaseRoles().contains(CaseRole.ADMIN)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前用户无权指派法官");
        }
        caseInfo.setJudgeId(request.getJudgeId());
        caseInfo.setClerkId(request.getClerkId());
        caseInfoMapper.updateById(caseInfo);
        recordProcess(caseInfo.getId(), CaseStatus.valueOf(caseInfo.getStatus()), CaseStatus.valueOf(caseInfo.getStatus()),
                "ASSIGN_JUDGE", request.getComment(), bestRole().name());
        return detail(id);
    }

    /**
     * 根据状态更新关键时间字段。
     *
     * @param caseInfo 案件实体
     * @param status   目标状态
     */
    private void applyTimeFields(CaseInfo caseInfo, CaseStatus status) {
        LocalDateTime now = LocalDateTime.now();
        if (status == CaseStatus.SUBMITTED) {
            caseInfo.setFilingTime(now);
        } else if (status == CaseStatus.ACCEPTED) {
            caseInfo.setAcceptedTime(now);
        } else if (status == CaseStatus.PAYMENT_PENDING && caseInfo.getPaymentDeadline() == null) {
            caseInfo.setPaymentDeadline(now.plusDays(7));
        } else if (status == CaseStatus.EVIDENCE_SUBMITTING && caseInfo.getEvidenceDeadline() == null) {
            caseInfo.setEvidenceDeadline(now.plusDays(15));
        } else if (status == CaseStatus.CLOSED || status == CaseStatus.WITHDRAWN || status == CaseStatus.TERMINATED) {
            caseInfo.setClosedTime(now);
        }
    }

    /**
     * 查询案件，不存在则抛出业务异常。
     *
     * @param id 案件主键
     * @return 案件实体
     */
    private CaseInfo requireCase(Long id) {
        CaseInfo caseInfo = caseInfoMapper.selectById(id);
        if (caseInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "案件不存在");
        }
        return caseInfo;
    }

    /**
     * 记录案件流程。
     *
     * @param caseId       案件主键
     * @param fromStatus   原状态
     * @param toStatus     目标状态
     * @param action       动作
     * @param comment      说明
     * @param operatorRole 操作角色
     */
    private void recordProcess(Long caseId, CaseStatus fromStatus, CaseStatus toStatus, String action, String comment, String operatorRole) {
        TdepUserPrincipal principal = casePermissionChecker.currentPrincipal();
        CaseProcess process = new CaseProcess();
        process.setCaseId(caseId);
        process.setFromStatus(fromStatus == null ? null : fromStatus.name());
        process.setToStatus(toStatus.name());
        process.setAction(action);
        process.setOperatorId(principal.getUserId());
        process.setOperatorRole(operatorRole);
        process.setComment(comment);
        process.setProcessTime(LocalDateTime.now());
        caseProcessMapper.insert(process);
    }

    /**
     * 生成案件编号。
     *
     * @return 案件编号
     */
    private String generateCaseNo() {
        return "TDEP-" + LocalDate.now().format(CASE_NO_DATE_FORMATTER) + "-" + System.nanoTime();
    }

    /**
     * 选择当前用户最优展示角色。
     *
     * @return 案件角色
     */
    private CaseRole bestRole() {
        Set<CaseRole> roles = casePermissionChecker.currentCaseRoles();
        if (roles.contains(CaseRole.ADMIN)) {
            return CaseRole.ADMIN;
        }
        if (roles.contains(CaseRole.JUDGE)) {
            return CaseRole.JUDGE;
        }
        if (roles.contains(CaseRole.CLERK)) {
            return CaseRole.CLERK;
        }
        return CaseRole.PARTY;
    }

    /**
     * 转换案件列表视图。
     *
     * @param caseInfo 案件实体
     * @return 列表视图
     */
    private CaseListVO toListVO(CaseInfo caseInfo) {
        return CaseListVO.builder()
                .id(caseInfo.getId())
                .caseNo(caseInfo.getCaseNo())
                .caseTitle(caseInfo.getCaseTitle())
                .caseType(caseInfo.getCaseType())
                .causeAction(caseInfo.getCauseAction())
                .claimAmount(caseInfo.getClaimAmount())
                .status(caseInfo.getStatus())
                .judgeId(caseInfo.getJudgeId())
                .createdBy(caseInfo.getCreatedBy())
                .createdAt(caseInfo.getCreatedAt())
                .build();
    }

    /**
     * 转换案件详情视图。
     *
     * @param caseInfo 案件实体
     * @return 详情视图
     */
    private CaseDetailVO toDetailVO(CaseInfo caseInfo) {
        Long caseId = caseInfo.getId();
        return CaseDetailVO.builder()
                .id(caseInfo.getId())
                .caseNo(caseInfo.getCaseNo())
                .caseTitle(caseInfo.getCaseTitle())
                .caseType(caseInfo.getCaseType())
                .causeAction(caseInfo.getCauseAction())
                .procedureType(caseInfo.getProcedureType())
                .claimRequest(caseInfo.getClaimRequest())
                .claimAmount(caseInfo.getClaimAmount())
                .courtName(caseInfo.getCourtName())
                .status(caseInfo.getStatus())
                .judgeId(caseInfo.getJudgeId())
                .clerkId(caseInfo.getClerkId())
                .filingTime(caseInfo.getFilingTime())
                .acceptedTime(caseInfo.getAcceptedTime())
                .paymentDeadline(caseInfo.getPaymentDeadline())
                .evidenceDeadline(caseInfo.getEvidenceDeadline())
                .closedTime(caseInfo.getClosedTime())
                .createdBy(caseInfo.getCreatedBy())
                .createdAt(caseInfo.getCreatedAt())
                .parties(listParties(caseId))
                .evidences(listEvidences(caseId))
                .trials(listTrials(caseId))
                .judgements(listJudgements(caseId))
                .processes(listProcesses(caseId))
                .build();
    }

    private List<CasePartyVO> listParties(Long caseId) {
        return casePartyMapper.selectList(new LambdaQueryWrapper<CaseParty>().eq(CaseParty::getCaseId, caseId))
                .stream()
                .map(party -> CasePartyVO.builder()
                        .id(party.getId())
                        .partyType(party.getPartyType())
                        .partyName(party.getPartyName())
                        .identityType(party.getIdentityType())
                        .identityNo(party.getIdentityNo())
                        .contactPhone(party.getContactPhone())
                        .contactAddress(party.getContactAddress())
                        .userId(party.getUserId())
                        .build())
                .toList();
    }

    private List<CaseEvidenceVO> listEvidences(Long caseId) {
        return caseEvidenceMapper.selectList(new LambdaQueryWrapper<CaseEvidence>().eq(CaseEvidence::getCaseId, caseId))
                .stream()
                .map(evidence -> CaseEvidenceVO.builder()
                        .id(evidence.getId())
                        .evidenceName(evidence.getEvidenceName())
                        .evidenceType(evidence.getEvidenceType())
                        .fileUrl(evidence.getFileUrl())
                        .fileHash(evidence.getFileHash())
                        .description(evidence.getDescription())
                        .uploadedBy(evidence.getUploadedBy())
                        .uploadedAt(evidence.getUploadedAt())
                        .build())
                .toList();
    }

    private List<CaseTrialVO> listTrials(Long caseId) {
        return caseTrialMapper.selectList(new LambdaQueryWrapper<CaseTrial>().eq(CaseTrial::getCaseId, caseId))
                .stream()
                .map(trial -> CaseTrialVO.builder()
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
                        .build())
                .toList();
    }

    private List<CaseJudgementVO> listJudgements(Long caseId) {
        return caseJudgementMapper.selectList(new LambdaQueryWrapper<CaseJudgement>().eq(CaseJudgement::getCaseId, caseId))
                .stream()
                .map(judgement -> CaseJudgementVO.builder()
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
                        .build())
                .toList();
    }

    private List<CaseProcessVO> listProcesses(Long caseId) {
        return caseProcessMapper.selectList(new LambdaQueryWrapper<CaseProcess>().eq(CaseProcess::getCaseId, caseId))
                .stream()
                .sorted(Comparator.comparing(CaseProcess::getProcessTime))
                .map(process -> CaseProcessVO.builder()
                        .id(process.getId())
                        .fromStatus(process.getFromStatus())
                        .toStatus(process.getToStatus())
                        .action(process.getAction())
                        .operatorId(process.getOperatorId())
                        .operatorRole(process.getOperatorRole())
                        .comment(process.getComment())
                        .processTime(process.getProcessTime())
                        .build())
                .toList();
    }
}
