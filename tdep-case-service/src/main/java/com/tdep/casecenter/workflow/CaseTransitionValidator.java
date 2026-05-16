package com.tdep.casecenter.workflow;

import com.tdep.casecenter.enums.CaseAction;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.CaseStatus;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 案件状态流转校验器，负责非法状态和非法角色拦截。
 */
@Component
@RequiredArgsConstructor
public class CaseTransitionValidator {

    private final CaseStateMachine caseStateMachine;

    /**
     * 校验并返回状态流转定义。
     *
     * @param fromStatus   原状态
     * @param action       流转动作
     * @param currentRoles 当前用户案件角色
     * @return 合法状态流转定义
     */
    public CaseTransition validate(CaseStatus fromStatus, CaseAction action, Set<CaseRole> currentRoles) {
        if (fromStatus.terminal()) {
            throw new BusinessException(ResultCode.CONFLICT, "终态案件不允许继续流转");
        }
        CaseTransition transition = caseStateMachine.findTransition(fromStatus, action)
                .orElseThrow(() -> new BusinessException(ResultCode.CONFLICT,
                        "非法案件状态流转：" + fromStatus.name() + " -> " + action.name()));
        boolean roleAllowed = currentRoles.stream().anyMatch(transition.getAllowedRoles()::contains);
        if (!roleAllowed) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权触发该案件状态流转");
        }
        return transition;
    }
}
