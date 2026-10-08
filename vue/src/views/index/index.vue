<template>
  <div class="dashboard">
    <div class="dashboard-heading"><div><div class="page-eyebrow">PORIA / DASHBOARD</div><h1>{{ greeting }}，{{ session.displayName }}</h1><p>欢迎回到工作台。</p></div></div>
    <section class="hero"><div class="hero-copy"><span class="hero-tag">统一管理平台</span><h2>从这里开始，<br>掌握你的工作空间。</h2><p>人员、权限与平台配置集中在一个清晰的界面中，进入所需模块即可开始处理。</p></div><div class="hero-art" aria-hidden="true" /></section>
    <section class="dashboard-section calendar-section"><div class="section-heading"><span class="page-eyebrow">CALENDAR</span><div class="calendar-heading"><h2>日历</h2><time class="calendar-clock">{{ clockText }}</time></div></div><MonthCalendar /></section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useSession } from '@/core/session'
import { now } from '@/core/clock'
import MonthCalendar from '@/components/MonthCalendar.vue'

const session = useSession()
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
.dashboard-heading h1 { margin: 7px 0 5px; color: #23344b; font-size: 29px; letter-spacing: -.04em; }
.dashboard-heading p { margin: 0; color: #8392a5; font-size: 13px; }
.hero { position: relative; min-height: 257px; display: flex; align-items: center; overflow: hidden; padding: 32px 42px; border: 1px solid #e4f1ec; border-radius: 18px; background: linear-gradient(110deg, #fff 34%, #f1fbf7 100%); box-shadow: 0 7px 25px #52718b0a; }
.hero::before { content: ""; position: absolute; top: -18px; left: -18px; width: 56px; height: 64px; border-right: 1px solid #b6e9d7; border-bottom: 1px solid #b6e9d7; border-radius: 0 0 100% 0; box-shadow: 7px 7px 0 -6px #f48a67; }
.hero-copy { position: relative; z-index: 1; max-width: 570px; }
.hero-tag { display: inline-block; padding: 6px 11px; border-radius: 7px; background: #e6f7f0; color: #298d70; font-size: 11px; font-weight: 750; }
.hero h2 { margin: 15px 0 11px; color: #26384e; font-size: clamp(25px, 3vw, 37px); line-height: 1.3; letter-spacing: -.04em; }
.hero p { max-width: 480px; margin: 0; color: #7c8da0; line-height: 1.7; font-size: 13px; }
.hero-art { position: absolute; top: -105px; right: 10%; width: 310px; height: 310px; border: 1px solid #9fdfca; border-radius: 50%; box-shadow: 0 0 0 16px #f3fbf8, 0 0 0 17px #b0e5d3, 0 0 0 33px #f3fbf8, 0 0 0 34px #c0eada, 0 0 0 50px #f3fbf8, 0 0 0 51px #d0eee3; }
.hero-art::after { content: ""; position: absolute; left: -35px; bottom: 45px; width: 9px; height: 9px; border-radius: 50%; background: #f47d52; }
.dashboard-section { margin-top: 32px; }
.calendar-section { margin-top: 26px; }
.section-heading { margin-bottom: 16px; }
.calendar-heading { display: flex; align-items: baseline; gap: 16px; }
.section-heading h2 { margin: 6px 0 0; color: #26384e; font-size: 21px; letter-spacing: -.025em; }
.calendar-clock { color: #52657b; font-size: 18px; font-variant-numeric: tabular-nums; font-weight: 650; }
@media (max-width: 700px) { .hero { padding: 29px 24px; } .hero-art { opacity: .35; right: -180px; } }
</style>
