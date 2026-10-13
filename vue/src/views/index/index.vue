<template>
  <div class="dashboard">
    <div class="dashboard-heading"><div><div class="page-eyebrow">PORIA / DASHBOARD</div><h1>{{ greeting }}，{{ session.displayName }}</h1><p>欢迎回到工作台。</p></div></div>
    <section class="hero">
      <div class="hero-copy">
        <span class="hero-tag">统一管理平台</span>
        <div class="hero-title-row">
          <h2>从这里开始，<br>掌握你的工作空间。</h2>
          <router-link class="hero-entry" to="/functions/index"><MenuIcon name="grid" /><span>全部功能</span><span class="hero-entry__count">{{ functionCount }} 项</span><span class="hero-entry__arrow" aria-hidden="true">→</span></router-link>
        </div>
        <p>人员、权限与平台配置集中在一个清晰的界面中，进入所需模块即可开始处理。</p>
      </div>
    </section>
    <section class="dashboard-section calendar-section"><div class="section-heading"><span class="page-eyebrow">CALENDAR</span><div class="calendar-heading"><h2>日历</h2><time class="calendar-clock">{{ clockText }}</time></div></div><MonthCalendar /></section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useSession } from '@/core/session'
import { now } from '@/core/clock'
import MonthCalendar from '@/components/MonthCalendar.vue'
import MenuIcon from '@/ui/MenuIcon.vue'
import { buildFunctionCatalog } from '@/core/functionCatalog'

const session = useSession()
const functionCount = computed(() => buildFunctionCatalog(session.menus, { includeProfile: session.hasPermission('sys_user_profile') }).length)
const clockText = computed(() => new Intl.DateTimeFormat('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit', hourCycle: 'h23' }).format(now.value))
const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 5) return '凌晨好'
  if (hour < 11) return '早上好'
  if (hour < 13) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})
</script>

<style scoped>
.dashboard-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 18px; margin-bottom: 19px; }
.dashboard-heading h1 { margin: 7px 0 5px; color: #323232; font-size: 29px; letter-spacing: -.04em; }
.dashboard-heading p { margin: 0; color: #909090; font-size: 13px; }
.hero { position: relative; min-height: 180px; display: flex; align-items: center; overflow: hidden; padding: 24px 28px; border: 1px solid var(--line); border-radius: 4px; background: var(--surface); box-shadow: none; }
.hero-copy { position: relative; z-index: 1; width: 100%; }
.hero-tag { display: inline-block; padding: 6px 11px; border-radius: 3px; background: var(--accent-soft); color: var(--ink); font-size: 11px; font-weight: 750; }
.hero h2 { margin: 14px 0 12px; color: #363636; font-size: clamp(24px, 2.35vw, 29px); line-height: 1.35; letter-spacing: -.035em; }
.hero-title-row { display: flex; flex-wrap: wrap; align-items: flex-end; gap: 0 24px; }
.hero-entry { display: inline-flex; align-items: center; gap: 9px; min-height: 34px; margin-bottom: 15px; padding: 7px 11px; border: 1px solid #e7e7e7; border-radius: 4px; background: transparent; color: #555; font-size: 12px; font-weight: 500; white-space: nowrap; transition: background var(--motion-fast, .16s) ease, border-color var(--motion-fast, .16s) ease; }
.hero-entry:hover { border-color: #cfcfcf; background: #f8f8f8; color: var(--ink); }
.hero-entry:focus-visible { outline: 2px solid var(--accent); outline-offset: 3px; }
.hero-entry > .el-icon { color: #777; font-size: 14px; }
.hero-entry__count { color: #999; font-size: 11px; font-variant-numeric: tabular-nums; }
.hero-entry__arrow { margin-left: 2px; color: #999; font-size: 15px; }
.hero p { max-width: 680px; margin: 0; color: #8b8b8b; line-height: 1.7; font-size: 13px; }
.dashboard-section { margin-top: 32px; }
.calendar-section { margin-top: 26px; }
.section-heading { margin-bottom: 16px; }
.calendar-heading { display: flex; align-items: baseline; gap: 16px; }
.section-heading h2 { margin: 6px 0 0; color: #363636; font-size: 21px; letter-spacing: -.025em; }
.calendar-clock { color: #636363; font-size: 18px; font-variant-numeric: tabular-nums; font-weight: 650; }
@media (max-width: 700px) { .hero { padding: 22px; } .hero-title-row { gap: 0 18px; } }
</style>
