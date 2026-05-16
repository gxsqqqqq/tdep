# tdep-auth-service 认证授权模块说明

## 模块目标

`tdep-auth-service` 实现企业级 RBAC 认证授权能力，基于 Spring Boot 3、Spring Security、JWT、MyBatis Plus、MySQL 和 Redis 构建。模块负责用户注册、登录、Token 签发与校验、当前用户信息读取、退出登录以及基于角色和权限的访问控制。

## 已实现功能

1. 用户注册：`POST /api/auth/register`
2. 用户登录：`POST /api/auth/login`
3. JWT Token 生成：登录成功后签发访问令牌
4. Token 校验：Spring Security 过滤器解析 JWT 并建立认证上下文
5. Redis Token 缓存：登录写入 Redis，退出登录删除 Redis 登录态
6. RBAC 权限控制：角色和权限写入 JWT，并支持 `@PreAuthorize` 方法级鉴权
7. 获取当前用户信息：`GET /api/auth/me`
8. 退出登录：`POST /api/auth/logout`
9. 统一返回：使用 `Result<T>` 返回结构
10. 全局异常处理：业务异常、参数校验异常、认证授权异常统一 JSON 输出
11. BCrypt 密码加密：注册和默认账号密码均使用 BCrypt
12. MyBatis Plus 逻辑删除：所有 RBAC 表均包含 `deleted` 字段

## 数据库表

自动生成的 RBAC 表如下：

| 表名 | 说明 |
| --- | --- |
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_permission` | 权限表 |
| `sys_user_role` | 用户角色关联表 |
| `sys_role_permission` | 角色权限关联表 |

所有表均包含基础字段、`created_at`、`updated_at` 和 `deleted` 逻辑删除字段。

SQL 文件位置：

- 自动启动初始化结构：[schema.sql](tdep-auth-service/src/main/resources/schema.sql)
- 自动启动初始化数据：[data.sql](tdep-auth-service/src/main/resources/data.sql)
- 完整建库脚本：[tdep_auth_schema.sql](tdep-auth-service/src/main/resources/sql/tdep_auth_schema.sql)

## 初始化数据

默认管理员账号：

| 字段 | 值 |
| --- | --- |
| 用户名 | `admin` |
| 密码 | `Admin@123456` |
| 角色 | `ADMIN` |

默认角色：

- `ADMIN`：系统管理员，拥有认证授权模块全部权限
- `USER`：普通用户，注册用户默认绑定该角色

默认权限：

- `auth:user:read`
- `auth:user:create`
- `auth:token:issue`
- `auth:token:revoke`
- `auth:rbac:manage`

## 接口说明

### 用户注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "demo_user",
  "password": "Demo@123456",
  "nickname": "演示用户",
  "phone": "13800000000",
  "email": "demo@tdep.local"
}
```

### 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@123456"
}
```

### 获取当前用户信息

```http
GET /api/auth/me
Authorization: Bearer <accessToken>
```

### 退出登录

```http
POST /api/auth/logout
Authorization: Bearer <accessToken>
```

完整 HTTP 测试示例见：[auth.http](tdep-auth-service/src/test/http/auth.http)

## 代码结构

认证服务代码结构如下：

```text
tdep-auth-service/src/main/java/com/tdep/auth
├── config          # MyBatis Plus 自动填充配置
├── controller      # REST 接口层
├── dto             # 请求参数对象
├── entity          # 数据库实体
├── mapper          # MyBatis Plus Mapper
├── security        # 认证服务安全扩展
├── service         # 业务接口
├── serviceImpl     # 业务实现
└── vo              # 响应视图对象
```

公共安全能力位于 `tdep-common`：

- `Result<T>` 统一返回
- `GlobalExceptionHandler` 全局异常处理
- `SecurityConfig` Spring Security 基础配置
- `JwtTokenService` JWT 签发和解析
- `JwtAuthenticationFilter` JWT 认证过滤器
- `JwtTokenValidator` Token 状态校验扩展点

## 启动配置

认证服务默认端口为 `9001`。

关键环境变量：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `TDEP_AUTH_SERVICE_PORT` | `9001` | 服务端口 |
| `TDEP_AUTH_DB_URL` | `jdbc:mysql://localhost:3306/tdep_auth...` | MySQL 地址 |
| `TDEP_AUTH_DB_USERNAME` | `root` | MySQL 用户名 |
| `TDEP_AUTH_DB_PASSWORD` | `root` | MySQL 密码 |
| `TDEP_REDIS_HOST` | `localhost` | Redis 主机 |
| `TDEP_REDIS_PORT` | `6379` | Redis 端口 |
| `TDEP_JWT_SECRET` | 开发默认密钥 | JWT 签名密钥 |
| `TDEP_JWT_ACCESS_TOKEN_SECONDS` | `7200` | Token 有效期秒数 |
| `TDEP_AUTH_SQL_INIT_MODE` | `always` | SQL 初始化模式 |

启动命令：

```bash
mvn -pl tdep-auth-service -am spring-boot:run
```

或先打包后启动：

```bash
mvn -pl tdep-auth-service -am package -DskipTests
java -jar tdep-auth-service/target/tdep-auth-service-1.0.0-SNAPSHOT.jar
```

## 验证结果

已执行并通过：

```bash
mvn -pl tdep-auth-service -am compile
mvn -pl tdep-auth-service -am package -DskipTests
mvn compile
```

本地启动检查结果：

- 跳过 SQL 初始化时，认证服务可正常启动并监听 `9001`
- 默认完整启动受本机环境影响：MySQL `root/root` 登录被拒绝
- 本机 Redis `localhost:6379` 当前不可连

在正确配置 MySQL 账号密码并启动 Redis 后，模块可按默认配置执行 SQL 初始化并提供完整认证授权能力。

# tdep-case-service 案件管理模块说明

## 模块目标

`tdep-case-service` 实现民商事案件全生命周期管理能力，基于 Spring Boot 3、Spring Security、JWT、RBAC、MyBatis Plus、MySQL 和 Redis 构建。模块围绕案件主数据、当事人、流程记录、证据、庭审和判决记录展开，状态变更统一由状态机校验。

## 已实现功能

1. 创建案件草稿：`POST /api/cases`
2. 案件列表分页：`GET /api/cases`
3. 案件详情查询：`GET /api/cases/{id}`
4. 案件状态流转：`POST /api/cases/{id}/transitions`
5. 指派承办法官：`PUT /api/cases/{id}/judge`
6. 上传证据元数据：`POST /api/cases/{id}/evidences`
7. 创建庭审记录：`POST /api/cases/{id}/trials`
8. 更新庭审记录：`PUT /api/cases/{id}/trials/{trialId}/record`
9. 创建判决记录：`POST /api/cases/{id}/judgements`
10. 状态机非法流转校验：禁止跨阶段流转、终态继续流转、无权限角色流转
11. RBAC 权限入口校验：接口层使用 `@PreAuthorize`
12. 流程审计：所有状态流转写入 `case_process`

## 案件状态机

状态枚举位于：[CaseStatus.java](tdep-case-service/src/main/java/com/tdep/casecenter/enums/CaseStatus.java)

已实现状态：

- `DRAFT`：草稿
- `SUBMITTED`：已提交立案
- `FILING_REVIEW`：立案审核中
- `NEED_SUPPLEMENT`：待补正
- `FILING_REJECTED`：立案驳回
- `ACCEPTED`：已受理
- `PAYMENT_PENDING`：待缴费
- `PAID`：已缴费
- `EVIDENCE_SUBMITTING`：举证中
- `SCHEDULED`：已排期
- `IN_TRIAL`：庭审中
- `MEDIATION`：调解中
- `MEDIATION_SUCCESS`：调解成功
- `MEDIATION_FAILED`：调解失败
- `JUDGEMENT_PENDING`：待判决
- `JUDGED`：已判决
- `APPEAL_PERIOD`：上诉期
- `APPEALED`：已上诉
- `EFFECTIVE`：已生效
- `EXECUTION_PENDING`：待执行
- `EXECUTING`：执行中
- `CLOSED`：已结案
- `WITHDRAWN`：已撤诉
- `TERMINATED`：已终结

状态机定义位于：[CaseStateMachine.java](tdep-case-service/src/main/java/com/tdep/casecenter/workflow/CaseStateMachine.java)

核心流转示例：

```text
DRAFT -> SUBMITTED -> FILING_REVIEW -> ACCEPTED -> PAYMENT_PENDING -> PAID
PAID -> EVIDENCE_SUBMITTING -> SCHEDULED -> IN_TRIAL -> JUDGEMENT_PENDING
JUDGEMENT_PENDING -> JUDGED -> APPEAL_PERIOD -> EFFECTIVE
EFFECTIVE -> EXECUTION_PENDING -> EXECUTING -> CLOSED
```

特殊流转：

- 非终态可撤诉：`WITHDRAW`
- 非终态可终结：`TERMINATE`
- 终态包括：`CLOSED`、`WITHDRAWN`、`TERMINATED`、`FILING_REJECTED`

## 数据库表

案件管理模块自动生成以下表：

| 表名 | 说明 |
| --- | --- |
| `case_info` | 案件主表 |
| `case_party` | 案件当事人表 |
| `case_process` | 案件流程记录表 |
| `case_evidence` | 案件证据表 |
| `case_trial` | 案件庭审表 |
| `case_judgement` | 案件裁判文书表 |

所有表均包含 `created_at`、`updated_at` 或流程时间字段，并支持 `deleted` 逻辑删除。

SQL 文件位置：

- 自动启动初始化结构：[schema.sql](tdep-case-service/src/main/resources/schema.sql)
- 完整建库脚本：[tdep_case_schema.sql](tdep-case-service/src/main/resources/sql/tdep_case_schema.sql)

## 接口说明

### 创建案件

```http
POST /api/cases
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "caseTitle": "买卖合同纠纷",
  "caseType": "CIVIL",
  "causeAction": "买卖合同纠纷",
  "procedureType": "FIRST_INSTANCE",
  "claimRequest": "请求判令被告支付货款及违约金。",
  "claimAmount": 120000.00,
  "courtName": "TDEP互联网法院",
  "parties": []
}
```

### 案件分页

```http
GET /api/cases?pageNo=1&pageSize=10&status=DRAFT
Authorization: Bearer <accessToken>
```

### 案件详情

```http
GET /api/cases/{id}
Authorization: Bearer <accessToken>
```

### 状态流转

```http
POST /api/cases/{id}/transitions
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "action": "SUBMIT",
  "comment": "提交在线立案申请"
}
```

### 指派法官

```http
PUT /api/cases/{id}/judge
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "judgeId": 20001,
  "clerkId": 20002,
  "comment": "指派承办法官和书记员"
}
```

### 上传证据

```http
POST /api/cases/{id}/evidences
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "evidenceName": "电子合同",
  "evidenceType": "DOCUMENT",
  "fileUrl": "https://example.com/evidence/contract.pdf",
  "fileHash": "sha256-demo",
  "description": "双方签署的电子合同"
}
```

### 创建庭审记录

```http
POST /api/cases/{id}/trials
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "trialType": "ONLINE",
  "trialTime": "2026-06-01 09:30:00",
  "trialLocation": "https://trial.tdep.local/room/1001",
  "judgeId": 20001,
  "clerkId": 20002
}
```

### 创建判决记录

```http
POST /api/cases/{id}/judgements
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "judgementType": "JUDGEMENT",
  "title": "民事判决书",
  "contentSummary": "判令被告支付货款及违约金。",
  "documentUrl": "https://example.com/judgement/jdg.pdf",
  "judgeId": 20001
}
```

完整 HTTP 测试示例：

- 常规接口：[case.http](tdep-case-service/src/test/http/case.http)
- 状态流转案例：[case-transition.http](tdep-case-service/src/test/http/case-transition.http)

## 代码结构

案件服务代码结构如下：

```text
tdep-case-service/src/main/java/com/tdep/casecenter
├── config          # MyBatis Plus 自动填充配置
├── controller      # REST 接口层
├── dto             # 请求参数对象
├── entity          # 数据库实体
├── enums           # 案件状态、动作、角色、庭审和文书枚举
├── mapper          # MyBatis Plus Mapper
├── service         # 业务接口
├── serviceImpl     # 业务实现
├── vo              # 响应视图对象
└── workflow        # 案件状态机、流转定义、校验器和权限解析器
```

## 权限说明

接口层使用 RBAC 权限编码：

- `case:create`
- `case:read:self`
- `case:read:all`
- `case:workflow`
- `case:assign`
- `case:evidence:upload`
- `case:trial:manage`
- `case:judgement:publish`

状态机内部还会根据 JWT 角色解析案件业务角色：

- `PARTY`：当事人
- `JUDGE`：法官
- `CLERK`：书记员
- `ADMIN`：管理员

## 验证结果

已执行并通过：

```bash
mvn -pl tdep-case-service -am compile
mvn -pl tdep-case-service -am package -DskipTests
mvn compile
```

本地启动检查结果：

- 跳过 SQL 初始化时，案件服务可正常启动并监听 `9004`
- 默认完整启动依赖本机 MySQL 和 Redis 配置

# tdep-evidence-service 可信电子证据模块说明

## 模块目标

`tdep-evidence-service` 实现企业级可信电子证据存证能力，基于 Spring Boot 3、MyBatis Plus、MySQL、Redis、MinIO、JWT、SHA-256 和 RESTful API 构建。模块负责文件上传、电子证据管理、Hash 防篡改、可信时间戳、证据链、文件校验、PDF 固化和预签名下载。

## 已实现功能

1. 上传证据文件：`POST /api/evidences/upload`
2. 证据分页查询：`GET /api/evidences`
3. 证据详情查询：`GET /api/evidences/{id}`
4. 查询证据 Hash：`GET /api/evidences/hash/{id}`
5. 文件完整性校验：`POST /api/evidences/{id}/verify`
6. 原始文件预签名下载：`GET /api/evidences/{id}/download-url`
7. PDF 固化凭证生成：`POST /api/evidences/{id}/seal`
8. PDF 固化凭证预签名下载：`GET /api/evidences/{id}/seal/download-url`
9. 查询证据链：`GET /api/evidences/{id}/chains`
10. 校验证据链完整性：`POST /api/evidences/{id}/chains/verify`
11. 查询操作审计日志：`GET /api/evidences/{id}/logs`
12. 冻结证据：`POST /api/evidences/{id}/freeze`
13. 逻辑删除证据：`DELETE /api/evidences/{id}`

## 防篡改机制

证据上传后会对文件内容计算 SHA-256：

```text
file_fingerprint = SHA-256(file_bytes)
```

文件写入 MinIO 后，数据库保存：

- Hash 算法：`SHA-256`
- 原始 Hash：`hash_value`
- 文件唯一指纹：`file_fingerprint`
- 文件大小：`file_size`
- 计算时间：`calculated_at`

完整性校验时，服务会从 MinIO 重新读取当前文件流，再次计算 SHA-256，并与数据库原始 Hash 比对。任何文件字节发生变化，当前 Hash 都会变化，校验结果会变为失败。

## 证据生命周期

已实现状态枚举位于：[EvidenceStatus.java](tdep-evidence-service/src/main/java/com/tdep/evidence/enums/EvidenceStatus.java)

状态包括：

- `UPLOADING`：上传中
- `HASHING`：Hash计算中
- `STORED`：已存储
- `TIMESTAMPED`：已时间戳
- `SEALED`：已固化
- `VERIFYING`：校验中
- `VERIFIED`：校验通过
- `VERIFY_FAILED`：校验失败
- `DELETED`：已删除
- `FROZEN`：已冻结

当前实现主流程：

```text
上传 -> SHA-256计算 -> MinIO存储 -> 时间戳记录 -> 证据链记录
校验 -> 重新计算Hash -> 比对原始Hash -> 更新 VERIFIED / VERIFY_FAILED
固化 -> 生成PDF凭证 -> 写入MinIO -> 更新 SEALED
冻结 -> FROZEN
删除 -> DELETED + 逻辑删除
```

## 对象存储

使用 MinIO，配置位于 `tdep.evidence.storage`。

默认 bucket：

- `tdep-evidence-original`：原始证据文件
- `tdep-evidence-sealed`：PDF 固化凭证
- `tdep-evidence-temp`：大文件临时区预留
- `tdep-evidence-preview`：预览文件预留

对象命名规则：

```text
原始文件：
yyyy/MM/dd/case-{caseId}/{evidenceNo}/original-{sha256}.{ext}

固化凭证：
yyyy/MM/dd/case-{caseId}/evidence-{evidenceId}/seal-certificate.pdf
```

Bucket 默认按私有访问设计，下载通过后端鉴权后生成 MinIO 预签名 URL，默认有效期 `900` 秒。

## 数据库表

证据模块自动生成以下表：

| 表名 | 说明 |
| --- | --- |
| `evidence_file` | 证据文件主表 |
| `evidence_hash` | 证据 Hash 表 |
| `evidence_chain` | 证据链表 |
| `evidence_operation_log` | 操作审计日志表 |

SQL 文件位置：

- 自动启动初始化结构：[schema.sql](tdep-evidence-service/src/main/resources/schema.sql)
- 完整建库脚本：[tdep_evidence_schema.sql](tdep-evidence-service/src/main/resources/sql/tdep_evidence_schema.sql)

## 权限说明

接口层使用 RBAC 权限编码：

- `evidence:upload`
- `evidence:read:self`
- `evidence:read:case`
- `evidence:read:all`
- `evidence:verify`
- `evidence:download`
- `evidence:delete`
- `evidence:seal`
- `evidence:freeze`
- `evidence:chain:read`
- `evidence:audit:read`
- `evidence:admin`

## 代码结构

```text
tdep-evidence-service/src/main/java/com/tdep/evidence
├── audit           # 操作审计
├── config          # MyBatis Plus 自动填充
├── controller      # REST 接口层
├── dto             # 请求参数对象
├── entity          # 数据库实体
├── enums           # 证据状态、操作类型、证据链事件枚举
├── hash            # SHA-256 Hash 计算和校验模型
├── mapper          # MyBatis Plus Mapper
├── seal            # PDF 固化凭证生成
├── security        # 证据权限辅助
├── service         # 业务接口
├── serviceImpl     # 业务实现
├── storage         # MinIO 对象存储封装
└── vo              # 响应视图对象
```

## 接口测试

完整 HTTP 测试示例见：[evidence.http](tdep-evidence-service/src/test/http/evidence.http)

## 验证结果

已执行并通过：

```bash
mvn -pl tdep-evidence-service -am compile
mvn -pl tdep-evidence-service -am package -DskipTests
mvn compile
```

本地启动检查结果：

- 跳过 SQL 初始化时，证据服务可正常启动并监听 `9003`
- 完整上传流程依赖本机 MySQL、Redis 和 MinIO 服务配置
