# Poria 控制台启动与部署

以下命令在 PowerShell 7 中执行，需要先安装 Node.js 和 npm。

## 本地启动

进入前端目录，首次使用时安装依赖：

```powershell
Set-Location D:\work\Project\platform\vue
npm install
```

在 `.env.development` 中配置后端网关地址：

```dotenv
VITE_API_PREFIX=/api
VITE_GATEWAY_TARGET=http://localhost:9999
```

启动前端：

```powershell
npm run dev
```

访问 `http://localhost:8000`。后端网关需已运行，开发代理会将 `/api/auth/plat/login` 转发为 `http://localhost:9999/auth/plat/login`。已有前端实例运行时无需重复启动；修改开发环境变量后，需自行重启现有实例。

## 生产部署

1. 配置 `.env.production`。前端通过页面部署域名访问；后端使用独立域名时，设置如下，将“后端部署域名”替换为实际域名：

   ```dotenv
   VITE_API_PREFIX=https://后端部署域名
   ```

   登录请求为 `https://后端部署域名/auth/plat/login`，不会自动添加 `/api`。若后端域名要求 `/api` 前缀，则在配置地址末尾加上 `/api`。同时确认文件中的 OAuth 客户端配置与后端一致。生产环境不需要 `VITE_GATEWAY_TARGET`。

   当前文件将 `VITE_API_PREFIX` 设为空，代码会回退到同域名的 `/api`；使用这种方式时，前端站点服务器必须将 `/api/` 请求转发到后端网关，并去掉 `/api` 前缀。

2. 在前端目录打包：

   ```powershell
   npm run build
   ```

3. 将生成的 `dist` 目录内容部署到前端静态站点，站点根目录应直接包含 `index.html` 和 `assets`。生产前端由 Nginx、IIS 等静态服务器提供，无需执行 `npm run dev`。站点需将未匹配到静态文件的页面路径回退到 `index.html`，保证刷新 `/login` 等页面正常；Nginx 可在页面的 `location /` 中使用 `try_files $uri $uri/ /index.html;`。同域名方式的 `/api/` 请求需单独代理到后端。

4. 配置域名解析和 HTTPS。使用独立后端域名时，后端网关或反向代理需允许页面部署域名跨域访问，允许接口所需的请求方法及 `Authorization`、`Content-Type` 请求头，并处理 `OPTIONS` 预检请求。

5. 访问页面部署域名，检查登录、验证码和业务接口。修改生产环境变量后，需重新打包并部署 `dist` 才会生效。
