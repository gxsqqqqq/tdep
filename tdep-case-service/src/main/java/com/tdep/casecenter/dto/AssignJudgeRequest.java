package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 指派法官请求参数。
 */
@Data
public class AssignJudgeRequest {

    /**
     * 承办法官用户编号。
     */
    @NotNull(message = "不能为空")
    private Long judgeId;

    /**
     * 书记员用户编号。
     */
    private Long clerkId;

    /**
     * 指派说明。
     */
    private String comment;
}
