<template>
  <div class="app-container">
    <el-card>
      <template #header>操作日志</template>
      <el-form :inline="true" :model="query">
        <el-form-item label="标题">
          <el-input v-model="query.title" clearable placeholder="请输入标题" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="query.createBy" clearable placeholder="请输入操作人" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column type="index" width="55" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">{{ display(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="createBy" label="操作人" width="130">
          <template #default="{ row }">{{ display(row.createBy) }}</template>
        </el-table-column>
        <el-table-column prop="requestUri" label="请求地址" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ display(row.requestUri) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">{{ display(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total > 0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
  </div>
</template>

<script setup>
  import {
    ElMessage,
    ElMessageBox
  } from 'element-plus'
  import {
    pageApi
  } from './api'

  const loading = ref(false)
  const rows = ref([])
  const total = ref(0)
  const query = reactive({
    title: '',
    createBy: ''
  })
  const page = reactive({
    current: 1,
    size: 10
  })

  const display = value => value === null || value === undefined || value === '' ? '-' : value

async function load() {
    loading.value = true
    try {
      // SysLogController 返回 R<Page<SysLog>>；请求层已校验 R.code，这里直接使用 data.records 与 data.total。
      const pageResult = (await pageApi.logs({
        ...query,
        current: page.current,
        size: page.size
      })).data
      rows.value = pageResult.records || []
      total.value = Number(pageResult.total || 0)
    } catch (_) {
      rows.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  function search() {
    page.current = 1;
    return load()
  }

  function reset() {
    query.title = '';
    query.createBy = '';
    return search()
  }
  async function remove(row) {
    if (row.id === undefined || row.id === null) return ElMessage.warning('缺少日志标识，无法删除')
    await ElMessageBox.confirm('确认删除该操作日志吗？', '系统提示', {
      type: 'warning'
    })
    await pageApi.deleteLog(row.id)
    ElMessage.success('删除成功')
    await load()
  }
  onMounted(load)

</script>
