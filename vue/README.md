# Poria 控制台

Vue 3 + Vite + Vue Router + Pinia + Element Plus 管理端。前端只维护菜单页面和接口调用，服务由网关提供。

## 本地运行

```powershell
npm install
npm run dev
```

默认地址为 http://localhost:8000。开发代理将 `/api/auth/**`、`/api/upms/**`、`/api/message/**`、`/api/base/**`、`/api/log/**` 转发到 `http://localhost:9999`，只去掉本地代理前缀 `/api`，保留服务前缀。环境仅有 `.env.development` 和 `.env.production`。

## 页面约定

`poria-upms.sql` 的 `sys_menu` 表仅用于确定 `platform=admin` 的菜单 path。运行时登录后从 `GET /upms/menu?platform=admin` 获取当前账号的菜单与权限。示例：`/admin/user/index` 对应 `src/views/admin/user/index.vue`，接口只写在同目录的 `api.js`。目录菜单会跳到第一个可用子页，按钮菜单不生成路由，找不到实现的 path 会显示明确的缺页信息。

已实现 SQL 中有效的 admin 页面：用户、菜单、角色、操作日志、字典、参数、终端、动态路由、分级字典、标签、版本、敏感信息日志。SQL 中令牌菜单的 `del_flag=1`，不生成页面。版本页使用网关的 `/base/api/version`；其他管理页使用 UPMS 接口。

## 请求契约

UPMS 等业务接口返回 `{code,msg,data}`，HTTP 层只在 `code===0` 时向页面返回 `data`。认证服务的 OAuth 令牌响应单独处理。登录、验证码和登出接口位于 `src/views/login`。会话只保存在当前浏览器标签页。

`npm run lint` 执行 ESLint。生产环境按实际网关地址配置 `VITE_API_PREFIX` 和 OAuth 客户端参数。
