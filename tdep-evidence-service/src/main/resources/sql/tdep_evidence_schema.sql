-- tdep-evidence-service 完整可信电子证据建库脚本，可直接在 MySQL 中执行。
CREATE DATABASE IF NOT EXISTS tdep_evidence
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE tdep_evidence;

CREATE TABLE IF NOT EXISTS evidence_file
(
    id                 BIGINT       NOT NULL COMMENT '证据主键',
    evidence_no        VARCHAR(64)  NOT NULL COMMENT '证据编号',
    case_id            BIGINT       NOT NULL COMMENT '关联案件编号',
    uploader_id        BIGINT       NOT NULL COMMENT '上传人编号',
    file_name          VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_ext           VARCHAR(32)  NULL COMMENT '文件扩展名',
    content_type       VARCHAR(128) NULL COMMENT 'MIME类型',
    file_size          BIGINT       NOT NULL COMMENT '文件大小，单位字节',
    bucket_name        VARCHAR(128) NOT NULL COMMENT '原始文件bucket',
    object_key         VARCHAR(500) NOT NULL COMMENT '原始文件object key',
    sealed_bucket_name VARCHAR(128) NULL COMMENT '固化PDF bucket',
    sealed_object_key  VARCHAR(500) NULL COMMENT '固化PDF object key',
    status             VARCHAR(32)  NOT NULL COMMENT '证据状态',
    description        VARCHAR(500) NULL COMMENT '证据说明',
    uploaded_at        DATETIME     NOT NULL COMMENT '上传时间',
    sealed_at          DATETIME     NULL COMMENT '固化时间',
    deleted_at         DATETIME     NULL COMMENT '删除时间',
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted            TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_evidence_file_no (evidence_no),
    KEY idx_evidence_file_case_id (case_id),
    KEY idx_evidence_file_uploader_id (uploader_id),
    KEY idx_evidence_file_status (status),
    KEY idx_evidence_file_uploaded_at (uploaded_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='电子证据文件表';

CREATE TABLE IF NOT EXISTS evidence_hash
(
    id               BIGINT      NOT NULL COMMENT '主键',
    evidence_id      BIGINT      NOT NULL COMMENT '证据主键',
    hash_algorithm   VARCHAR(32) NOT NULL COMMENT 'Hash算法',
    hash_value       CHAR(64)    NOT NULL COMMENT 'Hash值',
    file_fingerprint CHAR(64)    NOT NULL COMMENT '文件唯一指纹',
    file_size        BIGINT      NOT NULL COMMENT '文件大小',
    calculated_at    DATETIME    NOT NULL COMMENT 'Hash计算时间',
    verified_at      DATETIME    NULL COMMENT '最近校验时间',
    verify_result    TINYINT     NULL COMMENT '最近校验结果：1通过，0失败',
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_evidence_hash_evidence_id (evidence_id),
    KEY idx_evidence_hash_value (hash_value),
    KEY idx_evidence_hash_fingerprint (file_fingerprint)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='电子证据Hash表';

CREATE TABLE IF NOT EXISTS evidence_chain
(
    id                  BIGINT       NOT NULL COMMENT '主键',
    evidence_id         BIGINT       NOT NULL COMMENT '证据主键',
    chain_seq           INT          NOT NULL COMMENT '链序号',
    event_type          VARCHAR(64)  NOT NULL COMMENT '事件类型',
    event_data_hash     CHAR(64)     NOT NULL COMMENT '事件数据Hash',
    previous_chain_hash CHAR(64)     NULL COMMENT '上一条链Hash',
    current_chain_hash  CHAR(64)     NOT NULL COMMENT '当前链Hash',
    operator_id         BIGINT       NOT NULL COMMENT '操作人编号',
    operator_role       VARCHAR(32)  NOT NULL COMMENT '操作角色',
    event_time          DATETIME     NOT NULL COMMENT '事件时间',
    timestamp_value     VARCHAR(128) NOT NULL COMMENT '可信时间戳值',
    remark              VARCHAR(500) NULL COMMENT '备注',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted             TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_evidence_chain_seq (evidence_id, chain_seq),
    KEY idx_evidence_chain_evidence_id (evidence_id),
    KEY idx_evidence_chain_hash (current_chain_hash),
    KEY idx_evidence_chain_event_time (event_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='电子证据链表';

CREATE TABLE IF NOT EXISTS evidence_operation_log
(
    id               BIGINT        NOT NULL COMMENT '主键',
    evidence_id      BIGINT        NULL COMMENT '证据主键',
    operation_type   VARCHAR(64)   NOT NULL COMMENT '操作类型',
    operator_id      BIGINT        NOT NULL COMMENT '操作人编号',
    operator_role    VARCHAR(32)   NOT NULL COMMENT '操作角色',
    request_ip       VARCHAR(64)   NULL COMMENT '请求IP',
    user_agent       VARCHAR(500)  NULL COMMENT 'User-Agent',
    operation_result VARCHAR(32)   NOT NULL COMMENT '操作结果',
    operation_detail VARCHAR(1000) NULL COMMENT '操作详情',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted          TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_evidence_log_evidence_id (evidence_id),
    KEY idx_evidence_log_operator_id (operator_id),
    KEY idx_evidence_log_operation_type (operation_type),
    KEY idx_evidence_log_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='电子证据操作日志表';
