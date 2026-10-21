# Poria 控制台启动与部署

以下命令在 PowerShell 7 中执行，需要先安装 Node.js 和 npm。

## 页面维护

管理页面分别在 `src/views/admin/<业务目录>/index.vue` 中维护查询、表格、分页和页面操作，不再由通用列表组件统一生成。编辑弹窗位于同目录的 `EditDialog.vue`，自行维护该业务的表单、校验和保存请求；字典和标签的子项列表、子项编辑分别位于 `ItemsDialog.vue`、`ItemEditDialog.vue`。角色菜单权限使用 `role/MenuPermissionDialog.vue`，日志和令牌详情使用各自目录的 `DetailDialog.vue`。

页面继续使用 Element Plus 基础控件、公共认证和 HTTP 请求工具。查询栏不带外层卡片；弹窗的配色、间距、滚动和响应式样式在 `src/styles/index.css` 中维护，业务布局在对应弹窗文件中维护。

当前登录页和管理平台采用黑白极简风格：白色导航、黑色选中态及主要按钮、灰阶表格和小圆角弹窗。全局主题颜色由 `src/styles/index.css` 中的 `--accent`、`--ink`、`--muted`、`--line` 等变量维护；日历、天气和图标选择器在各自组件中维护布局。错误提示、节假日等表示业务状态的颜色继续保留。XXL-Job 内嵌页面由调度中心自身控制样式，不继承平台主题。

## 交互与加载维护

操作按钮通过 `src/core/permissions.js` 对照接口权限控制显示，保存逻辑继续检查权限；部门页保留自身的权限配置。新增或调整操作时，应同步维护权限标识。

标签页最多缓存 12 个页面实例；关闭标签会释放对应缓存，重新打开时重新加载。超过缓存上限后，较早访问页面的查询和表单状态可能重置。菜单加载失败时可在侧栏点击“重试”。

列表请求通过 `src/core/requestController.js` 取消被新查询替代的请求，并在页面卸载时取消未完成的请求。字典通过 `src/core/dictionaries.js` 按类型共享缓存，缓存有效期为 5 分钟；同类型并发请求会合并，切换登录账号或修改字典后清除缓存。列表和字典独立加载，字典失败可通过页面的重试按钮重新加载。

管理列表通过 `src/core/listRouteState.js` 同步筛选和分页：点击查询、重置、刷新或翻页后更新地址栏，刷新页面和打开同一链接时恢复对应条件。页码使用 `current`，每页条数使用 `size`，默认第一页和每页 10 条时省略这两个参数；无效分页值回退到默认值。标签页保存完整查询地址，切换标签时保留已提交的条件，输入但尚未查询的内容不会写入地址栏。

Element Plus 的组件、指令、服务及样式按需维护在 `src/ui/elementPlus.js`，全局中文语言和弹窗动画由 `src/App.vue` 的配置组件提供。新增 Element Plus 控件时需补充对应注册和样式。

## 工作台全部功能

工作台欢迎区标题右侧提供“全部功能”入口，打开 `/functions/index`。页面从当前账号已授权的菜单生成卡片，按菜单上级目录分组；按钮权限项、重复路径和没有对应前端页面的菜单不展示。平台已有的定时任务入口保持可见，个人中心在具备 `sys_user_profile` 时显示。

输入名称、分类或路径即可搜索，也可使用“全部分类”筛选；点击卡片进入对应业务页面，外部链接在新窗口打开。点击卡片右侧图钉可固定或取消常用入口，在“常用功能”中集中查看。固定记录仅保存到当前浏览器的本地存储，按登录用户名区分，不上传后端。

搜索匹配文字会在名称、分类和路径中高亮。按 `/` 聚焦搜索，按 `Esc` 清空关键词；正在其他输入框输入、使用输入法组合文字或打开弹窗时不会抢占焦点。功能页的关键词、分类和视图也会同步到地址栏。触屏或窄屏下，图钉点击区域扩大到 44×44 像素。

菜单信息变更后点击“刷新菜单”重新加载。角色操作权限调整后需退出并重新登录，以更新令牌中的权限；新功能需先实现对应的前端页面和接口文件，再配置菜单并授权给角色。

## 个人中心

左下角账号卡片进入 `/profile/index`，右侧图标单独退出登录。页面、请求和修改弹窗分别位于 `src/views/profile/index.vue`、`api.js`、`EditDialog.vue`，按独立页面维护。

查询使用 `GET /upms/user/me`，仅返回本人资料，需要 `sys_user_profile`。修改手机号和密码直接使用原有 `PUT /upms/user/edit`，保持原接口实现，需要 `sys_user_edit`；前端提交本人用户名、原头像，以及手机号或加密后的原密码、新密码。修改密码成功后退出并重新登录。部门和角色由管理员维护。

在其他环境中，需要在菜单管理中登记“查看个人中心”操作权限 `sys_user_profile`（按钮类型 `1`、所属平台 `admin`），并把它和已有的“用户修改”权限 `sys_user_edit` 追加到所有角色原有菜单权限中。`sys_user_edit` 同时被用户管理的更新接口共用。无需改表；新增查询接口需要由部署人员重新构建并启动 UPMS。已登录账号需重新登录获得新的权限。

新版 UPMS 会在新增角色和保存角色菜单权限时自动保留这两项默认权限。该规则由后端执行，角色权限弹窗只提示这一行为；部署新版 UPMS 后生效。当前环境的所有角色也应补齐这两项授权，授权完成后需退出并重新登录，以更新当前令牌携带的权限。

## 消息中心

站内信和公示公告由菜单管理中的 `path` 动态注册路由，不再在前端固定注入。页面路径分别为 `/message/station-letter/index` 和 `/message/announcement/index`，所属平台为 `admin`。UPMS 菜单初始化脚本为 `../poria-platform/poria-upms/poria-upms-biz/src/main/resources/db/message_center_menu.sql`，需在 UPMS 数据库执行；脚本可重复执行，保留已有授权并为所有现有角色补齐查看入口。新增角色和保存角色权限时，后端继续保留这两个页面及其上级菜单。

消息查看、分页、未读数量和已读操作无需按钮权限。系统管理员通过角色菜单权限配置 `message_station_add`（发送站内信）、`message_announcement_add`（新增公告）、`message_announcement_edit`（编辑、发送、撤回公告）和 `message_announcement_del`（删除公告），普通角色仅保留查看入口。

站内信、我的公告及公告管理的关键词查询均按标题或内容模糊匹配。

站内信和公告发送均由前端提交 `userIds` 字符串列表。接收人支持按用户名搜索、多选、清空，以及“添加所有用户”；后者分页读取当前账号有权查询的所有未删除用户并添加到当前选择中，不受搜索关键词限制。接收人查询复用现有 `GET /upms/user/page`，使用 `username` 参数搜索，沿用 `sys_user_page` 权限和用户查询的数据范围；前端仅使用用户ID和用户名。公告保存草稿不要求选择接收人，每次发送或再次发送时重新选择。

`POST /message/api/pending/task/allToRead` 及 App 对应接口无需传参，默认将当前用户未读的公告（98）和站内信（99）全部标记为已读，不处理其他业务待办。

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

## XXL-Job 定时任务界面

已在“系统管理”下提供固定的“定时任务”入口，页面路径为 `/admin/job/index`，无需再往菜单数据库添加记录。入口面向已登录的平台用户，访问页面仍经过平台登录校验；XXL-Job 内部操作权限由 Admin 自己管理。

在 `.env.development` 或 `.env.production` 中填写唯一的界面地址配置：

```dotenv
VITE_XXL_JOB_ADMIN_URL=http://服务器地址:9080/xxl-job-admin/
```

把示例地址替换为浏览器实际可访问的 Admin 地址，包含 `/xxl-job-admin/`。地址为空时页面显示未配置提示，不会请求调度中心。该地址直接用于 iframe，不经过平台的 API 代理，也不会附加平台访问令牌；用户在内嵌界面中使用 XXL-Job 自己的账号正常登录。

环境变量变更后，开发环境由你重启已有前端实例，生产环境由你重新打包并部署。HTTPS 平台应配置 HTTPS 调度中心地址。Admin 或其代理需允许平台页面通过 iframe 嵌入，浏览器也需允许其登录 Cookie；跨站 Cookie 被拦截时会影响登录，前端地址配置不能绕过这些浏览器限制。

端口和官方初始化 SQL 获取位置见 [XXL-Job 端口与 SQL 说明](../poria-platform/poria-job/XXL-Job-Docker-部署指南.md)。
