<template>
  <input ref="fileInput" type="file" :accept="`.${extension}`" hidden :disabled="busy" @change="importFile">
  <el-button :loading="importing" :disabled="busy" @click="fileInput.click()">导入字典</el-button>
  <el-button :loading="exporting" :disabled="busy" @click="exportFile()">导出全部</el-button>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveDictionary } from '@/core/dictionaryTransfer'

const props = defineProps({
  api: { type: Object, required: true },
  extension: { type: String, required: true }
})
const emit = defineEmits(['imported'])
const fileInput = ref()
const importing = ref(false)
const exporting = ref(false)
const busy = computed(() => importing.value || exporting.value)

async function importFile(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || busy.value) return
  if (!file.name.toLowerCase().endsWith(`.${props.extension}`)) {
    ElMessage.error(`请选择 .${props.extension} 字典文件`)
    return
  }
  if (!file.size) { ElMessage.error('文件不能为空'); return }
  importing.value = true
  try {
    try {
      await ElMessageBox.confirm('导入会替换文件中同编码字典的内容，确认继续吗？', '导入字典', { type: 'warning' })
    } catch { return }
    await props.api.importDict(file)
    ElMessage.success('字典导入成功')
    emit('imported')
  } catch (error) {
    ElMessage.error(error.message || '字典导入失败')
  } finally { importing.value = false }
}

async function exportFile(codes = []) {
  if (busy.value) return
  exporting.value = true
  try {
    const file = await props.api.exportDict(codes)
    saveDictionary(file, `dictionary.${props.extension}`)
  } catch (error) {
    ElMessage.error(error.message || '字典导出失败')
  } finally { exporting.value = false }
}

defineExpose({ exportFile })
</script>
