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
        <el-table-column label="操作" width="210">
          <template #default="scope">
            <el-button link type="primary" @click="openMenuPermission(scope.row)">菜单权限</el-button>
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
    <el-dialog v-model="menuDialog.open" :title="menuDialog.title" width="min(680px, calc(100vw - 32px))" append-to-body>
      <el-tree ref="menuTreeRef" :data="menuDialog.tree" node-key="id" show-checkbox :props="{ label: 'name', children: 'children' }" :default-checked-keys="menuDialog.checkedIds" />
      <template #footer>
        <el-button @click="menuDialog.open = false">取消</el-button>
        <el-button type="primary" :loading="menuDialog.saving" @click="saveMenuPermission">确定</el-button>
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
    }, {
      prop: 'dsType',
      label: '数据权限类型',
      type: 'number'
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
  const menuTreeRef = ref()
  const menuDialog = reactive({
    open: false,
    title: '',
    roleId: null,
    tree: [],
    checkedIds: [],
    saving: false
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
    form.dsType = 0
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
  async function openMenuPermission(row) {
    const [treeResult, checkedResult] = await Promise.all([
      pageApi.menuTree(),
      pageApi.roleMenuIds(row.roleId)
    ])
    menuDialog.title = `菜单权限：${row.roleName}`
    menuDialog.roleId = row.roleId
    menuDialog.tree = treeResult.data || []
    menuDialog.checkedIds = checkedResult.data || []
    menuDialog.open = true
  }
  async function saveMenuPermission() {
    menuDialog.saving = true
    try {
      const checked = menuTreeRef.value?.getCheckedKeys(false) || []
      const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() || []
      await pageApi.updateRoleMenus({ roleId: menuDialog.roleId, menuIds: [...new Set([...checked, ...halfChecked])] })
      ElMessage.success('菜单权限已保存')
      menuDialog.open = false
    } finally {
      menuDialog.saving = false
    }
  }
  onMounted(load)

</script>
