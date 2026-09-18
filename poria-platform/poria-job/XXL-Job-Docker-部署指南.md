# XXL-Job Admin Docker 部署指南

本文只部署 XXL-Job Admin 调度中心及其 MySQL 数据库，不包含任何业务执行器。Admin 用于维护执行器分组、Cron、任务配置和调度日志。

```text
浏览器 → xxl-job-admin:9080 → MySQL:3306（xxl_job 库）
```

## 1. 前置条件

- Docker Engine 和 Docker Compose v2。
- 建议使用 XXL-Job Admin `2.3.1`。实际版本应与未来接入的执行器 `xxl-job-core` 版本保持一致。
- 从对应 XXL-Job Admin 发行包取得 `db/tables_xxl_job.sql`，用于初始化调度中心表。

建议创建下列目录：

```text
deploy/xxl-job/
├── docker-compose.yml
└── mysql/init/tables_xxl_job.sql
```

`tables_xxl_job.sql` 创建的是 `xxl_job_info`、`xxl_job_group`、`xxl_job_log` 等调度中心表。建议使用独立的 `xxl_job` 数据库，不要混入业务表。

## 2. Docker Compose 配置

将以下内容保存为 `deploy/xxl-job/docker-compose.yml`。示例中的密码与 Token 必须替换为随机强值；生产环境建议用 Docker Secret 或受权限控制的 `.env` 文件注入，且不要提交到代码仓库。

```yml
services:
  mysql:
    image: mysql:8.4
    container_name: xxl-job-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: CHANGE_ME_ROOT_PASSWORD
      MYSQL_DATABASE: xxl_job
      MYSQL_USER: xxl_job
      MYSQL_PASSWORD: CHANGE_ME_DB_PASSWORD
      TZ: Asia/Shanghai
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    volumes:
      - xxl-job-mysql-data:/var/lib/mysql
      - ./mysql/init:/docker-entrypoint-initdb.d:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-pCHANGE_ME_ROOT_PASSWORD"]
      interval: 10s
      timeout: 5s
      retries: 12

  xxl-job-admin:
    image: xuxueli/xxl-job-admin:2.3.1
    container_name: xxl-job-admin
    restart: unless-stopped
    depends_on:
      mysql:
        condition: service_healthy
    ports:
      - "9080:8080"
    environment:
      TZ: Asia/Shanghai
      PARAMS: >-
        --server.port=8080
        --spring.datasource.url=jdbc:mysql://mysql:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
        --spring.datasource.username=xxl_job
        --spring.datasource.password=CHANGE_ME_DB_PASSWORD
        --xxl.job.accessToken=CHANGE_ME_TO_A_RANDOM_TOKEN

volumes:
  xxl-job-mysql-data:
```

说明：

- Compose 网络内，Admin 使用 `mysql:3306` 访问数据库；宿主机端口映射与容器内部连接无关。
- Admin 的容器端口为 `8080`，这里映射为宿主机 `9080`，所以浏览器访问 `http://宿主机地址:9080/xxl-job-admin`。
- `xxl.job.accessToken` 是未来执行器注册和回调时必须一致的共享 Token。请保存到安全的密钥管理位置。
- 示例没有暴露 MySQL 的 `3306` 到宿主机；需要本机运维连接时可按实际安全策略增加端口映射。

## 3. 初始化并启动

1. 将 Admin 发行包的 `db/tables_xxl_job.sql` 放入 `deploy/xxl-job/mysql/init/`。
2. 替换 Compose 文件中的三项示例密钥：MySQL root 密码、数据库用户密码、XXL Token。
3. 在 `deploy/xxl-job` 目录运行：

```powershell
docker compose up -d
docker compose ps
docker compose logs --tail=200 xxl-job-admin
```

首次启动时，MySQL 容器会初始化空数据卷、创建 `xxl_job` 库，并执行挂载目录内的 SQL。随后 Admin 会连接该数据库启动。

## 4. 登录与基础配置

访问：

```text
http://宿主机地址:9080/xxl-job-admin
```

XXL-Job 2.3.1 的初始账号通常为 `admin` / `123456`。首次登录后应立即修改密码。

Admin 正常运行后，可以在控制台中：

1. 创建“执行器管理”分组；待执行器上线时使用自动注册或手动录入地址。
2. 创建“任务管理”记录，配置 Cron、路由策略、阻塞策略等。
3. 在“调度日志”中查看触发和执行结果。

仅部署 Admin 不会产生实际的任务执行：任务仍需有已部署且在线的 XXL-Job 执行器来接收调度请求。

## 5. 常用运维命令

```powershell
# 查看运行状态
docker compose ps

# 持续查看 Admin 日志
docker compose logs -f xxl-job-admin

# 重启 Admin（不删除数据库卷）
docker compose restart xxl-job-admin

# 停止并删除容器、网络，保留 MySQL 数据卷
docker compose down
```

生产环境不要随意执行 `docker compose down -v`，该命令会删除 MySQL 数据卷，导致任务配置和调度日志丢失。

## 6. 常见问题

### Admin 启动失败或不停重启

先执行：

```powershell
docker compose logs --tail=200 mysql
docker compose logs --tail=200 xxl-job-admin
```

重点检查 MySQL 是否健康、JDBC 地址是否为 `mysql:3306/xxl_job`、账号密码是否一致，以及初始化 SQL 是否成功执行。

### 页面可打开但没有任务数据

确认 `tables_xxl_job.sql` 已导入当前 Admin 连接的数据库。MySQL 初始化脚本只会在数据卷首次创建时自动运行；已有数据卷时，必须按数据库变更流程手工导入或迁移。

### 执行器无法注册

确认执行器配置的 Admin 地址可从执行器网络访问。例如，执行器也在同一 Compose 网络时，地址应为：

```text
http://xxl-job-admin:8080/xxl-job-admin
```

同时确认双方的 `xxl.job.accessToken` 完全一致。
