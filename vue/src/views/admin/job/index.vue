<template>
  <section class="job-page">
    <header class="job-heading">
      <div><div class="page-eyebrow">XXL-JOB</div><h1>定时任务</h1><p>管理定时任务与执行日志。请使用 XXL-Job 账号登录。</p></div>
      <el-button :disabled="!configuration.url" @click="revision++">刷新调度中心</el-button>
    </header>
    <div class="surface-card job-content">
      <el-empty v-if="configuration.error" :description="configuration.error" />
      <iframe v-else :key="revision" :src="configuration.url" title="XXL-Job 定时任务控制台" referrerpolicy="no-referrer" />
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { readJobAdminConfig } from './api'

const configuration = readJobAdminConfig()
const revision = ref(0)
</script>

<style scoped>
.job-heading { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 18px; }
.job-heading h1 { margin: 6px 0 7px; color: var(--ink); font-size: 28px; letter-spacing: -.035em; }
.job-heading p { margin: 0; color: var(--muted); font-size: 13px; line-height: 1.6; }
.job-content { height: max(620px, calc(100vh - 265px)); }
.job-content iframe { display: block; width: 100%; height: 100%; border: 0; background: #ffffff; }
.job-content .el-empty { height: 100%; }
@media (max-width: 700px) { .job-heading { align-items: flex-start; flex-direction: column; } .job-content { height: max(540px, calc(100vh - 300px)); } }
</style>
