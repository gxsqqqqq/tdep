package com.tdep.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notify_message")
public class NotifyMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String notifyType;

    private String title;

    private String content;

    private String status;

    private LocalDateTime readAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
