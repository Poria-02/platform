<template>
  <div class="app-container">
    <el-card>
      <template #header>{{props.title}}</template>
      <el-button type="primary" @click="load">刷新</el-button>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label">
          <template #default="s">{{display(s.row[c.prop])}}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="s">
            <el-button link type="danger" @click="remove(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total>0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
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
  const props = {
    title: 'OAuth 客户端管理',
    columns: [{
      prop: 'clientId',
      label: '客户端 ID'
    }, {
      prop: 'scope',
      label: '作用域'
    }, {
      prop: 'authorizedGrantTypes',
      label: '授权方式'
    }, {
      prop: 'accessTokenValidity',
      label: '令牌有效期'
    }]
  }
  const loading = ref(false);
  const rows = ref([]);
  const total = ref(0);
  const page = reactive({
    current: 1,
    size: 10
  });
  const display = value => value === null || value === undefined || value === '' ? '-' : value
  async function load() {
    loading.value = true;
    try {
      const result = (await pageApi.clients({
        current: page.current,
        size: page.size
      })).data;
      rows.value = result.records || [];
      total.value = Number(result.total || 0)
    } catch (_) {
      rows.value = [];
      total.value = 0
    } finally {
      loading.value = false
    }
  }
  async function remove(row) {
    if (!row.clientId) return ElMessage.warning('缺少客户端标识');
    await ElMessageBox.confirm('确认删除该客户端吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteClient(row.clientId);
    ElMessage.success('删除成功');
    await load()
  }
  onMounted(load)

</script>
