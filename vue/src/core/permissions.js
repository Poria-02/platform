import { computed } from 'vue'
import { useSession } from './session'

// 使用接口实际要求的权限标识，避免根据页面名称推断权限。
const permissions = {
  user: { read: 'sys_user_page', create: 'sys_user_add', edit: 'sys_user_edit', remove: 'sys_user_del' },
  role: { read: 'sys_role_list', create: 'sys_role_add', edit: 'sys_role_edit', remove: 'sys_role_del', menus: ['sys_role_perm', 'sys_menu_tree', 'sys_menu_role_tree'] },
  menu: { read: 'sys_menu_tree', create: 'sys_menu_add', edit: 'sys_menu_edit', remove: 'sys_menu_del' },
  dept: { read: 'sys_dept_tree', create: 'sys_dept_add', edit: 'sys_dept_edit', remove: 'sys_dept_del' },
  client: { read: 'sys_client_page', create: 'sys_client_add', edit: 'sys_client_edit', remove: 'sys_client_delete', clear: 'sys_client_del' },
  dict: { read: 'sys_dict_page', create: 'sys_dict_add', edit: 'sys_dict_edit', remove: 'sys_dict_del', items: 'sys_dict_item_page', import: 'sys_dict_import', export: 'sys_dict_export' },
  dictItem: { read: 'sys_dict_item_page', create: 'sys_dict_item_add', edit: 'sys_dict_item_edit', remove: 'sys_dict_item_del' },
  treeDict: { read: 'sys_treedict_page', create: 'sys_treedict_add', edit: 'sys_treedict_edit', remove: 'sys_treedict_del', items: 'sys_treedict_item_tree', import: 'sys_treedict_item_import', export: 'sys_treedict_item_export' },
  treeDictItem: { read: 'sys_treedict_item_tree', create: 'sys_treedict_item_add', edit: 'sys_treedict_item_edit', remove: 'sys_treedict_item_del' },
  tag: { read: 'admin_tag_page', create: 'admin_tag_add', edit: 'admin_tag_edit', remove: 'admin_tag_del', items: 'admin_tagItem_page' },
  tagItem: { read: 'admin_tagItem_page', create: 'admin_tagItem_add', edit: 'admin_tagItem_edit', remove: 'admin_tagItem_del' },
  param: { read: 'admin_syspublicparam_page', create: 'admin_syspublicparam_add', edit: 'admin_syspublicparam_edit', remove: 'admin_syspublicparam_del' },
  route: { read: 'sys_route_get', create: 'sys_route_insert', edit: 'sys_route_insert', remove: 'sys_route_del', refresh: 'sys_route_edit' },
  version: { read: 'base_version_page', create: 'base_version_add', edit: ['base_version_edit', 'base_version_get'], remove: 'base_version_del' },
  log: { read: 'sys_log_get', remove: 'sys_log_del' },
  token: { read: 'sys_token_get', remove: 'sys_token_del' },
  sensitiveLog: { read: 'sys_sensitive_page', detail: 'sys_sensitive_get' },
  profile: { read: 'sys_user_profile', edit: 'sys_user_edit' },
  stationLetter: { create: 'message_station_add' },
  announcement: { viewDrafts: 'message_announcement_draft', create: 'message_announcement_add', edit: 'message_announcement_edit', delete: 'message_announcement_del', send: 'message_announcement_send' },
  messageTemplate: { read: 'message_template_page', detail: 'message_template_get', create: 'message_template_add', edit: ['message_template_edit', 'message_template_get'], remove: 'message_template_del', status: 'message_template_status' },
  messageSmsLog: { read: 'message_log_sms' },
  messageWxLog: { read: 'message_log_wx' }
}

function allowed(session, required) {
  return (Array.isArray(required) ? required : [required]).every(permission => session.hasPermission(permission))
}

export function getResourcePermissions(resource) {
  const session = useSession()
  return Object.fromEntries(Object.entries(permissions[resource]).map(([action, required]) => [
    `can${action[0].toUpperCase()}${action.slice(1)}`,
    allowed(session, required)
  ]))
}

export function useResourcePermissions(resource) {
  const session = useSession()
  return Object.fromEntries(Object.entries(permissions[resource]).map(([action, required]) => [
    `can${action[0].toUpperCase()}${action.slice(1)}`,
    computed(() => allowed(session, required))
  ]))
}
