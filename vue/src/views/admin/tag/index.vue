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
        <el-table-column label="操作" width="220">
          <template #default="s">
            <el-button link type="primary" @click="openItems(s.row)">编辑标签项</el-button>
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
    <el-dialog v-model="itemDialog.open" :title="itemDialog.title" width="900px" append-to-body>
      <div class="item-toolbar">
        <el-button type="success" @click="openItemCreate">新增标签项</el-button>
      </div>
      <el-table v-loading="itemDialog.loading" :data="itemDialog.rows" border>
        <el-table-column type="index" width="55" />
        <el-table-column prop="name" label="标签名称" min-width="130" />
        <el-table-column prop="value" label="标签值" min-width="120" />
        <el-table-column prop="color" label="颜色" width="100">
          <template #default="scope"><span class="color-dot" :style="{ backgroundColor: scope.row.color || 'transparent' }"></span>{{ scope.row.color || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" @click="openItemEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="removeItem(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-show="itemDialog.total > 0" v-model:page="itemDialog.page.current" v-model:limit="itemDialog.page.size" :total="itemDialog.total" @pagination="loadItems" />
    </el-dialog>
    <el-dialog v-model="itemEditor.open" :title="itemEditor.title" width="560px" append-to-body>
      <el-form :model="itemEditor.form" label-width="90px">
        <el-form-item label="标签名称"><el-input v-model="itemEditor.form.name" /></el-form-item>
        <el-form-item label="标签值"><el-input v-model="itemEditor.form.value" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="itemEditor.form.icon" /></el-form-item>
        <el-form-item label="颜色"><el-input v-model="itemEditor.form.color" placeholder="#409EFF" /></el-form-item>
        <el-form-item label="状态"><el-input-number v-model="itemEditor.form.status" :min="1" :max="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="itemEditor.form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemEditor.open = false">取消</el-button>
        <el-button type="primary" :loading="itemEditor.saving" @click="saveItem">确定</el-button>
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
    title: '标签管理',
    idKey: 'id',
    columns: [{
      prop: 'tagKey',
      label: '标签键'
    }, {
      prop: 'name',
      label: '标签名称'
    }, {
      prop: 'createBy',
      label: '创建人'
    }, {
      prop: 'createTime',
      label: '创建时间'
    }],
    queryFields: [{
      prop: 'tagKey',
      label: '标签键'
    }, {
      prop: 'name',
      label: '标签名称'
    }],
    formFields: [{
      prop: 'tagKey',
      label: '标签键'
    }, {
      prop: 'name',
      label: '标签名称'
    }]
  }
  const loading = ref(false);
  const saving = ref(false);
  const rows = ref([]);
  const total = ref(0);
  const query = reactive({
    tagKey: '',
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
  const itemDialog = reactive({
    open: false,
    title: '标签项',
    loading: false,
    rows: [],
    total: 0,
    tag: null,
    page: { current: 1, size: 10 }
  })
  const itemEditor = reactive({
    open: false,
    title: '',
    saving: false,
    form: {}
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
      const result = (await pageApi.tags({
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
    query.tagKey = '';
    query.name = '';
    return search()
  };

  function openCreate() {
    resetForm();
    dialog.title = '新增标签';
    dialog.open = true
  };

  function openEdit(row) {
    resetForm(row);
    dialog.title = '编辑标签';
    dialog.open = true
  }
  async function submit() {
    saving.value = true;
    try {
      await (form.id === undefined ? pageApi.saveTag({
        ...form
      }) : pageApi.updateTag({
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
    if (row.id === undefined) return ElMessage.warning('缺少标签标识');
    await ElMessageBox.confirm('确认删除该标签吗？', '系统提示', {
      type: 'warning'
    });
    await pageApi.deleteTag(row.id);
    ElMessage.success('删除成功');
    await load()
  }
  async function openItems(row) {
    itemDialog.tag = row
    itemDialog.title = `编辑标签项：${row.name || row.tagKey || row.id}`
    itemDialog.page.current = 1
    itemDialog.open = true
    await loadItems()
  }
  async function loadItems() {
    if (!itemDialog.tag?.id) return
    itemDialog.loading = true
    try {
      const result = (await pageApi.tagItems({
        tagId: itemDialog.tag.id,
        current: itemDialog.page.current,
        size: itemDialog.page.size
      })).data || {}
      itemDialog.rows = result.records || []
      itemDialog.total = Number(result.total || 0)
    } finally {
      itemDialog.loading = false
    }
  }
  function openItemCreate() {
    itemEditor.title = '新增标签项'
    itemEditor.form = { name: '', value: '', icon: '', color: '', status: 1, remark: '' }
    itemEditor.open = true
  }
  function openItemEdit(row) {
    itemEditor.title = '编辑标签项'
    itemEditor.form = { ...row }
    itemEditor.open = true
  }
  async function saveItem() {
    itemEditor.saving = true
    try {
      const data = { ...itemEditor.form, tagId: itemDialog.tag.id }
      await (data.id ? pageApi.updateTagItem(data) : pageApi.saveTagItem(data))
      ElMessage.success('标签项保存成功')
      itemEditor.open = false
      await loadItems()
    } finally {
      itemEditor.saving = false
    }
  }
  async function removeItem(row) {
    await ElMessageBox.confirm('确认删除该标签项吗？', '系统提示', { type: 'warning' })
    await pageApi.deleteTagItem(row.id)
    ElMessage.success('删除成功')
    await loadItems()
  }
  onMounted(load)

</script>
<style scoped>
.item-toolbar { margin-bottom: 12px; }
.color-dot { display: inline-block; width: 12px; height: 12px; margin-right: 6px; border: 1px solid #dcdfe6; border-radius: 50%; vertical-align: -1px; }
</style>
