import { computed } from 'vue'
import { useSession } from './session'

// 使用接口实际要求的权限标识，避免根据页面名称推断权限。
const permissions = {
  user: { create: 'sys_user_add', edit: 'sys_user_edit', remove: 'sys_user_del' },
  role: { create: 'sys_role_add', edit: 'sys_role_edit', remove: 'sys_role_del', menus: ['sys_role_perm', 'sys_menu_tree', 'sys_menu_role_tree'] },
  menu: { create: 'sys_menu_add', edit: 'sys_menu_edit', remove: 'sys_menu_del' },
  client: { create: 'sys_client_add', edit: 'sys_client_edit', remove: 'sys_client_delete', clear: 'sys_client_del' },
  dict: { create: 'sys_dict_add', edit: 'sys_dict_edit', remove: 'sys_dict_del', items: 'sys_dict_item_page', import: 'sys_dict_import', export: 'sys_dict_export' },
  dictItem: { create: 'sys_dict_item_add', edit: 'sys_dict_item_edit', remove: 'sys_dict_item_del' },
  treeDict: { create: 'sys_treedict_add', edit: 'sys_treedict_edit', remove: 'sys_treedict_del', items: 'sys_treedict_item_tree', import: 'sys_treedict_item_import', export: 'sys_treedict_item_export' },
  treeDictItem: { create: 'sys_treedict_item_add', edit: 'sys_treedict_item_edit', remove: 'sys_treedict_item_del' },
  tag: { create: 'admin_tag_add', edit: 'admin_tag_edit', remove: 'admin_tag_del', items: 'admin_tagItem_page' },
  tagItem: { create: 'admin_tagItem_add', edit: 'admin_tagItem_edit', remove: 'admin_tagItem_del' },
  param: { create: 'admin_syspublicparam_add', edit: 'admin_syspublicparam_edit', remove: 'admin_syspublicparam_del' },
  route: { create: 'sys_route_insert', edit: 'sys_route_insert', remove: 'sys_route_del', refresh: 'sys_route_edit' },
  version: { create: 'base_version_add', edit: 'base_version_edit', remove: 'base_version_del' },
  log: { remove: 'sys_log_del' },
  token: { remove: 'sys_token_del' },
  sensitiveLog: { detail: 'sys_sensitive_get' },
  messageTemplate: { read: 'message_template_page', detail: 'message_template_get', create: 'message_template_add', edit: ['message_template_edit', 'message_template_get'], remove: 'message_template_del', status: 'message_template_status' },
  messageSmsLog: { read: 'message_log_sms' },
  messageWxLog: { read: 'message_log_wx' }
}

export function useResourcePermissions(resource) {
  const session = useSession()
  return Object.fromEntries(Object.entries(permissions[resource]).map(([action, required]) => [
    `can${action[0].toUpperCase()}${action.slice(1)}`,
    computed(() => (Array.isArray(required) ? required : [required]).every(permission => session.hasPermission(permission)))
  ]))
}
