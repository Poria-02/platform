import {
  Bell, Clock, Collection, Connection, DataAnalysis, Document, Files, Folder,
  Grid, House, Key, Link, Lock, Management, Menu, Message, Monitor, Operation,
  Reading, Search, Setting, Tickets, Tools, User, UserFilled, Wallet
} from '@element-plus/icons-vue'

export const menuIcons = [
  { name: 'house', label: '首页', component: House },
  { name: 'user', label: '用户', component: User },
  { name: 'user-filled', label: '人员', component: UserFilled },
  { name: 'menu', label: '菜单', component: Menu },
  { name: 'grid', label: '模块', component: Grid },
  { name: 'lock', label: '安全', component: Lock },
  { name: 'key', label: '权限', component: Key },
  { name: 'document', label: '文档', component: Document },
  { name: 'reading', label: '字典', component: Reading },
  { name: 'setting', label: '设置', component: Setting },
  { name: 'tickets', label: '工单', component: Tickets },
  { name: 'bell', label: '通知', component: Bell },
  { name: 'data-analysis', label: '分析', component: DataAnalysis },
  { name: 'connection', label: '连接', component: Connection },
  { name: 'clock', label: '时间', component: Clock },
  { name: 'folder', label: '目录', component: Folder },
  { name: 'monitor', label: '终端', component: Monitor },
  { name: 'collection', label: '集合', component: Collection },
  { name: 'operation', label: '操作', component: Operation },
  { name: 'tools', label: '工具', component: Tools },
  { name: 'files', label: '文件', component: Files },
  { name: 'management', label: '管理', component: Management },
  { name: 'link', label: '链接', component: Link },
  { name: 'message', label: '消息', component: Message },
  { name: 'wallet', label: '财务', component: Wallet },
  { name: 'search', label: '搜索', component: Search }
]

const iconsByName = new Map(menuIcons.map(icon => [icon.name, icon]))
const legacyNames = {
  home: 'house', users: 'user', people: 'user', role: 'key', shield: 'lock',
  list: 'menu', dict: 'reading', book: 'reading', log: 'document',
  route: 'connection', database: 'collection', chart: 'data-analysis',
  tag: 'tickets', client: 'monitor', param: 'setting'
}

export function normalizeIconName(name) {
  return String(name || '').trim()
    .replace(/^(el-icon-|icon-|i-)/i, '')
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/[_\s]+/g, '-')
    .toLowerCase()
}

export function findMenuIcon(name) {
  const key = normalizeIconName(name)
  return iconsByName.get(key) || iconsByName.get(legacyNames[key]) || null
}

export function resolveMenuIcon(name) {
  return findMenuIcon(name) || iconsByName.get('grid')
}
