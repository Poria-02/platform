<template>
  <div class="app-container">
    <el-card>
      <template #header>{{ props.title }}</template>
      <el-form :inline="true" :model="query">
        <el-form-item v-for="f in props.queryFields" :key="f.prop" :label="f.label">
          <el-input v-model="query[f.prop]" @keyup.enter="search" />
        </el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openCreate">新增</el-button>
      </el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column type="index" width="55" />
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label">
          <template #default="s">{{ display(s.row[c.prop]) }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="s">
            <el-button link @click="openEdit(s.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total>0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
    <el-dialog v-model="dialog.open" :title="dialog.title">
      <el-form :model="form" label-width="100px">
        <el-form-item v-for="f in props.formFields" :key="f.prop" :label="f.label">
          <el-input v-model="form[f.prop]" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.open=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">确定</el-button>
      </template>
    </el-dialog>
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
    title: '树形字典管理',
    idKey: 'id',
    columns: [{
      prop: 'code',
      label: '字典编码'
    }, {
      prop: 'name',
      label: '字典名称'
    }, {
      prop: 'description',
      label: '描述'
    }, {
      prop: 'createTime',
      label: '创建时间'
    }],
    queryFields: [{
      prop: 'code',
      label: '字典编码'
    }, {
      prop: 'name',
      label: '字典名称'
    }],
    formFields: [{
      prop: 'code',
      label: '字典编码'
    }, {
      prop: 'name',
      label: '字典名称'
    }, {
      prop: 'description',
      label: '描述',
      type: 'textarea'
    }]
  }
  const loading = ref(false);
  const saving = ref(false);
  const rows = ref([]);
  const total = ref(0);
  const query = reactive({
    code: '',
    name: ''
  });
  const page = reactive({
    current: 1,
    size: 10
  });
  const form = reactive({});
  const dialog = reactive({
    open: false,
    title: ''
  })
  const display = value => value === null || value === undefined || value === '' ? '-' : value

  function resetForm(row = {}) {
    Object.keys(form).forEach(key => delete form[key]);
    props.formFields.forEach(field => {
      form[field.prop] = row[field.prop] ?? ''
    });
    if (row.id !== undefined) form.id = row.id
  }
  async function load() {
    loading.value = true;
    try {
      const result = (await pageApi.treeDictionaries({
        ...query,
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

  function search() {
    page.current = 1;
    return load()
  };

  function resetQuery() {
    query.code = '';
    query.name = '';
    return search()
  };

  function openCreate() {
    resetForm();
    dialog.title = '新增树形字典';
    dialog.open = true
  };

  function openEdit(row) {
    resetForm(row);
    dialog.title = '编辑树形字典';
    dialog.open = true
  }
  async function submit() {
    saving.value = true;
    try {
      await (form.id === undefined ? pageApi.saveTreeDictionary({
        ...form
      }) : pageApi.updateTreeDictionary({
        ...form
      }));
      ElMessage.success('保存成功');
      dialog.open = false;
      await load()
    } finally {
      saving.value = false
    }
  }
  async function remove(row) {
    if (row.id === undefined) return ElMessage.warning('缺少字典标识');
    await ElMessageBox.confirm('确认删除该树形字典吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteTreeDictionary(row.id);
    ElMessage.success('删除成功');
    await load()
  }
  onMounted(load)

</script>
