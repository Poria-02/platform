<template>
  <div class="message-recipients">
    <div class="message-recipients-actions">
      <el-select :model-value="modelValue" multiple filterable remote reserve-keyword collapse-tags collapse-tags-tooltip :remote-method="load" :loading="loading" :disabled="disabled || addingAll" placeholder="搜索并选择接收人" style="width:100%" @update:model-value="emit('update:modelValue', $event)">
        <el-option v-for="user in options" :key="user.userId" :label="`${user.username}（${user.userId}）`" :value="user.userId" />
      </el-select>
      <el-button :loading="addingAll" :disabled="disabled" @click="addAll">添加所有用户</el-button>
      <el-button :disabled="disabled || addingAll || !modelValue.length" @click="emit('update:modelValue', [])">清空</el-button>
    </div>
    <small>已选择 {{ modelValue.length }} 人</small>
    <div v-if="error" class="resource-error" role="alert"><span>{{ error }}</span><el-button link :disabled="disabled || addingAll" @click="load()">重试</el-button></div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { isCancel } from 'axios'
import { list as listUsers } from '@/views/admin/user/api'
import { useRequestController } from '@/core/requestController'

const props = defineProps({ modelValue: { type: Array, required: true }, disabled: Boolean })
const emit = defineEmits(['update:modelValue', 'loading'])
const nextSearchRequest = useRequestController()
const nextAllRequest = useRequestController()
const options = ref([])
const loading = ref(false)
const addingAll = ref(false)
const error = ref('')
let sequence = 0
let disposed = false
const normalize = users => (users || []).map(user => ({ userId: String(user.userId), username: user.username || String(user.userId) }))
const recipients = (params, config) => listUsers(params, { silent: true, ...config })
function mergeOptions(users) {
  options.value = [...new Map([...options.value.filter(user => props.modelValue.includes(user.userId)), ...users].map(user => [user.userId, user])).values()]
}
async function load(keyword = '') {
  if (addingAll.value || disposed) return
  const current = ++sequence
  const config = nextSearchRequest()
  loading.value = true
  error.value = ''
  try {
    const page = await recipients({ current: 1, size: 50, username: keyword.trim() || undefined }, config)
    if (current === sequence) mergeOptions(normalize(page?.records))
  } catch (cause) { if (current === sequence && !isCancel(cause)) error.value = cause.message || '接收人加载失败' }
  finally { if (current === sequence) loading.value = false }
}
async function addAll() {
  if (props.disabled || addingAll.value) return
  const config = nextAllRequest()
  addingAll.value = true
  emit('loading', true)
  error.value = ''
  sequence++
  nextSearchRequest()
  loading.value = false
  try {
    const users = []
    let current = 1
    let total
    do {
      const page = await recipients({ current, size: 200 }, config)
      const records = normalize(page?.records)
      total = Number(page?.total) || 0
      if (!records.length && users.length < total) throw new Error('用户列表已变化，请重新添加所有用户')
      users.push(...records)
      current++
    } while (users.length < total)
    if (disposed) return
    mergeOptions(users)
    emit('update:modelValue', [...new Set([...props.modelValue, ...users.map(user => user.userId)])])
  } catch (cause) { if (!disposed && !isCancel(cause)) error.value = cause.message || '添加所有用户失败，请重试' }
  finally { if (!disposed) { addingAll.value = false; emit('loading', false) } }
}
onMounted(() => { void load() })
onBeforeUnmount(() => { disposed = true; sequence++ })
</script>

<style scoped>
.message-recipients { width: 100%; }
.message-recipients-actions { display: flex; align-items: flex-start; gap: 8px; }
.message-recipients-actions .el-select { flex: 1; min-width: 0; }
.message-recipients-actions .el-button + .el-button { margin-left: 0; }
.message-recipients small { display: block; margin-top: 6px; color: #8a97aa; }
@media (max-width: 600px) { .message-recipients-actions { flex-wrap: wrap; } .message-recipients-actions .el-select { flex-basis: 100%; } }
</style>
