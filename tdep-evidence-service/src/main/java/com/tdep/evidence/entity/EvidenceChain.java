package com.tdep.evidence.entity;

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
 * 电子证据链实体，对应 evidence_chain 表。
 */
@Data
@TableName("evidence_chain")
public class EvidenceChain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     */
    @TableId
    private Long id;

    /**
     * 证据主键。
     */
    private Long evidenceId;

    /**
     * 链序号。
     */
    private Integer chainSeq;

    /**
     * 事件类型。
     */
    private String eventType;

    /**
     * 事件数据 Hash。
     */
    private String eventDataHash;

    /**
     * 上一条链 Hash。
     */
    private String previousChainHash;

    /**
     * 当前链 Hash。
     */
    private String currentChainHash;

    /**
     * 操作人编号。
     */
    private Long operatorId;

    /**
     * 操作角色。
     */
    private String operatorRole;

    /**
     * 事件时间。
     */
    private LocalDateTime eventTime;

    /**
     * 时间戳值。
     */
    private String timestampValue;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 逻辑删除标记。
     */
    @TableLogic
    private Integer deleted;
}
