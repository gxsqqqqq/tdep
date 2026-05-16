package com.tdep.casecenter.workflow;

import com.tdep.casecenter.enums.CaseAction;
import com.tdep.casecenter.enums.CaseRole;
import com.tdep.casecenter.enums.CaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * 案件状态流转定义，描述原状态、动作、目标状态和允许操作角色。
 */
@Getter
@Builder
public class CaseTransition {

    /**
     * 原状态。
     */
    private final CaseStatus fromStatus;

    /**
     * 流转动作。
     */
    private final CaseAction action;

    /**
     * 目标状态。
     */
    private final CaseStatus toStatus;

    /**
     * 允许触发该流转的案件角色集合。
     */
    private final Set<CaseRole> allowedRoles;
}
