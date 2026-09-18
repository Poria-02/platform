# Poria Vue 管理台

此项目以官方 **RuoYi-Vue3 3.9.2** 为工程基线，保留其 Layout、TagsView、主题、权限指令、路由守卫、Pinia 状态管理和 Element Plus 组件体系，并适配 Poria 微服务后端。

## 已对接

- Poria Gateway：开发环境的 `/api` 自动代理到 `http://localhost:9999`。
- Poria Auth：以 OAuth2 Bearer Token 登录；支持回调参数 `access_token` / `token`，也支持令牌粘贴登录。
- Poria UPMS：用户、角色的查询/新增/编辑/删除；菜单、部门、字典、网关路由、日志、客户端和在线令牌的数据列表。
- 权限：令牌存在时加载 `/menu`，保留若依动态路由、侧边栏、标签页与 `v-hasPermi` 机制。Poria 后端菜单路径与官方组件路径不同，因此前端将已知 Poria 菜单映射到本项目中的管理页面。

## 本地启动

```powershell
cd D:\work\Project\poria\poria-vue
npm install
npm run dev
```

先启动 Poria Gateway（默认端口 `9999`），然后访问 Vite 输出的本地地址。网关地址可在 `.env.development` 的 `VITE_GATEWAY_TARGET` 修改。

## 生产配置

在发布前修改 `.env.production`：

```properties
VITE_APP_BASE_API = 'https://你的-gateway-地址'
VITE_AUTH_LOGIN_URL = 'https://你的-gateway-地址/token/login'
```

随后执行：

```powershell
npm run build:prod
```

不要在浏览器端配置 OAuth2 客户端密钥。若需完整的授权码 + PKCE 回调流程，请先在 Poria 的 `sys_oauth_client_details` 注册前端回调地址和授权范围。

## 迁移说明

原先手工适配版已保留在 `D:\work\Project\poria\poria-vue-custom-backup`，当前 `poria-vue` 是正式的官方 RuoYi-Vue3 基线版本。
