<template>
  <div class="dashboard">
    <div class="dashboard-heading"><div><div class="page-eyebrow">PORIA / DASHBOARD</div><h1>{{ greeting }}，{{ session.displayName }}</h1><p>欢迎回到工作台。</p></div><span class="today-label">{{ today }}</span></div>
    <section class="hero"><div class="hero-copy"><span class="hero-tag">统一管理平台</span><h2>从这里开始，<br>掌握你的工作空间。</h2><p>人员、权限与平台配置集中在一个清晰的界面中，进入所需模块即可开始处理。</p><a href="#modules" class="hero-button">浏览功能模块 <span>↗</span></a></div><div class="hero-art" aria-hidden="true" /></section>
    <section class="stats-grid"><div v-for="(metric, index) in metrics" :key="metric.label" class="stat-card"><div class="stat-icon" :class="`stat-icon--${index}`">{{ metric.symbol }}</div><div><span>{{ metric.label }}</span><strong>{{ metric.value ?? '—' }}</strong></div><small>{{ metric.note }}</small></div></section>
    <section id="modules" class="dashboard-section"><div class="section-heading"><div><span class="page-eyebrow">YOUR WORKSPACE</span><h2>功能模块</h2></div><span>{{ modules.length }} 个可用分组</span></div><div v-if="modules.length" class="module-grid"><router-link v-for="(module, index) in modules" :key="module.id || module.path" :to="destination(module)" class="module-card"><div class="module-icon" :class="`module-icon--${index % 4}`">{{ module.symbol }}</div><div class="module-card__text"><h3>{{ module.title }}</h3><p>{{ module.children.length ? `${module.children.length} 个子菜单` : module.path }}</p></div><span class="module-arrow">↗</span></router-link></div><div v-else-if="session.menuError" class="empty-modules">菜单暂时无法加载。<el-button :loading="retrying" type="primary" text @click="retryMenus">重试</el-button></div><div v-else class="empty-modules">当前账号暂无可用菜单。请在 UPMS 中分配角色与菜单权限。</div></section>
    <section v-if="quickLinks.length" class="dashboard-section"><div class="section-heading"><div><span class="page-eyebrow">QUICK ACCESS</span><h2>快捷入口</h2></div></div><div class="quick-list"><router-link v-for="item in quickLinks" :key="item.path" :to="item.path"><span class="quick-dot" />{{ item.title }}<span>→</span></router-link></div></section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useSession } from '@/core/session'
import { now } from '@/core/clock'
import { firstPagePath, flattenMenus, isPageMenu, pageFor } from '@/core/menu'
import { installMenuRoutes } from '@/core/router'
import { fetchOverview } from './api'

const session = useSession()
const counts = ref([null, null, null])
const retrying = ref(false)
const today = computed(() => new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }).format(now.value))
const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 5) return '凌晨好'
  if (hour < 11) return '早上好'
  if (hour < 13) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})
const modules = computed(() => session.menus.filter(item => String(item.type) !== '1' && item.path !== '/index').map(item => ({ ...item, symbol: ['◈', '◎', '◇', '▦'][session.menus.indexOf(item) % 4] })))
const quickLinks = computed(() => flattenMenus(session.menus).filter(item => isPageMenu(item) && pageFor(item.path) && !item.children?.some(child => String(child.type) !== '1')).slice(0, 8))
const metrics = computed(() => [
  { label: '用户总数', value: counts.value[0], note: '来自 UPMS 用户服务', symbol: '◎' },
  { label: '角色总数', value: counts.value[1], note: '来自 UPMS 权限服务', symbol: '◇' },
  { label: '网关路由', value: counts.value[2], note: '已配置的路由条目', symbol: '⌁' },
  { label: '可用菜单', value: flattenMenus(session.menus).filter(isPageMenu).length, note: '当前账号有权访问', symbol: '☷' }
])
function destination(item) { return item.path && item.path !== '/' ? item.path : firstPagePath(item.children) || '/index' }
async function retryMenus() {
  retrying.value = true
  session.menusReady = false
  try { installMenuRoutes(await session.ensureMenus()) }
  catch { session.menusReady = true }
  finally { retrying.value = false }
}
onMounted(async () => { counts.value = await fetchOverview() })
</script>

<style scoped>
.dashboard-heading { display: flex; align-items: end; justify-content: space-between; gap: 18px; margin-bottom: 19px; }
.dashboard-heading h1 { margin: 7px 0 5px; color: #23344b; font-size: 29px; letter-spacing: -.04em; }
.dashboard-heading p { margin: 0; color: #8392a5; font-size: 13px; }
.today-label { color: #8c9bad; font-size: 12px; }
.hero { position: relative; min-height: 257px; display: flex; align-items: center; overflow: hidden; padding: 32px 42px; border: 1px solid #e4f1ec; border-radius: 18px; background: linear-gradient(110deg, #fff 34%, #f1fbf7 100%); box-shadow: 0 7px 25px #52718b0a; }
.hero::before { content: ""; position: absolute; top: -18px; left: -18px; width: 56px; height: 64px; border-right: 1px solid #b6e9d7; border-bottom: 1px solid #b6e9d7; border-radius: 0 0 100% 0; box-shadow: 7px 7px 0 -6px #f48a67; }
.hero-copy { position: relative; z-index: 1; max-width: 570px; }
.hero-tag { display: inline-block; padding: 6px 11px; border-radius: 7px; background: #e6f7f0; color: #298d70; font-size: 11px; font-weight: 750; }
.hero h2 { margin: 15px 0 11px; color: #26384e; font-size: clamp(25px, 3vw, 37px); line-height: 1.3; letter-spacing: -.04em; }
.hero p { max-width: 480px; margin: 0; color: #7c8da0; line-height: 1.7; font-size: 13px; }
.hero-button { display: inline-flex; align-items: center; gap: 23px; margin-top: 21px; padding: 10px 16px; border-radius: 9px; background: #45b995; color: #fff; font-size: 12px; font-weight: 750; transition: background .18s ease, transform .18s ease; }
.hero-button:hover { background: #278d72; transform: translateY(-2px); }
.hero-button span { font-size: 18px; }
.hero-art { position: absolute; top: -105px; right: 10%; width: 310px; height: 310px; border: 1px solid #9fdfca; border-radius: 50%; box-shadow: 0 0 0 16px #f3fbf8, 0 0 0 17px #b0e5d3, 0 0 0 33px #f3fbf8, 0 0 0 34px #c0eada, 0 0 0 50px #f3fbf8, 0 0 0 51px #d0eee3; }
.hero-art::after { content: ""; position: absolute; left: -35px; bottom: 45px; width: 9px; height: 9px; border-radius: 50%; background: #f47d52; }
.stats-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 13px; margin: 17px 0 34px; }
.stat-card { position: relative; min-height: 117px; display: flex; align-items: flex-start; gap: 13px; padding: 19px; border: 1px solid #e9eff3; border-radius: 14px; background: #fff; box-shadow: 0 5px 19px #52718b07; }
.stat-icon { display: grid; place-items: center; width: 37px; height: 37px; flex: 0 0 37px; border-radius: 10px; background: #e6f7f0; color: #329c7d; font-size: 19px; }
.stat-icon--1 { background: #f0efff; color: #8480bf; }
.stat-icon--2 { background: #fff0e9; color: #e3815a; }
.stat-icon--3 { background: #eaf4fb; color: #6b9bc2; }
.stat-card span { display: block; color: #8392a5; font-size: 12px; }
.stat-card strong { display: block; margin-top: 7px; color: #26384e; font-size: 25px; line-height: 1; }
.stat-card small { position: absolute; left: 19px; bottom: 14px; color: #a4b1bd; font-size: 10px; }
.dashboard-section { margin-top: 32px; }
.section-heading { display: flex; align-items: end; justify-content: space-between; margin-bottom: 16px; }
.section-heading h2 { margin: 6px 0 0; color: #26384e; font-size: 21px; letter-spacing: -.025em; }
.section-heading > span { color: #95a3b1; font-size: 12px; }
.module-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 13px; }
.module-card { min-height: 101px; display: flex; align-items: center; gap: 14px; padding: 19px; border: 1px solid #e9eff3; border-radius: 14px; background: #fff; transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease; }
.module-card:hover { transform: translateY(-2px); border-color: #cbe9dc; box-shadow: 0 12px 28px #52718b14; }
.module-icon { display: grid; place-items: center; width: 43px; height: 43px; flex: 0 0 43px; border-radius: 10px; background: #e6f7f0; color: #329c7d; font-size: 23px; }
.module-icon--1 { background: #f0efff; color: #8480bf; }
.module-icon--2 { background: #fff0e9; color: #e3815a; }
.module-icon--3 { background: #eaf4fb; color: #6b9bc2; }
.module-card__text { min-width: 0; }
.module-card h3 { margin: 0 0 5px; color: #2b3e54; font-size: 14px; }
.module-card p { margin: 0; overflow: hidden; color: #98a6b4; font-size: 11px; text-overflow: ellipsis; white-space: nowrap; }
.module-arrow { margin-left: auto; color: #a8b7c3; font-size: 19px; }
.empty-modules { padding: 31px; border: 1px dashed #d5e2e9; border-radius: 14px; background: #fff; color: #8392a5; text-align: center; }
.quick-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }
.quick-list a { display: flex; align-items: center; gap: 10px; min-width: 0; padding: 14px 17px; border: 1px solid #e9eff3; border-radius: 10px; background: #fff; font-size: 12px; font-weight: 700; }
.quick-list a:hover { border-color: #bce4d4; }
.quick-list a > span:last-child { margin-left: auto; color: #319a7a; }
.quick-dot { width: 7px; height: 7px; flex: 0 0 7px; border-radius: 50%; background: #45b995; }
@media (max-width: 1200px) { .stats-grid, .module-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 700px) { .today-label { display: none; } .hero { padding: 29px 24px; } .hero-art { opacity: .35; right: -180px; } .stats-grid, .module-grid, .quick-list { grid-template-columns: 1fr; } }
@media (prefers-reduced-motion: reduce) { .hero-button, .module-card { transition: none; } }
</style>
