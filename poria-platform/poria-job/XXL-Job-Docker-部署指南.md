# XXL-Job 端口与 SQL 说明

管理平台已在“系统管理 → 定时任务”提供内嵌入口，页面路径为 `/admin/job/index`。进入平台需要正常登录，进入 XXL-Job 界面后仍使用 XXL-Job 自己的账号登录。

## 端口

| 服务 | 使用端口 | 说明 |
|---|---|---|
| XXL-Job Admin 控制台 | **9080** | 浏览器访问 `http://服务器地址:9080/xxl-job-admin/`；使用官方 Docker 镜像时，容器内部端口为 8080 |
| `poria-job` 应用 | **5002** | Spring Boot 应用端口，不是 XXL-Job 控制台 |
| `poria-job` 执行器通信 | **9998（建议）** | Admin 调度执行器使用；源码的 `xxl.job.executor.port` 目前为 9999，部署时需改为 9998，避免与网关同机冲突 |
| Admin 使用的 MySQL | **3306** | 保存任务配置与调度日志，仅需供 Admin 访问 |

内嵌页面指向 Admin 的 **9080**，不是执行器的 5002 或 9998。实际端口可以调整，调整后同步修改相关地址配置。

## 官方初始化 SQL

当前项目的 `xxl-job-core` 版本为 **2.3.1**，Admin 和初始化 SQL 使用对应版本。

- 文件名：`tables_xxl_job.sql`。
- 官方源码路径：`doc/db/tables_xxl_job.sql`。
- 获取位置：[XXL-Job 2.3.1 官方 SQL 文件](https://github.com/xuxueli/xxl-job/blob/2.3.1/doc/db/tables_xxl_job.sql)。
- 用途：初始化 Admin 使用的 `xxl_job` 数据库，包含任务、执行器、用户和日志等表。

使用可信的对应版本源码包时，也可以在上述路径取得该文件。本仓库的 `poria_base.sql` 不能代替它。

本次未联网下载或核验该链接，未执行 SQL。

## 前端界面地址

在前端 `.env.development` 或 `.env.production` 中填写：

```dotenv
VITE_XXL_JOB_ADMIN_URL=http://服务器地址:9080/xxl-job-admin/
```

将服务器地址替换为浏览器实际可访问的地址，保留 `/xxl-job-admin/`。只需这一项界面地址配置，不需要新增前端代理配置。

- 地址为空时，页面显示未配置提示。
- HTTPS 管理平台应配置 HTTPS Admin 地址。
- 内嵌页面不自动登录，也不传递平台 Token；Admin 需要允许 iframe 嵌入，浏览器需要允许其登录 Cookie。
- 修改开发环境变量后由你重启已有前端实例；修改生产环境变量后由你重新打包并部署。

本文件仅说明端口、SQL 获取位置及前端地址，不包含部署步骤。
