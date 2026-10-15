<template>
  <el-dialog class="business-dialog" append-to-body :model-value="Boolean(selected)" :title="`字典项 · ${selected?.type || ''}`" width="min(980px, calc(100vw - 24px))" @close="emit('close')">
    <div v-if="selected" class="resource-page">

      <div class="resource-toolbar">

        <div class="page-heading__actions">

          <el-button :disabled="!itemCanRead" @click="loadItems">刷新</el-button>
          <el-button :disabled="!itemCanCreate" type="primary" @click="openCreateItem()">新增字典项</el-button>
        </div>
      </div>
      <section class="surface-card">
        <div v-if="itemLoadError" class="resource-error" role="alert"><span>{{ itemLoadError }}</span><el-button :disabled="!itemCanRead" link type="primary" @click="loadItems">重试</el-button></div>

        <el-table v-loading="itemLoading" :data="itemRows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
          <el-table-column prop="label" label="显示名称" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.label, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="value" label="值" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.value, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.sort, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="description" label="说明" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.description, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="190" fixed="right">
            <template #default="scope">

              <el-button :disabled="!itemCanEdit" link type="primary" @click="openEditItem(scope.row)">编辑</el-button>
              <el-button :disabled="!itemCanRemove" link type="danger" @click="removeItem(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="itemTotal > 0" class="table-footer">
          <span>共 {{ itemTotal }} 条记录</span>
          <el-pagination :disabled="!itemCanRead" v-model:current-page="itemPage.current" v-model:page-size="itemPage.size" :page-sizes="[10, 20, 50]" :total="itemTotal" layout="sizes, prev, pager, next" @size-change="loadItems" @current-change="loadItems" />
        </div>
      </section>
      <ItemEditDialog ref="itemEditorRef" :selected="selected" :item-can-create="itemCanCreate" :item-can-edit="itemCanEdit" @saved="loadItems" />
    </div>
  </el-dialog>
</template>

<script setup>
import ItemEditDialog from './ItemEditDialog.vue'
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'

const { canRead: itemCanRead, canCreate: itemCanCreate, canEdit: itemCanEdit, canRemove: itemCanRemove } = useResourcePermissions('dictItem')

const props = defineProps({
  selected: { type: Object, required: true }
})
const emit = defineEmits(["close"])

const itemLoading = ref(false)

const itemLoadError = ref('')

const itemRows = ref([])

let itemLoadSequence = 0

const itemTotal = ref(0)

const itemPage = reactive({ current: 1, size: 10 })

async function loadItems() {
  if (!props.selected || !itemCanRead.value) return
  const sequence = ++itemLoadSequence
  itemLoading.value = true
  try {
    const params = { current: itemPage.current, size: itemPage.size }
    const data = await api.listItems({ ...params, dictId: props.selected.id, current: 1, size: 1000 })
    if (sequence !== itemLoadSequence) return
    itemRows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []
    const sorted = [...itemRows.value].sort((a, b) => Number(a.sort) - Number(b.sort) || String(a.id).localeCompare(String(b.id), 'zh-CN', { numeric: true }))
    itemTotal.value = sorted.length
    itemRows.value = sorted.slice((itemPage.current - 1) * itemPage.size, itemPage.current * itemPage.size)

    itemLoadError.value = ''
  } catch (error) {
    if (sequence === itemLoadSequence) itemLoadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === itemLoadSequence) itemLoading.value = false
  }
}

async function removeItem(row) {
  if (!itemCanRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条字典项吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.removeItem(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    if (itemRows.value.length === 1 && itemPage.current > 1) itemPage.current--
    await loadItems()

  } catch { /* 请求层显示错误。 */ }
}

watch(() => [props.selected?.id], () => {
  ++itemLoadSequence
  itemLoading.value = false
  itemRows.value = []
  itemLoadError.value = ''
  itemEditorRef.value?.close()
  itemPage.current = 1
  itemTotal.value = 0
  if (props.selected) void loadItems()
})

const itemEditorRef = ref()

function openCreateItem(defaults = {}) {
  if (itemCanCreate.value) itemEditorRef.value?.openCreate(defaults)
}

function openEditItem(row) {
  if (itemCanEdit.value) return itemEditorRef.value?.openEdit(row)
}
onMounted(loadItems)
</script>
