<template>
  <div class="app-container">
    <el-card>
      <template #header>{{ props.title }}</template>
      <el-form :inline="true" :model="query">
        <el-form-item v-for="field in props.queryFields" :key="field.prop" :label="field.label">
          <el-input v-model="query[field.prop]" clearable @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openCreate">新增</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column type="index" width="55" />
        <el-table-column v-for="column in props.columns" :key="column.prop" :prop="column.prop" :label="column.label" :min-width="column.width || 130">
          <template #default="scope">{{ display(scope.row[column.prop]) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total > 0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
    <el-dialog v-model="dialog.open" :title="dialog.title" width="560px">
      <el-form :model="form" label-width="105px">
        <el-form-item v-for="field in props.formFields" :key="field.prop" :label="field.label">
          <el-input-number v-if="field.type === 'number'" v-model="form[field.prop]" style="width:100%" />
          <el-input v-else v-model="form[field.prop]" :type="field.type === 'textarea' ? 'textarea' : 'text'" />
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
    title: '角色管理',
    idKey: 'roleId',
    columns: [{
      prop: 'roleName',
      label: '角色名称'
    }, {
      prop: 'roleCode',
      label: '角色编码'
    }, {
      prop: 'roleDesc',
      label: '描述'
    }, {
      prop: 'createTime',
      label: '创建时间'
    }],
    queryFields: [{
      prop: 'roleName',
      label: '角色名称'
    }, {
      prop: 'roleCode',
      label: '角色编码'
    }],
    formFields: [{
      prop: 'roleName',
      label: '角色名称'
    }, {
      prop: 'roleCode',
      label: '角色编码'
    }, {
      prop: 'roleDesc',
      label: '描述',
      type: 'textarea'
    }]
  }
  const loading = ref(false);
  const saving = ref(false);
  const rows = ref([]);
  const total = ref(0);
  const query = reactive({
    roleName: '',
    roleCode: ''
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
  const display = value => value === null || value === undefined || value === '' ? '-' : (typeof value === 'object' ? JSON.stringify(value) : value)

  function resetForm(row = {}) {
    Object.keys(form).forEach(key => delete form[key]);
    props.formFields.forEach(field => {
      form[field.prop] = row[field.prop] ?? ''
    });
    if (row.roleId !== undefined) form.roleId = row.roleId
  }
  async function load() {
    loading.value = true;
    try {
      const pageResult = (await pageApi.roles({
        ...query,
        current: page.current,
        size: page.size
      })).data;
      rows.value = pageResult.records || [];
      total.value = Number(pageResult.total || 0)
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
    query.roleName = '';
    query.roleCode = '';
    return search()
  };

  function openCreate() {
    resetForm();
    dialog.title = '新增角色';
    dialog.open = true
  };

  function openEdit(row) {
    resetForm(row);
    dialog.title = '编辑角色';
    dialog.open = true
  }
  async function submit() {
    saving.value = true;
    try {
      await (form.roleId === undefined ? pageApi.saveRole({
        ...form
      }) : pageApi.updateRole({
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
    if (row.roleId === undefined) return ElMessage.warning('缺少角色标识');
    await ElMessageBox.confirm('确认删除该角色吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteRole(row.roleId);
    ElMessage.success('删除成功');
    await load()
  }
  onMounted(load)

</script>
