export const pageConfigs = {
  user: { title: '用户管理', idKey: 'userId', columns: [['username', '用户名'], ['phone', '手机号'], ['deptName', '部门'], ['lockFlag', '锁定状态'], ['createTime', '创建时间']], queryFields: [['username', '用户名'], ['phone', '手机号']], formFields: [['username', '用户名', true], ['password', '密码'], ['phone', '手机号'], ['deptId', '部门 ID', false, 'number']] },
  role: { title: '角色管理', idKey: 'roleId', columns: [['roleName', '角色名称'], ['roleCode', '角色编码'], ['roleDesc', '描述'], ['createTime', '创建时间']], queryFields: [['roleName', '角色名称'], ['roleCode', '角色编码']], formFields: [['roleName', '角色名称', true], ['roleCode', '角色编码', true], ['roleDesc', '描述', false, 'textarea']] },
  dept: { title: '部门管理', idKey: 'deptId', tree: true, paged: false, columns: [['name', '部门名称'], ['parentId', '上级部门'], ['sort', '排序'], ['type', '类型']], queryFields: [['name', '部门名称']], formFields: [['name', '部门名称', true], ['parentId', '上级部门 ID', false, 'number'], ['sort', '排序', false, 'number'], ['type', '类型', false, 'number']] },
  menu: { title: '菜单管理', idKey: 'menuId', tree: true, paged: false, columns: [['name', '名称'], ['path', '页面路径'], ['permission', '权限标识'], ['type', '类型'], ['platform', '平台'], ['sort', '排序']], queryFields: [['name', '名称'], ['platform', '平台']], formFields: [['name', '名称', true], ['path', '页面路径', true], ['permission', '权限标识'], ['parentId', '上级菜单 ID', false, 'number'], ['icon', '图标'], ['type', '类型', true], ['platform', '平台'], ['sort', '排序', false, 'number']] },
  dict: { title: '字典管理', idKey: 'id', columns: [['type', '字典类型'], ['description', '描述'], ['system', '系统'], ['remarks', '备注'], ['createTime', '创建时间']], queryFields: [['type', '字典类型'], ['description', '描述']], formFields: [['type', '字典类型', true], ['description', '描述', true], ['system', '系统'], ['remarks', '备注', false, 'textarea']] },
  treeDict: { title: '树形字典管理', idKey: 'id', columns: [['code', '字典编码'], ['name', '字典名称'], ['description', '描述'], ['createTime', '创建时间']], queryFields: [['code', '字典编码'], ['name', '字典名称']], formFields: [['code', '字典编码', true], ['name', '字典名称', true], ['description', '描述', false, 'textarea']] },
  param: { title: '公共参数管理', idKey: 'publicId', columns: [['publicName', '参数名称'], ['publicKey', '参数键'], ['publicValue', '参数值'], ['status', '状态'], ['system', '系统']], queryFields: [['publicName', '参数名称'], ['publicKey', '参数键']], formFields: [['publicName', '参数名称', true], ['publicKey', '参数键', true], ['publicValue', '参数值', true, 'textarea'], ['status', '状态'], ['system', '系统']] },
  tag: { title: '标签管理', idKey: 'id', columns: [['tagKey', '标签键'], ['name', '标签名称'], ['createBy', '创建人'], ['createTime', '创建时间']], queryFields: [['tagKey', '标签键'], ['name', '标签名称']], formFields: [['tagKey', '标签键', true], ['name', '标签名称', true]] },
  client: { title: 'OAuth 客户端管理', idKey: 'id', columns: [['clientId', '客户端 ID'], ['scope', '作用域'], ['authorizedGrantTypes', '授权方式'], ['accessTokenValidity', '令牌有效期']], queryFields: [['clientId', '客户端 ID']], formFields: [['clientId', '客户端 ID', true], ['clientSecret', '客户端密钥', true], ['scope', '作用域', true], ['authorizedGrantTypes', '授权方式'], ['webServerRedirectUri', '回调地址'], ['accessTokenValidity', '令牌有效期', false, 'number'], ['refreshTokenValidity', '刷新令牌有效期', false, 'number']] },
  route: { title: '网关路由管理', idKey: 'id', paged: false, columns: [['routeId', '路由 ID'], ['routeName', '路由名称'], ['uri', '目标地址'], ['order', '顺序']], queryFields: [['routeId', '路由 ID'], ['routeName', '路由名称']], formFields: [['routeId', '路由 ID', true], ['routeName', '路由名称', true], ['uri', '目标地址', true], ['predicates', '断言', true, 'textarea'], ['filters', '过滤器', false, 'textarea'], ['order', '顺序', false, 'number']] },
  social: { title: '社交登录配置', idKey: 'id', columns: [['type', '类型'], ['appId', '应用 ID'], ['redirectUrl', '回调地址'], ['remark', '备注']], queryFields: [['type', '类型'], ['appId', '应用 ID']], formFields: [['type', '类型', true], ['appId', '应用 ID', true], ['appSecret', '应用密钥', true], ['redirectUrl', '回调地址'], ['remark', '备注', false, 'textarea']] },
  token: { title: '令牌管理', idKey: 'token', columns: [['token', '令牌'], ['clientId', '客户端'], ['userName', '用户'], ['expiresAt', '过期时间']], queryFields: [['clientId', '客户端'], ['userName', '用户']], formFields: [] },
  log: { title: '操作日志', idKey: 'id', columns: [['title', '标题'], ['type', '类型'], ['createBy', '操作人'], ['requestUri', '请求地址'], ['createTime', '创建时间']], queryFields: [['title', '标题'], ['createBy', '操作人']], formFields: [] },
  sensitiveLog: { title: '敏感数据日志', idKey: 'id', columns: [['title', '标题'], ['createBy', '操作人'], ['requestUri', '请求地址'], ['createTime', '创建时间']], queryFields: [['title', '标题'], ['createBy', '操作人']], formFields: [] }
}

export function toCrudProps(config, api, methods) {
  const normalize = entries => entries.map(([prop, label, required = false, type = 'text']) => ({ prop, label, required, type }))
  return {
    ...config,
    columns: config.columns.map(([prop, label, width]) => ({ prop, label, width })),
    queryFields: normalize(config.queryFields),
    formFields: normalize(config.formFields),
    fetcher: api[methods.fetch],
    saver: methods.save ? api[methods.save] : undefined,
    updater: methods.update ? api[methods.update] : undefined,
    remover: methods.remove ? api[methods.remove] : undefined
  }
}
