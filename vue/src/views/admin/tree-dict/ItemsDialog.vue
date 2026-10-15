<template>
  <el-dialog class="business-dialog" append-to-body :model-value="Boolean(selected)" :title="`字典项 · ${selected?.name || ''}`" width="min(980px, calc(100vw - 24px))" @close="emit('close')">
    <div v-if="itemParents.length" class="item-location"><el-button :disabled="!itemCanRead" link type="primary" @click="itemParents.pop()">返回上级</el-button><span>{{ itemParents.map(item => item.name).join(' / ') }}</span></div>
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
          <el-table-column prop="name" label="名称" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="value" label="值" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.value, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="pid" label="上级节点 ID" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.pid, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.sort, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.remark, {}, [], scope.row) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="190" fixed="right">
            <template #default="scope">
              <el-button :disabled="!itemCanRead" link type="primary" @click="itemParents.push({ id: scope.row.id, name: scope.row.name })">查看子项</el-button>
              <el-button :disabled="!itemCanEdit" link type="primary" @click="openEditItem(scope.row)">编辑</el-button>
              <el-button :disabled="!itemCanRemove" link type="danger" @click="removeItem(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

      </section>
      <ItemEditDialog ref="itemEditorRef" :selected="selected" :parent-id="parentId" :item-can-create="itemCanCreate" :item-can-edit="itemCanEdit" @saved="loadItems" />
    </div>
  </el-dialog>
</template>

<script setup>
import ItemEditDialog from './ItemEditDialog.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'

const { canRead: itemCanRead, canCreate: itemCanCreate, canEdit: itemCanEdit, canRemove: itemCanRemove } = useResourcePermissions('treeDictItem')

const props = defineProps({
  selected: { type: Object, required: true }
})
const emit = defineEmits(["close"])

const itemParents = ref([])

const parentId = computed(() => itemParents.value.at(-1)?.id || '0')

const itemLoading = ref(false)

const itemLoadError = ref('')

const itemRows = ref([])

let itemLoadSequence = 0

async function loadItems() {
  if (!props.selected || !itemCanRead.value) return
  const sequence = ++itemLoadSequence
  itemLoading.value = true
  try {

    const data = await api.listItems(props.selected.code, parentId.value)
    if (sequence !== itemLoadSequence) return
    itemRows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []

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

    await loadItems()

  } catch { /* 请求层显示错误。 */ }
}

watch(() => [props.selected?.id, parentId.value], () => {
  ++itemLoadSequence
  itemLoading.value = false
  itemRows.value = []
  itemLoadError.value = ''
  itemEditorRef.value?.close()

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

<style scoped>.item-location { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: #808080; font-size: 12px; }</style>
