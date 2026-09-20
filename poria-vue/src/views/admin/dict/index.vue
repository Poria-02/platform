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
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label" :min-width="c.width||130">
          <template #default="s">{{display(s.row[c.prop])}}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="s">
            <el-button link type="primary" @click="openEdit(s.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total>0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
    <el-dialog v-model="dialog.open" :title="dialog.title" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item v-for="f in props.formFields" :key="f.prop" :label="f.label">
          <el-input v-model="form[f.prop]" :type="f.type==='textarea'?'textarea':'text'" />
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
    title: '字典管理',
    idKey: 'id',
    columns: [{
      prop: 'type',
      label: '字典类型'
    }, {
      prop: 'description',
      label: '描述'
    }, {
      prop: 'system',
      label: '系统'
    }, {
      prop: 'remarks',
      label: '备注'
    }, {
      prop: 'createTime',
      label: '创建时间'
    }],
    queryFields: [{
      prop: 'type',
      label: '字典类型'
    }, {
      prop: 'description',
      label: '描述'
    }],
    formFields: [{
      prop: 'type',
      label: '字典类型'
    }, {
      prop: 'description',
      label: '描述'
    }, {
      prop: 'system',
      label: '系统'
    }, {
      prop: 'remarks',
      label: '备注',
      type: 'textarea'
    }]
  }
  const loading = ref(false);
  const saving = ref(false);
  const rows = ref([]);
  const total = ref(0);
  const query = reactive({
    type: '',
    description: ''
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
      const result = (await pageApi.dictionaries({
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
  }

  function resetQuery() {
    query.type = '';
    query.description = '';
    return search()
  }

  function openCreate() {
    resetForm();
    dialog.title = '新增字典';
    dialog.open = true
  }

  function openEdit(row) {
    resetForm(row);
    dialog.title = '编辑字典';
    dialog.open = true
  }
  async function submit() {
    saving.value = true;
    try {
      await (form.id === undefined ? pageApi.saveDictionary({
        ...form
      }) : pageApi.updateDictionary({
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
    await ElMessageBox.confirm('确认删除该字典吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteDictionary(row.id);
    ElMessage.success('删除成功');
    await load()
  }
  onMounted(load)

</script>
