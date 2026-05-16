-- tdep-case-service 案件管理表结构初始化脚本。
CREATE TABLE IF NOT EXISTS case_info
(
    id                BIGINT         NOT NULL COMMENT '案件主键',
    case_no           VARCHAR(64)    NOT NULL COMMENT '案号',
    case_title        VARCHAR(200)   NOT NULL COMMENT '案件标题',
    case_type         VARCHAR(32)    NOT NULL COMMENT '案件类型',
    cause_action      VARCHAR(100)   NOT NULL COMMENT '案由',
    procedure_type    VARCHAR(32)    NOT NULL COMMENT '程序类型',
    claim_request     TEXT           NULL COMMENT '诉讼请求',
    claim_amount      DECIMAL(18, 2) NULL COMMENT '标的金额',
    court_name        VARCHAR(100)   NULL COMMENT '受理法院',
    status            VARCHAR(32)    NOT NULL COMMENT '案件状态',
    judge_id          BIGINT         NULL COMMENT '承办法官编号',
    clerk_id          BIGINT         NULL COMMENT '书记员编号',
    filing_time       DATETIME       NULL COMMENT '立案提交时间',
    accepted_time     DATETIME       NULL COMMENT '受理时间',
    payment_deadline  DATETIME       NULL COMMENT '缴费截止时间',
    evidence_deadline DATETIME       NULL COMMENT '举证截止时间',
    closed_time       DATETIME       NULL COMMENT '结案时间',
    created_by        BIGINT         NOT NULL COMMENT '创建人编号',
    created_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_case_info_case_no (case_no),
    KEY idx_case_info_status (status),
    KEY idx_case_info_created_by (created_by),
    KEY idx_case_info_judge_id (judge_id),
    KEY idx_case_info_filing_time (filing_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件主表';

CREATE TABLE IF NOT EXISTS case_party
(
    id              BIGINT       NOT NULL COMMENT '主键',
    case_id         BIGINT       NOT NULL COMMENT '案件主键',
    party_type      VARCHAR(32)  NOT NULL COMMENT '当事人类型',
    party_name      VARCHAR(100) NOT NULL COMMENT '当事人名称',
    identity_type   VARCHAR(32)  NULL COMMENT '证件类型',
    identity_no     VARCHAR(64)  NULL COMMENT '证件号码',
    contact_phone   VARCHAR(20)  NULL COMMENT '联系电话',
    contact_address VARCHAR(255) NULL COMMENT '联系地址',
    user_id         BIGINT       NULL COMMENT '绑定平台用户编号',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_case_party_case_id (case_id),
    KEY idx_case_party_user_id (user_id),
    KEY idx_case_party_identity_no (identity_no)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件当事人表';

CREATE TABLE IF NOT EXISTS case_process
(
    id            BIGINT       NOT NULL COMMENT '主键',
    case_id       BIGINT       NOT NULL COMMENT '案件主键',
    from_status   VARCHAR(32)  NULL COMMENT '原状态',
    to_status     VARCHAR(32)  NOT NULL COMMENT '目标状态',
    action        VARCHAR(64)  NOT NULL COMMENT '流程动作',
    operator_id   BIGINT       NOT NULL COMMENT '操作人编号',
    operator_role VARCHAR(32)  NOT NULL COMMENT '操作角色',
    comment       VARCHAR(500) NULL COMMENT '处理意见',
    process_time  DATETIME     NOT NULL COMMENT '处理时间',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_case_process_case_id (case_id),
    KEY idx_case_process_time (process_time),
    KEY idx_case_process_operator (operator_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件流程记录表';

CREATE TABLE IF NOT EXISTS case_evidence
(
    id            BIGINT       NOT NULL COMMENT '主键',
    case_id       BIGINT       NOT NULL COMMENT '案件主键',
    evidence_name VARCHAR(200) NOT NULL COMMENT '证据名称',
    evidence_type VARCHAR(64)  NOT NULL COMMENT '证据类型',
    file_url      VARCHAR(500) NOT NULL COMMENT '文件地址',
    file_hash     VARCHAR(128) NULL COMMENT '文件哈希',
    description   VARCHAR(500) NULL COMMENT '证据说明',
    uploaded_by   BIGINT       NOT NULL COMMENT '上传人编号',
    uploaded_at   DATETIME     NOT NULL COMMENT '上传时间',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_case_evidence_case_id (case_id),
    KEY idx_case_evidence_uploaded_by (uploaded_by),
    KEY idx_case_evidence_hash (file_hash)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件证据表';

CREATE TABLE IF NOT EXISTS case_trial
(
    id             BIGINT       NOT NULL COMMENT '庭审主键',
    case_id        BIGINT       NOT NULL COMMENT '案件主键',
    trial_no       VARCHAR(64)  NOT NULL COMMENT '庭审编号',
    trial_type     VARCHAR(32)  NOT NULL COMMENT '庭审类型',
    trial_time     DATETIME     NOT NULL COMMENT '开庭时间',
    trial_location VARCHAR(255) NOT NULL COMMENT '开庭地点或线上地址',
    judge_id       BIGINT       NOT NULL COMMENT '承办法官编号',
    clerk_id       BIGINT       NULL COMMENT '书记员编号',
    trial_status   VARCHAR(32)  NOT NULL COMMENT '庭审状态',
    trial_record   TEXT         NULL COMMENT '庭审记录内容',
    record_url     VARCHAR(500) NULL COMMENT '庭审记录文件地址',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_case_trial_no (trial_no),
    KEY idx_case_trial_case_id (case_id),
    KEY idx_case_trial_time (trial_time),
    KEY idx_case_trial_judge_id (judge_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件庭审表';

CREATE TABLE IF NOT EXISTS case_judgement
(
    id              BIGINT       NOT NULL COMMENT '主键',
    case_id         BIGINT       NOT NULL COMMENT '案件主键',
    judgement_no    VARCHAR(64)  NOT NULL COMMENT '文书编号',
    judgement_type  VARCHAR(32)  NOT NULL COMMENT '文书类型',
    title           VARCHAR(200) NOT NULL COMMENT '文书标题',
    content_summary TEXT         NULL COMMENT '裁判摘要',
    document_url    VARCHAR(500) NULL COMMENT '文书文件地址',
    judge_id        BIGINT       NOT NULL COMMENT '出具法官编号',
    publish_time    DATETIME     NOT NULL COMMENT '发布时间',
    effective_time  DATETIME     NULL COMMENT '生效时间',
    appeal_deadline DATETIME     NULL COMMENT '上诉截止时间',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_case_judgement_no (judgement_no),
    KEY idx_case_judgement_case_id (case_id),
    KEY idx_case_judgement_effective_time (effective_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='案件裁判文书表';
