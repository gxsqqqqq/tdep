-- tdep-auth-service 默认 RBAC 初始化数据。
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
