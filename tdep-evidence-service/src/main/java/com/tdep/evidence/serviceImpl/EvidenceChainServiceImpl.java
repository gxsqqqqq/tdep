package com.tdep.evidence.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.evidence.entity.EvidenceChain;
import com.tdep.evidence.enums.EvidenceChainEventType;
import com.tdep.evidence.hash.HashService;
import com.tdep.evidence.mapper.EvidenceChainMapper;
import com.tdep.evidence.security.EvidencePermissionChecker;
import com.tdep.evidence.service.EvidenceChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 证据链服务实现。
 */
@Service
@RequiredArgsConstructor
public class EvidenceChainServiceImpl implements EvidenceChainService {

    private static final String GENESIS_HASH = "GENESIS";

    private final EvidenceChainMapper evidenceChainMapper;

    private final HashService hashService;

    private final EvidencePermissionChecker permissionChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceChain append(Long evidenceId, EvidenceChainEventType eventType, String remark) {
        TdepUserPrincipal principal = permissionChecker.currentPrincipal();
        EvidenceChain latest = latest(evidenceId);
        int nextSeq = latest == null ? 1 : latest.getChainSeq() + 1;
        String previousHash = latest == null ? GENESIS_HASH : latest.getCurrentChainHash();
        LocalDateTime now = LocalDateTime.now();
        String timestampValue = "TDEP-TS-" + now;
        String eventData = evidenceId + "|" + eventType.name() + "|" + principal.getUserId() + "|" + now + "|" + remark;
        String eventDataHash = hashService.sha256Hex(eventData);
        String currentHash = hashService.sha256Hex(previousHash + "|" + eventDataHash);

        EvidenceChain chain = new EvidenceChain();
        chain.setEvidenceId(evidenceId);
        chain.setChainSeq(nextSeq);
        chain.setEventType(eventType.name());
        chain.setEventDataHash(eventDataHash);
        chain.setPreviousChainHash(previousHash);
        chain.setCurrentChainHash(currentHash);
        chain.setOperatorId(principal.getUserId());
        chain.setOperatorRole(permissionChecker.currentRole());
        chain.setEventTime(now);
        chain.setTimestampValue(timestampValue);
        chain.setRemark(remark);
        evidenceChainMapper.insert(chain);
        return chain;
    }

    @Override
    public List<EvidenceChain> listByEvidenceId(Long evidenceId) {
        return evidenceChainMapper.selectList(new LambdaQueryWrapper<EvidenceChain>()
                        .eq(EvidenceChain::getEvidenceId, evidenceId)
                        .orderByAsc(EvidenceChain::getChainSeq))
                .stream()
                .sorted(Comparator.comparing(EvidenceChain::getChainSeq))
                .toList();
    }

    @Override
    public boolean verifyChain(Long evidenceId) {
        List<EvidenceChain> chains = listByEvidenceId(evidenceId);
        String previous = GENESIS_HASH;
        for (EvidenceChain chain : chains) {
            if (!previous.equals(chain.getPreviousChainHash())) {
                return false;
            }
            String expected = hashService.sha256Hex(previous + "|" + chain.getEventDataHash());
            if (!expected.equals(chain.getCurrentChainHash())) {
                return false;
            }
            previous = chain.getCurrentChainHash();
        }
        return true;
    }

    /**
     * 查询最新证据链记录。
     *
     * @param evidenceId 证据主键
     * @return 最新记录
     */
    private EvidenceChain latest(Long evidenceId) {
        List<EvidenceChain> chains = evidenceChainMapper.selectList(new LambdaQueryWrapper<EvidenceChain>()
                .eq(EvidenceChain::getEvidenceId, evidenceId)
                .orderByDesc(EvidenceChain::getChainSeq)
                .last("LIMIT 1"));
        if (chains.size() > 1) {
            throw new BusinessException(ResultCode.CONFLICT, "证据链序号异常");
        }
        return chains.isEmpty() ? null : chains.get(0);
    }
}
