# Trusted Digital Evidence Platform

TDEP 是一个面向可信数字证据场景的企业后台系统，覆盖认证授权、案件流转、电子证据、法律文书、AI 能力接入和前端管理台。

当前仓库采用 Spring Boot / Spring Cloud Gateway 多模块后端，加 Vue 3 / TypeScript 前端。

## 模块结构

| 模块 | 说明 | 默认端口 |
| --- | --- | --- |
| `tdep-common` | 公共返回体、异常处理、TraceId、JWT 安全组件、MyBatis Plus 配置 | - |
| `tdep-gateway` | API 网关，统一鉴权、路由转发、用户上下文透传 | `8080` |
| `tdep-auth-service` | 登录、注册、退出、JWT 签发、RBAC 权限管理 | `9001` |
| `tdep-user-service` | 用户服务占位模块 | `9002` |
| `tdep-evidence-service` | 电子证据上传、Hash、固化、下载、证据链、操作日志 | `9003` |
| `tdep-case-service` | 案件创建、分页、详情、状态流转、审判相关能力 | `9004` |
| `tdep-ai-service` | AI 能力服务占位/扩展模块 | `9005` |
| `tdep-document-service` | 文书模板、文书生成、PDF 导出、签章、归档 | `9006` |
| `tdep-notify-service` | 站内消息通知、已读状态管理、未读计数 | `9007` |
| `tdep-web` | Vue 3 企业后台前端 | `5173` |

## 技术栈

后端：

- Java 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Spring Security
- MyBatis Plus 3.5.7
- JJWT 0.12.5
- MySQL 8+
- Redis 6+
- 阿里云 OSS (对象存储)

前端：

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Element Plus
- Axios
- ECharts

## 环境要求

本地启动前请准备：

- JDK 17
- Maven 3.9+
- Node.js 18+
- MySQL 8+
- Redis 6+

默认数据库由各服务 `application.yml` 指定：

| 服务 | 默认数据库 |
| --- | --- |
| `tdep-auth-service` | `tdep_auth` |
| `tdep-case-service` | `tdep_case` |
| `tdep-evidence-service` | `tdep_evidence` |
| `tdep-document-service` | `tdep_document` |
| `tdep-notify-service` | `tdep_notify` |

所有服务默认数据库账号为 `root/123456`，配置已硬编码在 `application.yml` 中。

## 配置说明

所有服务的配置已硬编码在各自的 `application.yml` 文件中，包括：

- 数据库连接 (MySQL)
- Redis 连接
- JWT 密钥
- 阿里云 OSS 配置

如需修改配置，直接编辑对应服务的 `application.yml` 文件即可。

## 构建

后端编译：

```bash
mvn clean compile
```

跳过测试打包：

```bash
mvn clean package -DskipTests
```

前端安装依赖：

```bash
cd tdep-web
npm install
```

前端构建：

```bash
npm run build
```

## 本地启动

建议启动顺序：

1. 启动 MySQL、Redis、MinIO。
2. 启动后端基础服务。
3. 启动网关。
4. 启动前端。

后端服务示例：

```bash
mvn -pl tdep-auth-service spring-boot:run
mvn -pl tdep-evidence-service spring-boot:run
mvn -pl tdep-case-service spring-boot:run
mvn -pl tdep-document-service spring-boot:run
mvn -pl tdep-gateway spring-boot:run
```

前端开发服务：

```bash
cd tdep-web
npm run dev
```

访问地址：

- 前端：`http://localhost:5173`
- 网关：`http://localhost:8080`

默认管理员账号：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `Admin@123456` | 系统管理员 |

注册用户可选择「普通用户」或「法官」身份。

## 网关路由

| 路径 | 下游服务 |
| --- | --- |
| `/api/auth/**` | `tdep-auth-service` |
| `/api/users/**` | `tdep-user-service` |
| `/api/evidences/**` | `tdep-evidence-service` |
| `/api/cases/**` | `tdep-case-service` |
| `/api/document/**` | `tdep-document-service` |
| `/api/documents/**` | `tdep-document-service` |
| `/api/template/**` | `tdep-document-service` |
| `/api/notify/**` | `tdep-notify-service` |
| `/api/ai/**` | `tdep-ai-service` |

网关白名单：

- `/actuator/health`
- `/api/auth/login`
- `/api/auth/register`

其他接口需要携带：

```http
Authorization: Bearer <accessToken>
```

## 前端功能

当前 `tdep-web` 已包含：

- 登录页、注册页（支持 USER/JUDGE 角色选择）
- 主布局、侧边菜单、顶部栏、面包屑、多标签页
- 动态路由与权限过滤
- 工作台仪表盘
- 案件列表、创建、详情、状态流转
- 证据列表、上传、校验、下载地址打开
- 文书生成、预览、下载、签章
- 文书模板列表和上传
- 通知中心页面（已对接后端通知服务）

已知边界：

- 系统用户管理、案件详情独立页仍为占位或待扩展功能。
- 文书分页列表接口尚未提供，前端文书页仅展示当前会话生成结果。

## 统一响应格式

后端接口统一返回：

```json
{
  "code": 0,
  "message": "成功",
  "data": {},
  "traceId": "string",
  "timestamp": "2026-05-16 12:00:00"
}
```

`code = 0` 表示成功。前端会自动展开 `data`。

## TraceId

前端请求会携带：

```http
X-TDEP-Trace-Id: <uuid>
```

后端 `TraceIdFilter` 会将 traceId 写入 MDC，并在响应头中回传，便于跨服务排查。

## RBAC 与权限

当前 RBAC 基础表位于 `tdep-auth-service`：

- `sys_user`
- `sys_role`
- `sys_permission`
- `sys_user_role`
- `sys_role_permission`

### 角色说明

| 角色 | 编码 | 说明 |
| --- | --- | --- |
| 系统管理员 | `ADMIN` | 拥有全部权限 |
| 普通用户 | `USER` | 案件创建、证据上传、文书管理等基础权限 |
| 法官 | `JUDGE` | 案件审理、证据验证、文书签章等审判权限 |

### 注册功能

用户注册时可选择身份类型（普通用户/法官），系统会自动分配对应角色。

登录成功后，`auth-service` 会把用户角色和权限写入 JWT。前端根据权限动态过滤路由菜单。

后续系统管理模块建议新增 `tdep-system-service`，由该服务管理用户、角色、菜单、按钮权限、字典、参数、操作日志和审计日志；`auth-service` 保持认证与签发职责。

## 相关文档

- [AUTH_SERVICE.md](AUTH_SERVICE.md)：认证授权服务设计和使用说明。

## 开发注意事项

- 后端统一使用 Java 17 和 UTF-8 编码。
- 业务服务优先通过网关访问，避免前端直连服务端口。
- 修改角色权限后，已签发 JWT 不会自动更新，用户需重新登录后生效。
- 网关使用 `StripPrefix=1`，会自动去除路径中的 `/api` 前缀，因此各服务 Controller 需同时映射 `/api/xxx` 和 `/xxx` 两个路径。
- 生产环境必须修改默认 JWT 密钥、数据库密码和阿里云 OSS 配置。
