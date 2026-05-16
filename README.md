# Trusted Digital Evidence Platform

TDEP 是一个面向可信数字证据场景的企业后台系统，覆盖认证授权、案件流转、电子证据、法律文书、AI 能力接入和前端管理台。

当前仓库采用 Spring Boot / Spring Cloud Gateway 多模块后端，加 Vue 3 / TypeScript 前端。

## 模块结构

| 模块 | 说明 | 默认端口 |
| --- | --- | --- |
| `tdep-common` | 公共返回体、异常处理、TraceId、JWT 安全组件、MyBatis Plus 配置 | - |
| `tdep-gateway` | API 网关，统一鉴权、路由转发、用户上下文透传 | `8080` |
| `tdep-auth-service` | 登录、注册、退出、JWT 签发、基础 RBAC 读取 | `9001` |
| `tdep-user-service` | 用户服务占位模块 | `9002` |
| `tdep-evidence-service` | 电子证据上传、Hash、固化、下载、证据链、操作日志 | `9003` |
| `tdep-case-service` | 案件创建、分页、详情、状态流转、审判相关能力 | `9004` |
| `tdep-ai-service` | AI 能力服务占位/扩展模块 | `9005` |
| `tdep-document-service` | 文书模板、文书生成、PDF 导出、签章、归档 | `9006` |
| `tdep-web` | Vue 3 企业后台前端 | `5173` |

## 技术栈

后端：

- Java 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Spring Security
- MyBatis Plus 3.5.7
- JJWT 0.12.5
- MySQL
- Redis
- MinIO

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
- MinIO

默认数据库由各服务 `application.yml` 指定：

| 服务 | 默认数据库 |
| --- | --- |
| `tdep-auth-service` | `tdep_auth` |
| `tdep-case-service` | `tdep_case` |
| `tdep-evidence-service` | `tdep_evidence` |
| `tdep-document-service` | `tdep_document` |
| `tdep-ai-service` | `tdep_ai` |

大多数服务默认数据库账号为 `root/root`，可通过环境变量覆盖。`tdep-ai-service` 当前配置为 `roto/123456`，如需本地启动请按实际 MySQL 用户调整。

## 常用环境变量

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `TDEP_JWT_SECRET` | JWT 签名密钥 | `tdep-development-secret-key-please-change-in-production` |
| `TDEP_REDIS_HOST` | Redis 地址 | `localhost` |
| `TDEP_REDIS_PORT` | Redis 端口 | `6379` |
| `TDEP_MINIO_ENDPOINT` | MinIO 地址 | `http://localhost:9000` |
| `TDEP_MINIO_ACCESS_KEY` | MinIO Access Key | `minioadmin` |
| `TDEP_MINIO_SECRET_KEY` | MinIO Secret Key | `minioadmin` |
| `TDEP_GATEWAY_PORT` | 网关端口 | `8080` |

各服务数据库连接也可以通过 `TDEP_AUTH_DB_URL`、`TDEP_CASE_DB_URL`、`TDEP_EVIDENCE_DB_URL`、`TDEP_DOCUMENT_DB_URL` 等变量覆盖。

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

| 用户名 | 密码 |
| --- | --- |
| `admin` | `Admin@123456` |

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

- 登录页
- 主布局、侧边菜单、顶部栏、面包屑、多标签页
- 动态路由与权限过滤
- 工作台仪表盘
- 案件列表、创建、详情、状态流转
- 证据列表、上传、校验、下载地址打开
- 文书生成、预览、下载、签章
- 文书模板列表和上传
- 通知中心页面

已知边界：

- 通知服务当前仓库未提供后端模块，前端通知查询对 `404/503` 做空数据降级。
- 系统用户管理、通知日志、案件详情独立页仍为占位或待扩展功能。
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

登录成功后，`auth-service` 会把用户角色和权限写入 JWT。各业务服务使用 `@PreAuthorize` 做方法级鉴权。

后续系统管理模块建议新增 `tdep-system-service`，由该服务管理用户、角色、菜单、按钮权限、字典、参数、操作日志和审计日志；`auth-service` 保持认证与签发职责。

## 相关文档

- [AUTH_SERVICE.md](AUTH_SERVICE.md)：认证授权服务设计和使用说明。

## 开发注意事项

- 后端统一使用 Java 17 和 UTF-8 编码。
- 业务服务优先通过网关访问，避免前端直连服务端口。
- 修改角色权限后，已签发 JWT 不会自动更新，用户需重新登录后生效。
- 生产环境必须覆盖默认 JWT 密钥、数据库密码和 MinIO 密钥。
