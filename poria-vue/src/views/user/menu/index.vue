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
      <el-table v-loading="loading" :data="rows" :row-key="props.idKey" :tree-props="{children:'children'}" border>
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
    </el-card>
    <el-dialog v-model="dialog.open" :title="dialog.title" width="560px">
      <el-form :model="form" label-width="105px">
        <el-form-item v-for="field in props.formFields" :key="field.prop" :label="field.label">
          <el-input-number v-if="field.type === 'number'" v-model="form[field.prop]" style="width:100%" />
          <el-input v-else v-model="form[field.prop]" />
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
    title: '菜单管理',
    idKey: 'menuId',
    columns: [{
      prop: 'name',
      label: '名称'
    }, {
      prop: 'path',
      label: '页面路径'
    }, {
      prop: 'permission',
      label: '权限标识'
    }, {
      prop: 'type',
      label: '类型'
    }, {
      prop: 'platform',
      label: '平台'
    }, {
      prop: 'sort',
      label: '排序'
    }],
    queryFields: [{
      prop: 'name',
      label: '名称'
    }, {
      prop: 'platform',
      label: '平台'
    }],
    formFields: [{
      prop: 'name',
      label: '名称'
    }, {
      prop: 'path',
      label: '页面路径'
    }, {
      prop: 'permission',
      label: '权限标识'
    }, {
      prop: 'parentId',
      label: '上级菜单 ID',
      type: 'number'
    }, {
      prop: 'icon',
      label: '图标'
    }, {
      prop: 'type',
      label: '类型'
    }, {
      prop: 'platform',
      label: '平台'
    }, {
      prop: 'sort',
      label: '排序',
      type: 'number'
    }]
  }
  const loading = ref(false);
  const saving = ref(false);
  const rows = ref([]);
  const query = reactive({
    name: '',
    platform: ''
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
    if (row.menuId !== undefined) form.menuId = row.menuId
  }
  async function load() {
    loading.value = true;
    try {
      rows.value = (await pageApi.menuTree({
        ...query
      })).data || []
    } catch (_) {
      rows.value = []
    } finally {
      loading.value = false
    }
  }

  function search() {
    return load()
  };

  function resetQuery() {
    query.name = '';
    query.platform = '';
    return load()
  };

  function openCreate() {
    resetForm();
    dialog.title = '新增菜单';
    dialog.open = true
  };

  function openEdit(row) {
    resetForm(row);
    dialog.title = '编辑菜单';
    dialog.open = true
  }
  async function submit() {
    saving.value = true;
    try {
      await (form.menuId === undefined ? pageApi.saveMenu({
        ...form
      }) : pageApi.updateMenu({
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
    if (row.menuId === undefined) return ElMessage.warning('缺少菜单标识');
    await ElMessageBox.confirm('确认删除该菜单吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteMenu(row.menuId);
    ElMessage.success('删除成功');
    await load()
  }
  onMounted(load)

</script>
