<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col v-for="item in cards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="metric-card">
          <div>{{ item.label }}</div>
          <strong>{{ item.value }}</strong>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="summary" shadow="never">
      <template #header>管理台概览</template>
      <p>数据来自 UPMS 用户、角色、网关路由、令牌和日志接口。</p>
      <el-button type="primary" :loading="loading" @click="load">刷新数据</el-button>
    </el-card>
  </div>
</template>
<script setup>
  import {
    pageApi
  } from './api'
  const loading = ref(false)
  const cards = ref([{
    label: '用户',
    value: '-'
  }, {
    label: '角色',
    value: '-'
  }, {
    label: '网关路由',
    value: '-'
  }, {
    label: '在线令牌',
    value: '-'
  }])

  function countOf(response) {
    const data = response?.data ?? response
    if (Array.isArray(data)) return data.length
    return data?.total ?? data?.totalCount ?? data?.records?.length ?? 0
  }
  async function load() {
    loading.value = true
    const results = await Promise.allSettled([pageApi.users({
      current: 1,
      size: 1
    }), pageApi.roles({
      current: 1,
      size: 1
    }), pageApi.routes(), pageApi.tokens({
      current: 1,
      size: 1
    })])
    cards.value.forEach((card, index) => {
      card.value = results[index].status === 'fulfilled' ? countOf(results[index].value) : '不可用'
    })
    loading.value = false
  }
  onMounted(load)

</script>
<style scoped>
  .metric-card {
    margin-bottom: 16px;
    color: #606266;
  }

  .metric-card strong {
    display: block;
    margin-top: 10px;
    font-size: 30px;
    color: var(--el-color-primary);
  }

  .summary {
    margin-top: 8px;
  }

</style>
