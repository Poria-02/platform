<template>
  <el-dialog class="business-dialog" append-to-body :model-value="Boolean(selected)" :title="`标签项 · ${selected?.name || ''}`" width="min(980px, calc(100vw - 24px))" @close="emit('close')">
    <div v-if="selected" class="resource-page">

      <div class="resource-toolbar">

        <div class="page-heading__actions">

          <el-button :disabled="!itemCanRead" @click="loadItems">刷新</el-button>
          <el-button :disabled="!itemCanCreate" type="primary" @click="openCreateItem()">新增标签项</el-button>
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
          <el-table-column prop="status" label="状态" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.status, {}, itemStatusChoices, scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" :min-width="130" show-overflow-tooltip>
            <template #default="scope">{{ displayResourceValue(scope.row.remark, {}, [], scope.row) }}</template>
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
      <ItemEditDialog ref="itemEditorRef" :selected="selected" :item-can-create="itemCanCreate" :item-can-edit="itemCanEdit" :item-status-choices="itemStatusChoices" @saved="loadItems" />
    </div>
  </el-dialog>
</template>

<script setup>
import ItemEditDialog from './ItemEditDialog.vue'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'

const { canRead: itemCanRead, canCreate: itemCanCreate, canEdit: itemCanEdit, canRemove: itemCanRemove } = useResourcePermissions('tagItem')

const props = defineProps({
  selected: { type: Object, required: true },
  statusOptions: { type: Array, required: true }
})
const emit = defineEmits(["close"])

const itemLoading = ref(false)

const itemLoadError = ref('')

const itemRows = ref([])

let itemLoadSequence = 0

const itemTotal = ref(0)

const itemPage = reactive({ current: 1, size: 10 })

const itemStatusChoices = computed(() => props.statusOptions)

async function loadItems() {
  if (!props.selected || !itemCanRead.value) return
  const sequence = ++itemLoadSequence
  itemLoading.value = true
  try {
    const params = { current: itemPage.current, size: itemPage.size }
    const data = await api.listItems({ ...params, tagId: props.selected.id })
    if (sequence !== itemLoadSequence) return
    itemRows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []
    itemTotal.value = Number(Array.isArray(data) ? data.length : data?.total ?? data?.totalCount ?? itemRows.value.length)

    itemLoadError.value = ''
  } catch (error) {
    if (sequence === itemLoadSequence) itemLoadError.value = (error?.message || '数据加载失败')
  } finally {
    if (sequence === itemLoadSequence) itemLoading.value = false
  }
}

async function removeItem(row) {
  if (!itemCanRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条标签项吗？", '确认删除', { type: 'warning' }) } catch { return }
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
