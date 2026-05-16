CREATE TABLE IF NOT EXISTS document_template
(
    id              BIGINT       NOT NULL COMMENT 'template id',
    template_code   VARCHAR(64)  NOT NULL COMMENT 'template code',
    document_type   VARCHAR(64)  NOT NULL COMMENT 'document type',
    template_name   VARCHAR(128) NOT NULL COMMENT 'template name',
    version         INT          NOT NULL COMMENT 'template version',
    bucket_name     VARCHAR(128) NOT NULL COMMENT 'template bucket',
    object_key      VARCHAR(500) NOT NULL COMMENT 'template object key',
    engine_type     VARCHAR(32)  NOT NULL COMMENT 'template engine',
    enabled         TINYINT      NOT NULL DEFAULT 1 COMMENT 'enabled flag',
    current_version TINYINT      NOT NULL DEFAULT 0 COMMENT 'current version flag',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT 'logic delete flag',
    PRIMARY KEY (id),
    UNIQUE KEY uk_document_template_type_version (document_type, version),
    KEY idx_document_template_current (document_type, enabled, current_version)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='document template';

CREATE TABLE IF NOT EXISTS document_file
(
    id                BIGINT       NOT NULL COMMENT 'document id',
    case_id           BIGINT       NOT NULL COMMENT 'case id',
    document_no       VARCHAR(128) NOT NULL COMMENT 'document number',
    document_type     VARCHAR(64)  NOT NULL COMMENT 'document type',
    title             VARCHAR(255) NOT NULL COMMENT 'document title',
    status            VARCHAR(32)  NOT NULL COMMENT 'document status',
    template_id       BIGINT       NULL COMMENT 'template id',
    word_bucket       VARCHAR(128) NULL COMMENT 'word bucket',
    word_object_key   VARCHAR(500) NULL COMMENT 'word object key',
    pdf_bucket        VARCHAR(128) NULL COMMENT 'pdf bucket',
    pdf_object_key    VARCHAR(500) NULL COMMENT 'pdf object key',
    signed_bucket     VARCHAR(128) NULL COMMENT 'signed pdf bucket',
    signed_object_key VARCHAR(500) NULL COMMENT 'signed pdf object key',
    file_hash         VARCHAR(128) NULL COMMENT 'current file hash',
    created_by        BIGINT       NOT NULL COMMENT 'creator user id',
    reviewed_by       BIGINT       NULL COMMENT 'reviewer user id',
    signed_by         BIGINT       NULL COMMENT 'signer user id',
    generated_at      DATETIME     NULL COMMENT 'generated time',
    reviewed_at       DATETIME     NULL COMMENT 'reviewed time',
    signed_at         DATETIME     NULL COMMENT 'signed time',
    effective_at      DATETIME     NULL COMMENT 'effective time',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    deleted           TINYINT      NOT NULL DEFAULT 0 COMMENT 'logic delete flag',
    PRIMARY KEY (id),
    UNIQUE KEY uk_document_file_no (document_no),
    KEY idx_document_file_case_type (case_id, document_type),
    KEY idx_document_file_status (status),
    KEY idx_document_file_created_by (created_by)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='legal document file';

CREATE TABLE IF NOT EXISTS document_sign
(
    id              BIGINT       NOT NULL COMMENT 'sign id',
    document_id     BIGINT       NOT NULL COMMENT 'document id',
    signer_id       BIGINT       NULL COMMENT 'signer user id',
    signer_name     VARCHAR(64)  NOT NULL COMMENT 'signer name',
    sign_type       VARCHAR(32)  NOT NULL COMMENT 'sign type',
    cert_serial_no  VARCHAR(128) NULL COMMENT 'certificate serial number',
    signature_field VARCHAR(128) NULL COMMENT 'pdf signature field',
    before_hash     VARCHAR(128) NULL COMMENT 'hash before signing',
    after_hash      VARCHAR(128) NULL COMMENT 'hash after signing',
    timestamp_token TEXT         NULL COMMENT 'timestamp token',
    verify_status   VARCHAR(32)  NOT NULL COMMENT 'verify status',
    signed_at       DATETIME     NOT NULL COMMENT 'signed time',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT 'logic delete flag',
    PRIMARY KEY (id),
    KEY idx_document_sign_document_id (document_id),
    KEY idx_document_sign_signer_id (signer_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='document sign record';

CREATE TABLE IF NOT EXISTS document_archive
(
    id              BIGINT       NOT NULL COMMENT 'archive id',
    document_id     BIGINT       NOT NULL COMMENT 'document id',
    case_id         BIGINT       NOT NULL COMMENT 'case id',
    archive_no      VARCHAR(128) NOT NULL COMMENT 'archive number',
    bucket_name     VARCHAR(128) NOT NULL COMMENT 'archive bucket',
    object_key      VARCHAR(500) NOT NULL COMMENT 'archive object key',
    file_hash       VARCHAR(128) NOT NULL COMMENT 'archive file hash',
    file_size       BIGINT       NOT NULL COMMENT 'archive file size',
    archived_by     BIGINT       NULL COMMENT 'archiver user id',
    archived_at     DATETIME     NOT NULL COMMENT 'archive time',
    retention_years INT          NOT NULL DEFAULT 30 COMMENT 'retention years',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT 'logic delete flag',
    PRIMARY KEY (id),
    UNIQUE KEY uk_document_archive_no (archive_no),
    KEY idx_document_archive_document_id (document_id),
    KEY idx_document_archive_case_id (case_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='document archive';

CREATE TABLE IF NOT EXISTS document_operation_log
(
    id             BIGINT        NOT NULL COMMENT 'log id',
    document_id    BIGINT        NULL COMMENT 'document id',
    case_id        BIGINT        NULL COMMENT 'case id',
    operation_type VARCHAR(64)   NOT NULL COMMENT 'operation type',
    operator_id    BIGINT        NULL COMMENT 'operator user id',
    operator_role  VARCHAR(255)  NULL COMMENT 'operator role',
    before_status  VARCHAR(32)   NULL COMMENT 'before status',
    after_status   VARCHAR(32)   NULL COMMENT 'after status',
    result         VARCHAR(32)   NOT NULL COMMENT 'operation result',
    ip_address     VARCHAR(64)   NULL COMMENT 'request ip',
    remark         VARCHAR(1000) NULL COMMENT 'remark',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    deleted        TINYINT       NOT NULL DEFAULT 0 COMMENT 'logic delete flag',
    PRIMARY KEY (id),
    KEY idx_document_log_document_id (document_id),
    KEY idx_document_log_case_id (case_id),
    KEY idx_document_log_operator_id (operator_id),
    KEY idx_document_log_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='document operation log';
