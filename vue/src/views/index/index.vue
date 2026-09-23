<template>
  <div class="dashboard">
    <div class="dashboard-heading"><div><div class="page-eyebrow">PORIA / DASHBOARD</div><h1>{{ greeting }}，{{ session.displayName }}</h1><p>欢迎回到工作台。</p></div><span class="today-label">{{ today }}</span></div>
    <section class="hero"><div class="hero-copy"><span class="hero-tag">统一管理平台</span><h2>从这里开始，<br>掌握你的工作空间。</h2><p>人员、权限与平台配置集中在一个清晰的界面中，进入所需模块即可开始处理。</p><a href="#modules" class="hero-button">浏览功能模块 <span>↗</span></a></div><div class="hero-art" aria-hidden="true"><div class="hero-orbit hero-orbit--one" /><div class="hero-orbit hero-orbit--two" /><span class="hero-letter">P</span></div></section>
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
.dashboard-heading { display: flex; align-items: end; justify-content: space-between; gap: 18px; margin-bottom: 26px; }.dashboard-heading h1 { margin: 7px 0 5px; font-size: 29px; letter-spacing: -.04em; }.dashboard-heading p { margin: 0; color: #8299a5; font-size: 13px; }.today-label { color: #8ea2ac; font-size: 12px; }
.hero { position: relative; min-height: 266px; overflow: hidden; display: flex; align-items: center; border-radius: 20px; padding: 36px 42px; background: radial-gradient(circle at 75% 0%, #296d75 0, transparent 34%), linear-gradient(110deg, #0b2a3e 0%, #174c56 100%); color: #fff; box-shadow: 0 20px 45px #153f5020; }.hero-copy { z-index: 1; max-width: 560px; }.hero-tag { display: inline-block; padding: 7px 12px; border: 1px solid #a2e1c655; border-radius: 99px; color: #b8f1d2; font-size: 11px; font-weight: 750; letter-spacing: .08em; }.hero h2 { margin: 17px 0 10px; font-size: clamp(25px, 3vw, 38px); line-height: 1.25; letter-spacing: -.04em; }.hero p { max-width: 480px; margin: 0; color: #bed7d9; line-height: 1.7; font-size: 13px; }.hero-button { display: inline-flex; align-items: center; gap: 25px; margin-top: 23px; padding: 10px 16px; border-radius: 8px; background: #a9e6c5; color: #103a34; font-size: 12px; font-weight: 800; }.hero-button span { font-size: 18px; }.hero-art { position: absolute; right: 0; top: 0; bottom: 0; width: 38%; display: grid; place-items: center; }.hero-letter { z-index: 1; font-size: 145px; font-weight: 850; color: #d1f4dc; text-shadow: 16px 20px 0 #ffffff10; transform: rotate(-9deg); }.hero-orbit { position: absolute; width: 270px; height: 270px; border: 1px solid #d7f2dc35; border-radius: 50%; }.hero-orbit--two { width: 390px; height: 390px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; margin: 20px 0 38px; }.stat-card { position: relative; min-height: 125px; display: flex; align-items: flex-start; gap: 14px; padding: 21px; border: 1px solid #e5edef; border-radius: 15px; background: #fff; box-shadow: 0 8px 28px #1c48540a; }.stat-icon { width: 38px; height: 38px; flex: 0 0 38px; display: grid; place-items: center; border-radius: 11px; background: #dff4ec; color: #1a866f; font-size: 20px; }.stat-icon--1 { background: #e8eafa; color: #6d72c8; }.stat-icon--2 { background: #fff2df; color: #c78a34; }.stat-icon--3 { background: #e4f1fb; color: #4d91bf; }.stat-card span { display: block; color: #8497a2; font-size: 12px; }.stat-card strong { display: block; margin-top: 7px; color: #1d3949; font-size: 26px; line-height: 1; }.stat-card small { position: absolute; left: 21px; bottom: 17px; color: #a0b0b7; font-size: 10px; }
.dashboard-section { margin-top: 34px; }.section-heading { display: flex; align-items: end; justify-content: space-between; margin-bottom: 17px; }.section-heading h2 { margin: 6px 0 0; font-size: 21px; letter-spacing: -.025em; }.section-heading > span { color: #90a2ab; font-size: 12px; }.module-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 15px; }.module-card { min-height: 105px; display: flex; align-items: center; gap: 15px; padding: 21px; border: 1px solid #e2ebed; border-radius: 14px; background: #fff; transition: transform .2s, box-shadow .2s; }.module-card:hover { transform: translateY(-3px); box-shadow: 0 16px 35px #123e4a14; }.module-icon { width: 46px; height: 46px; flex: 0 0 46px; display: grid; place-items: center; border-radius: 12px; background: #e2f4ec; color: #1c8d70; font-size: 24px; }.module-icon--1 { background: #e9edfb; color: #7375cb; }.module-icon--2 { background: #fff1df; color: #ca9238; }.module-icon--3 { background: #e4f1f9; color: #4c91b6; }.module-card__text { min-width: 0; }.module-card h3 { margin: 0 0 5px; font-size: 15px; }.module-card p { margin: 0; color: #95a6af; font-size: 11px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }.module-arrow { margin-left: auto; color: #a4b5bc; font-size: 20px; }.empty-modules { padding: 32px; border: 1px dashed #cbdbdf; border-radius: 14px; color: #869ba7; background: #fff; text-align: center; }.quick-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }.quick-list a { display: flex; align-items: center; gap: 10px; min-width: 0; padding: 14px 18px; border: 1px solid #e4edef; border-radius: 10px; background: #fff; font-size: 12px; font-weight: 720; }.quick-list a:hover { border-color: #9cd5c1; }.quick-list a > span:last-child { color: #2c8c76; }.quick-dot { width: 7px; height: 7px; flex: 0 0 7px; border-radius: 50%; background: #46b596; }
.quick-list a > span:last-child { margin-left: auto; }
@media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); }.module-grid { grid-template-columns: repeat(2, 1fr); } }.dashboard-heading h1 { color: #183545; }
@media (max-width: 700px) { .today-label { display: none; }.hero { padding: 30px 25px; }.hero-art { opacity: .18; right: -90px; width: 60%; }.stats-grid, .module-grid, .quick-list { grid-template-columns: 1fr; }.stat-card { min-height: 118px; } }
</style>
