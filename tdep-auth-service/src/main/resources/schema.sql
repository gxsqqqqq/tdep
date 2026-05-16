-- tdep-auth-service RBAC 表结构初始化脚本。
CREATE TABLE IF NOT EXISTS sys_user
(
    id              BIGINT       NOT NULL COMMENT '用户主键',
    username        VARCHAR(32)  NOT NULL COMMENT '登录用户名',
    password        VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    nickname        VARCHAR(64)  NOT NULL COMMENT '用户昵称',
    phone           VARCHAR(20)  NULL COMMENT '手机号码',
    email           VARCHAR(128) NULL COMMENT '邮箱地址',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1启用，0禁用',
    last_login_time DATETIME     NULL COMMENT '最后登录时间',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username, deleted),
    KEY idx_sys_user_phone (phone),
    KEY idx_sys_user_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

CREATE TABLE IF NOT EXISTS sys_role
(
    id          BIGINT      NOT NULL COMMENT '角色主键',
    role_code   VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name   VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) NULL COMMENT '角色说明',
    status      TINYINT     NOT NULL DEFAULT 1 COMMENT '角色状态：1启用，0禁用',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_role_code (role_code, deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色表';

CREATE TABLE IF NOT EXISTS sys_permission
(
    id              BIGINT       NOT NULL COMMENT '权限主键',
    permission_code VARCHAR(128) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(64)  NOT NULL COMMENT '权限名称',
    permission_type VARCHAR(20)  NOT NULL COMMENT '权限类型：MENU菜单，BUTTON按钮，API接口',
    resource_path   VARCHAR(255) NULL COMMENT '资源路径',
    http_method     VARCHAR(16)  NULL COMMENT 'HTTP方法',
    description     VARCHAR(255) NULL COMMENT '权限说明',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '权限状态：1启用，0禁用',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_permission_code (permission_code, deleted),
    KEY idx_sys_permission_resource (resource_path, http_method)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='权限表';

CREATE TABLE IF NOT EXISTS sys_user_role
(
    id         BIGINT   NOT NULL COMMENT '关联主键',
    user_id    BIGINT   NOT NULL COMMENT '用户主键',
    role_id    BIGINT   NOT NULL COMMENT '角色主键',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted    TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_role (user_id, role_id, deleted),
    KEY idx_sys_user_role_role_id (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_role_permission
(
    id            BIGINT   NOT NULL COMMENT '关联主键',
    role_id       BIGINT   NOT NULL COMMENT '角色主键',
    permission_id BIGINT   NOT NULL COMMENT '权限主键',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_role_permission (role_id, permission_id, deleted),
    KEY idx_sys_role_permission_permission_id (permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色权限关联表';
