-- tdep-auth-service 完整 RBAC 建库脚本，可直接在 MySQL 中执行。
CREATE DATABASE IF NOT EXISTS tdep_auth
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE tdep_auth;

-- 用户表。
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

-- 角色表。
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

-- 权限表。
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

-- 用户角色关联表。
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

-- 角色权限关联表。
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

-- admin 默认密码：Admin@123456
INSERT INTO sys_user (id, username, password, nickname, phone, email, status, last_login_time, created_at, updated_at, deleted)
VALUES (1000000000000000001, 'admin', '$2a$10$fzApts.cL3Hq1ROn/fu8auT/VWfiWKk9QIXg0h3dOtd8J3MFICp7y',
        '系统管理员', NULL, 'admin@tdep.local', 1, NULL, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE nickname   = VALUES(nickname),
                        email      = VALUES(email),
                        status     = VALUES(status),
                        updated_at = NOW(),
                        deleted    = 0;

INSERT INTO sys_role (id, role_code, role_name, description, status, created_at, updated_at, deleted)
VALUES (1000000000000000101, 'ADMIN', '系统管理员', '拥有认证授权模块全部权限', 1, NOW(), NOW(), 0),
       (1000000000000000102, 'USER', '普通用户', '平台默认注册用户角色', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE role_name   = VALUES(role_name),
                        description = VALUES(description),
                        status      = VALUES(status),
                        updated_at  = NOW(),
                        deleted     = 0;

INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, resource_path, http_method, description, status,
                            created_at, updated_at, deleted)
VALUES (1000000000000000201, 'auth:user:read', '读取当前用户', 'API', '/api/auth/me', 'GET', '读取当前登录用户信息', 1, NOW(), NOW(), 0),
       (1000000000000000202, 'auth:user:create', '创建用户', 'API', '/api/auth/register', 'POST', '注册或创建用户账号', 1, NOW(), NOW(), 0),
       (1000000000000000203, 'auth:token:issue', '签发令牌', 'API', '/api/auth/login', 'POST', '用户登录并签发访问令牌', 1, NOW(), NOW(), 0),
       (1000000000000000204, 'auth:token:revoke', '注销令牌', 'API', '/api/auth/logout', 'POST', '退出登录并注销访问令牌', 1, NOW(), NOW(), 0),
       (1000000000000000205, 'auth:rbac:manage', '管理权限', 'API', '/api/auth/**', '*', '认证授权模块管理权限', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name),
                        permission_type = VALUES(permission_type),
                        resource_path   = VALUES(resource_path),
                        http_method     = VALUES(http_method),
                        description     = VALUES(description),
                        status          = VALUES(status),
                        updated_at      = NOW(),
                        deleted         = 0;

INSERT INTO sys_user_role (id, user_id, role_id, created_at, updated_at, deleted)
VALUES (1000000000000000301, 1000000000000000001, 1000000000000000101, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE updated_at = NOW(),
                        deleted    = 0;

INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, updated_at, deleted)
VALUES (1000000000000000401, 1000000000000000101, 1000000000000000201, NOW(), NOW(), 0),
       (1000000000000000402, 1000000000000000101, 1000000000000000202, NOW(), NOW(), 0),
       (1000000000000000403, 1000000000000000101, 1000000000000000203, NOW(), NOW(), 0),
       (1000000000000000404, 1000000000000000101, 1000000000000000204, NOW(), NOW(), 0),
       (1000000000000000405, 1000000000000000101, 1000000000000000205, NOW(), NOW(), 0),
       (1000000000000000406, 1000000000000000102, 1000000000000000201, NOW(), NOW(), 0),
       (1000000000000000407, 1000000000000000102, 1000000000000000204, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE updated_at = NOW(),
                        deleted    = 0;
