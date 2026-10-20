# 通用项目框架

本文件统一维护各模块的部署与配置说明。通用配置集中说明一次，各模块只列专有配置。配置项和默认端口以当前源码为准，示例中的地址、密钥需替换为实际环境值。

## 模块与配置入口

后端使用 Java 21、Maven，前端使用 Node.js、npm。基础账号密码登录涉及前端、网关、认证和 UPMS；文件、消息、定时任务按使用的功能增加。

| 模块 | 是否独立运行 | 当前端口 | 配置入口 | 主要需要配置什么 |
|---|---|---|---|---|
| [poria-auth](#poria-auth) | 是 | 3000 | [application.yml](poria-platform/poria-auth/src/main/resources/application.yml) | 登录加密、UPMS 地址、OAuth 客户端 |
| [poria-gateway](#poria-gateway) | 是 | 9999 | [application.yml](poria-platform/poria-gateway/src/main/resources/application.yml) | 动态路由、跨域、同步报文密钥 |
| [poria-upms-biz](#poria-upms) | 是 | 4000 | [application.yml](poria-platform/poria-upms/poria-upms-biz/src/main/resources/application.yml) | 登录加密、认证及消息地址、菜单和角色权限 |
| [poria-base-biz](#poria-base) | 是 | 8002 | [application.yml](poria-platform/poria-base/poria-base-biz/src/main/resources/application.yml) | 对象存储、桶、文件限制；按需配置 REBUILD |
| [poria-message-biz](#poria-message) | 是 | 8001 | [application.yml](poria-platform/poria-message/poria-message-biz/src/main/resources/application.yml) | 消息模板；按需配置短信、腾讯 IM、第三方消息服务和 Kafka |
| [poria-job](#poria-job) | 是 | HTTP 5002；执行器 9999 | [application.yml](poria-platform/poria-job/src/main/resources/application.yml) | XXL-Job Admin、Token、执行器地址及日志目录 |
| [Vue 前端](#vue-前端) | 开发运行 Vite；生产部署静态文件 | 开发 8000 | [.env.development](vue/.env.development)、[.env.production](vue/.env.production) | API 地址、OAuth 客户端、加密密钥、调度中心页面地址 |
| poria-platform、poria-common | 否，Maven 聚合 | 无 | [后端 POM](poria-platform/pom.xml)、[公共模块 POM](poria-platform/poria-common/pom.xml) | 构建版本和子模块 |
| poria-upms、poria-base、poria-message | 否，Maven 聚合 | 无 | 对应模块的 pom.xml | 聚合 API 和 biz |
| poria-upms-api、poria-base-api、poria-message-api | 否，Java 依赖库 | 无 | 接口、模型和客户端定义 | 在调用方配置目标服务及认证 |
| [poria-common-*](#公共模块) | 否，Java 依赖库 | 无 | 下文各公共模块 | 在使用它们的宿主服务中配置 |

XXL-Job Admin 另外部署，本文示例对外使用 9080。

## 通用配置与服务地址

### 各后端服务共同核对的配置

| 配置 | 配置要求 |
|---|---|
| `server.port` / `SERVER_PORT` | 服务 HTTP 端口；改端口时同步修改调用地址、网关路由 |
| `spring.application.name` | 应用标识，多数模块通过 Maven 的 `@artifactId@` 填充 |
| `spring.profiles.active` / `SPRING_PROFILES_ACTIVE` | 运行环境；UPMS 当前固定为 dev，其他应用使用 Maven 的 `@profiles.active@` |
| `spring.profiles.include` / `SPRING_PROFILES_INCLUDE` | 按需加载附加配置：kafka 创建自定义生产者，log 启用 Kafka 日志投递；同时使用时合并填写 |
| `springdoc.api-docs.enabled` | OpenAPI 描述开关；业务服务用 `SWAGGER_ENABLE` 覆盖，网关用 `SWAGGER_API_DOCS_ENABLE` 覆盖 |
| `springdoc.swagger-ui.enabled` | 引入 UI 的应用可用此项控制文档界面；网关用 `SWAGGER_ENABLE` 覆盖 |
| `springdoc.api-docs.path` | OpenAPI 描述接口路径；网关当前配置为 `/swagger-ui.html`，此项不是 UI 页面地址 |
| `logging.level.*` | 按环境调整日志级别；网关过滤器、UPMS Mapper 和 base DAO 当前 debug，消息 DAO 默认 info，可用 `MESSAGE_SQL_LOG_LEVEL` 覆盖 |
| `spring.servlet.multipart.max-file-size` | 单文件上限；UPMS 当前 20MB，base 使用 `BASE_MAX_FILE_SIZE`，默认 50MB |
| `spring.servlet.multipart.max-request-size` | 请求总上限；UPMS 当前 100MB，base 与单文件共用上述变量和默认值 |

接口文档的实际开关使用 springdoc 配置；公共模块历史字段 `swagger.enabled` 不能替代这些开关。

Maven 当前默认 profile 为 dev，可用 `-Pprod` 选择 prod。Maven 占位符在构建时替换，环境变量占位符在运行时解析；切换 profile 不会自动补齐生产地址或凭据。以下 YAML 示例应与原配置合并，保留原有其他配置。

公共库中的同名 `application.yml` 不应被当作已完整合并到业务服务；使用相关功能时，在宿主服务中明确提供或导入配置。

### 服务间调用地址

统一常量位于 [ServiceNameConstants.java](poria-platform/poria-common/poria-common-core/src/main/java/cn/poria/common/core/constant/ServiceNameConstants.java)。以下配置填写在发起调用的进程中，主机名必须能从该进程所在网络解析和访问。

| 环境变量 | 当前默认地址 | 用途 |
|---|---|---|
| `PORIA_auth` | `http://poria-auth:3000` | UPMS 令牌管理等认证调用；变量大小写与源码一致 |
| `PORIA_UPMS` | `http://poria-upms:4000` | auth 读取用户、OAuth 客户端，以及其他服务查询角色、部门、字典、公共参数 |
| `PORIA_BASE` | `http://poria-base:8002` | 基础服务 |
| `PORIA_MESSAGE` | `http://poria-message:8001` | 消息服务，包括 UPMS 短信发送 |
| `PORIA_LOG` | `http://poria-log:8046` | 预留日志服务地址，仓库没有独立 poria-log 应用 |

`ServiceNameConstants.*` 保存的是带环境变量占位符的配置字符串。Feign 的 `url`、Spring 的 `@Value` 会解析它；使用 `HttpUtil`、`RestTemplate` 时需要先解析，再拼接接口路径。UPMS 短信地址通过 `@Value` 注入后拼接 `/inner/sms/send`。

本机部署时，可将实际用到的变量覆盖为 `http://localhost:对应端口`；容器部署则使用实际服务名。直接复制容器主机名到普通主机不保证可访问。

### 登录字段加密

以下配置由 auth、UPMS 和前端共同使用，各模块不再重复列出。

| 配置 / 环境变量 | 配置要求 |
|---|---|
| `auth.enable` / `ENCRYPT_ENABLE` | auth、UPMS 默认 true；前端当前始终加密，单独关闭 auth 解密会导致协议不一致，UPMS 部分写入也仍直接解密 |
| `auth.encodeKey` / `ENCODE_KEY` | auth、UPMS 的 AES 解密密钥，UTF-8 编码后为 16 字节 |
| `VITE_AUTH_ENCODE_KEY` | 前端加密密钥，必须与两个后端的密钥一致；当前环境文件未列出，需要补充覆盖 |

## poria-auth

认证服务，处理登录、令牌签发、刷新和退出，需要能访问 UPMS。OAuth 客户端记录在 UPMS 维护，ID、Secret、授权类型和 scope 与前端一致；当前登录使用 plat，刷新使用 refresh_token。

## poria-gateway

统一 API 入口，使用响应式网关 `spring.main.web-application-type=reactive`。

| 配置 | 何时配置 | 作用与要求 |
|---|---|---|
| 动态路由的 URI、Path、filters | 必须在平台路由管理核对 | URI 指向真实服务地址；修改记录后刷新路由 |
| `spring.cloud.gateway.server.webflux.globalcors` | 按前端来源配置 | 允许的页面来源、方法、请求头和 Cookie；当前来源配置较宽泛 |
| `spring.http.codecs.max-in-memory-size` | 按请求体大小配置，当前 100MB | 网关编解码缓冲区上限，业务服务上传限制另外配置 |
| `sync-encrypt-key` / `SYNC_ENCRYPT_KEY` | 使用同步报文加解密时 | 与调用端一致，配合 `isEncrypt` 请求头 |
| `gateway.encodeKey` | 路由使用 PasswordDecoderFilter 时 | 当前 YAML 未列出；按协议配置，避免与 auth 重复解密 |

路由 Path 与目标服务的对应关系如下，URI 使用前文服务地址表中的实际地址：

| Path 谓词 | 目标服务 |
|---|---|
| `/auth/**` | poria-auth |
| `/upms/**` | poria-upms-biz |
| `/base/**` | poria-base-biz |
| `/message/**` | poria-message-biz |

现有全局过滤器已移除第一层服务前缀，例如 `/message/inner/sms/send` 转为 `/inner/sms/send`；路由中避免再次配置 `StripPrefix=1` 导致重复移除。前端开发代理只去掉 `/api`，保留服务前缀交给网关处理。

限流配置类只提供 KeyResolver；需要限流的路由还需配置 RequestRateLimiter。响应式网关中的 servlet multipart 配置不能替代实际业务服务的上传限制。

## poria-upms

父模块聚合 API 和 biz，实际部署 `poria-upms-biz`。

专有配置为 `write.mobiles` / `LOGIN_MOBILES`，默认空；按需填写逗号分隔的手机号，名单中的手机号跳过短信流程中的一处 IP 限流检查。

UPMS 维护前端 OAuth 客户端、菜单路径、按钮权限、角色授权和网关路由。菜单调整后刷新菜单；角色权限调整后重新登录，以更新令牌中的权限。

个人中心需要 `sys_user_profile` 和 `sys_user_edit`。查询使用 `GET /user/me`，修改沿用 `PUT /user/edit`；`sys_user_edit` 同时用于用户管理的更新接口。新版角色创建和菜单权限保存会保留这两项默认权限，需要先有对应菜单定义。

API 子模块不独立配置端口，调用方注册相应 Feign 客户端并提供目标地址及认证。

## poria-base

父模块聚合 API 和 biz，实际部署 `poria-base-biz`，提供文件、Banner、活动、版本和 REBUILD 对接。

### 对象存储与文件上传

OSS 客户端在启动时初始化，上传服务依赖该客户端；部署 base 时需要明确存储参数。当前普通上传和签名访问使用 S3 兼容 SDK，文件按原格式上传。

| 配置 / 环境变量 | 配置要求 |
|---|---|
| `oss.endpoint` / `OSS_ENDPOINT` | 对象存储 API 地址，当前默认阿里云成都节点；浏览器使用签名地址时也需能访问 |
| `oss.region` / `OSS_REGION` | 与存储一致的区域，当前默认 cn-chengdu |
| `oss.access-key` / `OSS_ACCESS_KEY` | 存储 AccessKey，按环境提供 |
| `oss.secret-key` / `OSS_SECRET_KEY` | 存储 SecretKey，按环境提供 |
| `oss.path-style-access` | 当前 YAML 未设置，配置类默认 true；路径式存储使用 true，桶子域名访问按存储要求设置 false |
| `bucket.publicBucket` / `PUBLIC_BUCKET` | 公共桶名，默认 poria-public |
| `bucket.privateBucket` / `PRIVATE_BUCKET` | 私有桶名，默认 poria-private |
| `oss.max-connections` | 客户端最大连接数，配置类默认 100 |

对象存储配置示例：

```yaml
oss:
  endpoint: 'https://<对象存储API地址>'
  region: '<存储区域>'
  access-key: '<AccessKey>'
  secret-key: '<SecretKey>'
  path-style-access: false
bucket:
  publicBucket: '<公共桶>'
  privateBucket: '<私有桶>'
```

上传代码会检查桶并尝试创建，账号需有相应权限。公共上传返回去掉签名的 URL，公共桶的公开读策略需在存储侧设置；私有桶保持私有。浏览器直传还需存储端 CORS。

`/api/file/signature` 使用阿里云 OSS SDK 和域名拼接规则，不能仅改 endpoint 就当作适配了任意 S3 存储。`oss.enable` 缺省仍启用客户端，关闭可能使依赖客户端的服务无法启动；`oss.info` 也不能作为可靠关闭 `/oss` 控制器的开关。

### 按功能配置

| 功能 | 配置 / 环境变量 | 配置要求 |
|---|---|---|
| REBUILD | `rebuild.appid` / `REBUILD_APPID` | 对接应用 ID |
| REBUILD | `rebuild.appSecret` / `REBUILD_APP_SECRET` | 应用签名密钥 |
| REBUILD | `rebuild.url` / `REBUILD_URL` | 目标系统基础地址 |
| REBUILD | `rebuild.user` / `REBUILD_USER` | 目标系统账号 |
| REBUILD | `rebuild.password` / `REBUILD_PASSWORD` | 目标系统密码 |

REBUILD 在调用 `/api/rebuild/sgin` 时请求目标系统的 `/gw/api/login-token`。未使用时可保留当前空值，但不要删除供 `@Value` 注入的配置键。

`poria-base-api` 提供模型和 BaseFeignClient 定义，不独立运行；BaseFeignClient 本身没有 `@FeignClient`，调用方需提供具体客户端注册和实际 URL，接口路径按当前控制器核对。

## poria-message

父模块聚合 API 和 biz，实际部署 `poria-message-biz`。当前源码提供短信、消息模板、站内信、公告、待办及腾讯 IM。旧文档提到的 TRTC、VOD、外呼、云信小号、内容审核、语音识别等配置不作为当前模块的配置项。

### 基础与第三方服务地址

| 配置 / 环境变量 | 何时配置 | 作用与要求 |
|---|---|---|
| `convert.url` / `MESSAGE_CONVERT_URL` | 使用短链接转换时 | 可兼容当前请求响应结构的转换服务地址 |
| `PORIA_CUSTOMER` | 使用账号绑定、企业信息时 | 默认 `http://poria-customer:8004` |
| `PORIA_WXMP` | 使用公众号模板消息时 | 默认 `http://poria-wxmp:9994` |
| `PORIA_WXMA` | 使用小程序订阅消息时 | 默认 `http://poria-wxma:8023` |
| `PORIA_ALIMA` | 使用支付宝模板消息时 | 默认 `http://poria-alima:8034` |

本仓库没有上述 customer、wxmp、wxma、alima 服务实现，需要对接实际提供相应接口的服务。公众号发送器中的接收人 OpenId 解析仍有 TODO，不能仅配置地址就认为自动接收人解析已完成。

### 短信渠道

按实际使用的渠道配置，未使用的渠道可保留 YAML 中的空值；模板、渠道类型、签名和第三方模板 ID 需配套维护。

| 渠道 | YAML 配置前缀 | 需要填写的环境变量 |
|---|---|---|
| 阿里短信 | `message.alis` | `SMS_ALI_ACCESS_KEY_ID`、`SMS_ALI_ACCESS_KEY_SECRET`、`SMS_ALI_SIGN` |
| 移动 MAS | `message.mas` | `SMS_MAS_EC_NAME`、`SMS_MAS_AP_ID`、`SMS_MAS_SIGN`、`SMS_MAS_SECRET_KEY`、`SMS_MAS_URL` |
| 联通短信 | `message.lt` | `SMS_LT_LOGIN_NAME`、`SMS_LT_PASSWORD`、`SMS_LT_URL` |
| 电信短信 | `message.esurfing.plat` | `SMS_ESURFING_URL`、`SMS_ESURFING_APP_KEY`、`SMS_ESURFING_APP_SECRET`、`SMS_ESURFING_REPORT_URL`、`SMS_ESURFING_MO_URL` |
| 网易短信 | `message.ntes` | `SMS_NTES_URL`、`SMS_NTES_APP_KEY`、`SMS_NTES_APP_SECRET`、`SMS_NTES_NONCE`、`SMS_NTES_BUSINESS_ID` |

UPMS 验证码发送使用 `VERIFY_CODE` 模板 Key。使用前需维护有效模板及对应渠道参数。

### 腾讯 IM

| 配置 / 环境变量 | 作用与默认值 |
|---|---|
| `tx.im.sdkAppId` / `TX_IM_SDK_APP_ID` | 腾讯 IM 应用 ID，默认 0；使用时填写真实 ID |
| `tx.im.secretKey` / `TX_IM_SECRET_KEY` | UserSig 签名密钥，默认空 |
| `tx.im.adminAccountName` / `TX_IM_ADMIN_ACCOUNT` | 管理员账号，默认 administrator |
| `tx.im.singExpireTime` / `TX_IM_SIGN_EXPIRE_TIME` | 签名有效期秒数，默认 604800；源码配置名为 singExpireTime |
| `tx.im.callbackToken` / `TX_IM_CALLBACK_TOKEN` | 与腾讯控制台一致的回调 Token，默认空 |

回调经网关的路径为 `/message/api/im/tx/callback`。当前实现校验应用 ID、Token 签名和请求时间；需要保持服务时间准确。回调处理会投递 Kafka 消息，使用时还需启用 kafka profile 并配置可用 Broker。

### Kafka 与消息页面

消息服务的 [application-kafka.yml](poria-platform/poria-message/poria-message-biz/src/main/resources/application-kafka.yml) 提供消息模块的 Kafka 配置，默认未启用该附加配置。启用方式见通用配置，连接参数统一见[公共 Kafka 配置](#kafka)。

消息模块专有开关 `MESSAGE_KAFKA_ENABLED` 默认 false，仅控制监听器自动启动，不控制生产者或消息发送。

当前未发现 `@KafkaListener` 消费者，因此不能仅打开监听开关就认为消费链路已具备。站内信和公告不要求为此先启用腾讯 IM 或 Kafka 回调功能。

站内信和公告页面分别为 `/message/station-letter/index`、`/message/announcement/index`，通过 UPMS 菜单 path 动态注册。角色操作权限按需配置：

| 权限 | 用途 |
|---|---|
| `message_station_add` | 发送站内信 |
| `message_announcement_add` | 新增公告 |
| `message_announcement_edit` | 编辑、发送、撤回公告 |
| `message_announcement_del` | 删除公告 |
| `sys_user_page` | 发送时查询、选择接收人，沿用用户查询的数据范围 |

查看入口由角色默认权限规则保留。站内信与公告发送均提交 `userIds` 字符串列表；公告草稿无需选择接收人，发送时再选择。`allToRead` 无需参数，只处理当前用户未读的公告（98）与站内信（99）。

`poria-message-api` 提供 MessageApi 和模型，调用方注册客户端并使用 `ServiceNameConstants.MESSAGE_SERVICE`；引用 API 依赖不会自动启动消息服务。

## poria-job

定时任务分为另外部署的 XXL-Job Admin 和本模块执行器。当前依赖 `xxl-job-core` 为 2.3.1，Admin 应使用相匹配版本。

| 配置 | 当前值 / 配置要求 |
|---|---|
| `xxl.job.admin.addresses` / `XXL_HOST`、`XXL_PORT` | 默认 `http://poria-xxl:9080/xxl-job-admin`，执行器可达 |
| `xxl.job.accessToken` | 与 Admin 的通信 Token 一致，按实际环境覆盖 |
| `xxl.job.executor.appname` | 当前 xxl-job-executor-sample，必须与 Admin 执行器分组 AppName 一致 |
| `xxl.job.executor.address` | 向 Admin 注册的执行器完整地址，当前空 |
| `xxl.job.executor.ip` | 注册 IP，当前空；容器、多网卡环境填写 Admin 可达的 IP |
| `xxl.job.executor.port` | 当前 9999，同机有网关时改为 9998 等可用端口 |
| `xxl.job.executor.logpath` | 当前 /data/applogs/xxl-job/jobhandler，运行账号可写 |
| `xxl.job.executor.logretentiondays` | 日志保留天数，当前 30 |

执行器示例，替换地址和 Token，并在 Admin 配置相同 AppName：

```yaml
xxl:
  job:
    accessToken: '<与Admin一致的Token>'
    admin:
      addresses: 'http://<Admin地址>:9080/xxl-job-admin'
    executor:
      appname: poria-job
      address: 'http://<Admin可达的执行器IP>:9998/'
      ip: '<Admin可达的执行器IP>'
      port: 9998
      logpath: /data/applogs/xxl-job/jobhandler
      logretentiondays: 30
```

执行器与 Admin 需要双向可达。当前仓库没有实际 `@XxlJob` 任务方法，启用业务任务还需实现方法，并在 Admin 填写对应 JobHandler、Cron、参数、超时和重试。任务调用远程服务时检查业务返回并设计重复执行保护。

Admin 容器内部端口为 8080，实际以部署值为准。前端内嵌页面的地址与登录要求见 Vue 前端章节。

## Vue 前端

配置位于 `vue/.env.development`、`vue/.env.production`；变量在开发进程启动或生产构建时读取。

| 环境变量 | 何时配置 | 作用与要求 |
|---|---|---|
| `VITE_API_PREFIX` | 必须核对 | API 前缀或完整地址；当前代码对空字符串回退为 /api |
| `VITE_GATEWAY_TARGET` | 仅开发环境 | Vite 代理目标，当前 `http://localhost:9999`；生产不使用它创建代理 |
| `VITE_AUTH_CLIENT_ID` | 必须 | 与 UPMS OAuth 客户端 ID 一致 |
| `VITE_AUTH_CLIENT_SECRET` | 必须 | 与客户端凭据一致；VITE_* 会进入浏览器产物 |
| `VITE_AUTH_USER_TYPE` | 当前 ADMIN_PWD | 登录用户类型 |
| `VITE_AUTH_SCOPE` | 当前 server | OAuth scope |
| `VITE_XXL_JOB_ADMIN_URL` | 使用定时任务页时 | 浏览器可达的完整 Admin 页面地址，包含 /xxl-job-admin/ |

开发环境示例：

```dotenv
VITE_API_PREFIX=/api
VITE_GATEWAY_TARGET=http://localhost:9999
VITE_AUTH_CLIENT_ID=<客户端ID>
VITE_AUTH_CLIENT_SECRET=<客户端凭据>
VITE_AUTH_ENCODE_KEY=<与后端一致的16字节密钥>
VITE_AUTH_USER_TYPE=ADMIN_PWD
VITE_AUTH_SCOPE=server
VITE_XXL_JOB_ADMIN_URL=
```

开发代理将 `/api/auth/plat/login` 转为 `http://localhost:9999/auth/plat/login`。开发配置变更后由部署人员重启已有前端实例。

生产选择以下一种 API 地址方式：

| 方式 | 配置与站点要求 |
|---|---|
| 前后端同域 | `VITE_API_PREFIX=/api`；站点将 /api/ 转发到网关，去掉 /api 前缀 |
| 独立后端域名 | `VITE_API_PREFIX=https://<后端域名>`；若网关入口含 /api，地址末尾也加 /api，并配置跨域 |

生产站点部署 `dist` 内容，根目录直接包含 index.html 和 assets；前端页面路径刷新需回退到 index.html。Nginx 的页面 location 可使用 `try_files $uri $uri/ /index.html;`，API 转发单独配置。登录加密使用 Web Crypto，生产页面需满足浏览器安全上下文要求，使用 HTTPS。

定时任务固定页面为 `/admin/job/index`，配置 `VITE_XXL_JOB_ADMIN_URL=https://<Admin域名>/xxl-job-admin/` 后通过 iframe 展示。Admin 单独登录，需要允许 iframe 和相应登录 Cookie；HTTPS 平台使用 HTTPS Admin。该地址不经过 API 代理，不附加平台 Token。

生产变量变更后由部署人员重新构建和部署。前端现有命令如下，均在 `vue` 目录执行：

```powershell
npm run dev
npm run build
npm run lint
```

分别用于开发、生产打包和代码检查；生产由静态站点服务提供页面。

## 公共模块

以下模块都不独立监听端口，参数放在实际引用它们的业务服务中。

| 模块 | 用途 | 配置位置与接入要求 |
|---|---|---|
| `poria-common-bom` | pom.xml 中的 poria.version 及各依赖版本 | 只管理构建依赖，不部署组件；模块显式版本优先核对 |
| `poria-common-core` | 公共工具和统一返回 | 调用地址见通用配置；完整链路追踪见下文 |
| `poria-common-data` | 数据访问、字典转换和数据范围 | 默认数据范围需要 UPMS，参数见下文 |
| `poria-common-feign` | 服务间 HTTP 客户端 | 注册客户端并提供认证，超时配置见下文 |
| `poria-common-security` | 接口认证、权限及 XSS 处理 | 宿主接入平台认证，参数见下文 |
| `poria-common-swagger` | 接口文档辅助 | 相关开关与路径见通用配置，无需独立文档服务 |
| `poria-common-gateway` | 动态路由组件 | 由网关引用，配置见网关章节 |
| `poria-common-log` | 日志与追踪 | Kafka 投递和追踪按需接入，参数见下文 |
| `poria-common-kafka` | 消息生产者与发送工具 | Broker、主题和消费端按需准备，参数见下文 |
| `poria-common-seata` | 分布式事务客户端 | 使用时准备 Seata Server，参数见下文 |
| `poria-common-excel` | Excel 导入导出 | 模板配置见下文，上传限制及业务校验由宿主提供 |

### Feign、数据访问、安全与接口文档

Feign 可在宿主服务配置 `spring.cloud.openfeign.client.config.default.connectTimeout` 和 `readTimeout`，单位毫秒；当前仓库未显式设置，可按业务增加，例如 3000、15000。只有接口定义而没有注册客户端，不会自动发起调用；用户令牌透传需要相应请求上下文，定时任务没有浏览器令牌可自动透传。

公共 data 的 [application.yml](poria-platform/poria-common/poria-common-data/src/main/resources/application.yml) 给出 Mapper、类型处理器及 not_empty 字段写入策略。宿主服务已有自己的 Mapper 路径时，按实际文件位置配置。当前分页组件最大分页量 1000；数据范围依赖具体查询接入，不是统一多租户隔离。

`security.oauth2.ignore.urls` 只补充匿名路径；XSS 的 `mica.xss.enabled` 默认启用、`mica.xss.mode` 默认 clear，form、jackson 子项可配置字符串处理。`@Inner` 同时隐藏接口文档，内部调用仍需按当前认证机制接入。

Excel 使用 `excel.template-path` 指定模板资源目录，默认 excel；接口通过 `@RequestExcel`、`@ResponseExcel` 指定导入参数、标题行和导出模板，不需要独立 Excel 服务。

### Kafka

公共配置位于 [application-kafka.yml](poria-platform/poria-common/poria-common-kafka/src/main/resources/application-kafka.yml)。只有启用 kafka profile 才创建 `poriaKafkaTemplate`；连接配置和消息模块差异统一如下。

| 配置 | 公共库默认 / 覆盖方式 | 消息服务默认 / 覆盖方式 |
|---|---|---|
| `spring.kafka.bootstrap-servers` / `KAFKA_BOOTSTRAP_SERVERS` | poria-kafka:9096 | localhost:9092 |
| `spring.kafka.poria-producer.key-serializer`、`value-serializer` | StringSerializer | StringSerializer |
| `spring.kafka.poria-producer.properties.security.protocol` | SASL_PLAINTEXT | PLAINTEXT；用 `KAFKA_SECURITY_PROTOCOL` 覆盖 |
| `spring.kafka.poria-producer.properties.sasl.mechanism` | SCRAM-SHA-512 | SCRAM-SHA-512；用 `KAFKA_SASL_MECHANISM` 覆盖 |
| `spring.kafka.poria-producer.properties.sasl.jaas.config` | JAAS 认证字符串，可用 `KAFKA_USERNAME`、`KAFKA_PASSWORD` 配置凭据 | 默认空；用 `KAFKA_SASL_JAAS_CONFIG` 提供完整认证字符串 |
| `spring.kafka.consumer.*` | 按需配置反序列化、认证及消费组，并实现消费者 | 按需配置反序列化、认证及消费组，并实现消费者 |

公共生产者当前只转交代码显式读取的参数，不能假定 YAML 中所有扩展项均生效。普通发送未检查异步结果；延时发送仅向 PORIA_DELAY_QUEUE 写入消息和消息头，仓库未提供完整延时转发消费者。

### 日志与追踪

[application-log.yml](poria-platform/poria-common/poria-common-log/src/main/resources/application-log.yml) 配合 log profile，将日志投递到 poria-log 主题；Logback 使用单独一套 Kafka 日志参数。

| 配置 / 环境变量 | 配置要求 |
|---|---|
| `KAFKA_HOST` | Logback 的 Broker 地址，与业务消息的 KAFKA_BOOTSTRAP_SERVERS 分开配置 |
| `HOSTNAME`、`CONTEXT_NAME` | 节点和应用标识 |
| `LOGGING_PATH` | 日志初始化路径，未提供时回退 logs，运行账号可写 |
| `management.server.port` | log profile 当前设置 7888，同机多个应用需分别覆盖 |
| `management.endpoints.web.exposure.include` / `ACTUATOR_ENDPOINTS` | 按需暴露的管理端点，当前变量默认空 |
| `sw.enabled` / `SW_ENABLED` | 默认 false，控制自定义 TraceId 过滤器，不会自动部署追踪系统 |

完整追踪需要实际加载 SkyWalking Agent 并配置服务名及 OAP。Logback 的 kafka-doris 只是 appender 名称，仓库未因此提供 Doris 或完整日志消费链路；操作日志切面的发送存在注释逻辑，仅部署 Kafka 不会自动完成审计落库。

### Seata

配置位于 [seata-config.yml](poria-platform/poria-common/poria-common-seata/src/main/resources/seata-config.yml)，由配置类显式加载。当前 UPMS 的 Seata 依赖被注释，仓库未见 `@GlobalTransactional` 业务入口。

| 配置 | 当前值 / 要求 |
|---|---|
| `seata.enabled` | true，仅在实际引入本库时生效 |
| `seata.tx-service-group` | pigx_tx_group |
| `seata.service.vgroup-mapping.pigx_tx_group` | default，分组名与映射键保持一致 |
| `seata.service.grouplist.default` / `SEATA_SERVER` | poria-seata:8091，替换为实际 TC 地址 |
| `seata.registry.type`、`seata.config.type` | file |
| `seata.registry.file.name`、`seata.config.file.name` | file.conf；仓库未提供该文件，按最终客户端版本补充 |
| `seata.client.*` | 锁、提交、回滚重试等，按事务模式配置 |

Seata POM 显式使用 Alibaba starter 2025.0.0.0，不能以 BOM 中旧 seata.version 属性认定最终版本。需要真实事务入口、参与方及代理接入后再验证分布式事务。

## 构建与页面维护

后端在 `poria-platform` 目录使用 Maven。仅构建指定模块时可使用以下形式，由部署人员按需执行：

```powershell
mvn -pl poria-message/poria-message-biz -am -DskipTests package
```

其他独立应用替换 `-pl` 路径，prod 构建按需增加 `-Pprod`。执行构建前准备相应环境及依赖，产物位于各应用 target 目录。本文不代表服务已启动或配置已通过运行验证。

前端管理页面位于 `vue/src/views/admin/<业务目录>/index.vue`，请求放同目录 api.js，编辑弹窗使用 EditDialog.vue；消息页面位于 `vue/src/views/message`，个人中心位于 `vue/src/views/profile`。新增业务页面还需在 UPMS 维护菜单 path 和角色权限。

公共 HTTP、认证、菜单路由及按钮权限位于 `vue/src/core`；Element Plus 注册与样式入口位于 `vue/src/ui`，主题及公共弹窗样式位于 `vue/src/styles/index.css`。页面专属表单和校验继续在对应业务目录维护。
