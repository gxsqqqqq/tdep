CREATE TABLE IF NOT EXISTS notify_message
(
    id          BIGINT       NOT NULL COMMENT '主键ID',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    notify_type VARCHAR(50)  NOT NULL COMMENT '通知类型：CASE/EVIDENCE/DOCUMENT/SYSTEM',
    title       VARCHAR(200) NOT NULL COMMENT '消息标题',
    content     TEXT         NOT NULL COMMENT '消息内容',
    status      VARCHAR(20)  NOT NULL DEFAULT 'UNREAD' COMMENT '状态：UNREAD/READ',
    read_at     DATETIME              DEFAULT NULL COMMENT '阅读时间',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '通知消息表';
