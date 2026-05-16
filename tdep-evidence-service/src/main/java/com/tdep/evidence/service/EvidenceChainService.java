package com.tdep.evidence.service;

import com.tdep.evidence.entity.EvidenceChain;
import com.tdep.evidence.enums.EvidenceChainEventType;

import java.util.List;

/**
 * 证据链服务。
 */
public interface EvidenceChainService {

    /**
     * 追加证据链事件。
     *
     * @param evidenceId 证据主键
     * @param eventType  事件类型
     * @param remark     备注
     * @return 证据链记录
     */
    EvidenceChain append(Long evidenceId, EvidenceChainEventType eventType, String remark);

    /**
     * 查询证据链。
     *
     * @param evidenceId 证据主键
     * @return 证据链记录
     */
    List<EvidenceChain> listByEvidenceId(Long evidenceId);

    /**
     * 校验证据链完整性。
     *
     * @param evidenceId 证据主键
     * @return true 表示完整
     */
    boolean verifyChain(Long evidenceId);
}
