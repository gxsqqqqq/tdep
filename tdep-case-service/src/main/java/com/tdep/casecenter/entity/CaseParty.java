package com.tdep.casecenter.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 案件当事人实体，对应 case_party 表。
 */
@Data
@TableName("case_party")
public class CaseParty implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     */
    @TableId
    private Long id;

    /**
     * 案件主键。
     */
    private Long caseId;

    /**
     * 当事人类型。
     */
    private String partyType;

    /**
     * 当事人名称。
     */
    private String partyName;

    /**
     * 证件类型。
     */
    private String identityType;

    /**
     * 证件号码。
     */
    private String identityNo;

    /**
     * 联系电话。
     */
    private String contactPhone;

    /**
     * 联系地址。
     */
    private String contactAddress;

    /**
     * 绑定平台用户编号。
     */
    private Long userId;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记。
     */
    @TableLogic
    private Integer deleted;
}
