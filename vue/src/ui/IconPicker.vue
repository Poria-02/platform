<template>
  <div class="icon-picker">
    <el-popover v-model:visible="visible" placement="bottom-start" :width="410" trigger="click">
      <template #reference>
        <button class="icon-picker__trigger" type="button" aria-label="选择图标">
          <el-icon><component :is="selected.component" /></el-icon>
          <span>{{ modelValue ? selected.label : '选择图标' }}</span>
          <span class="icon-picker__chevron">⌄</span>
        </button>
      </template>
      <el-input v-model="keyword" clearable placeholder="搜索图标名称或英文标识" class="icon-picker__search" />
      <div class="icon-picker__count">共 {{ matches.length }} 个图标</div>
      <div v-if="matches.length" class="icon-picker__grid">
        <button v-for="icon in visibleIcons" :key="icon.name" class="icon-picker__option" :class="{ 'is-selected': selected.name === icon.name && modelValue }" type="button" :title="`${icon.label} · ${icon.name}`" :aria-label="`${icon.label}图标`" :aria-pressed="selected.name === icon.name && Boolean(modelValue)" @click="choose(icon.name)">
          <el-icon><component :is="icon.component" /></el-icon>
          <span>{{ icon.label }}</span>
        </button>
      </div>
      <div v-if="!matches.length" class="icon-picker__empty">没有匹配的图标</div>
      <div class="icon-picker__footer">
        <button type="button" @click="choose('')">使用默认图标</button>
        <div v-if="pageCount > 1" class="icon-picker__pages">
          <button type="button" :disabled="page === 1" aria-label="上一页" @click="page--">‹</button>
          <span>{{ page }} / {{ pageCount }}</span>
          <button type="button" :disabled="page === pageCount" aria-label="下一页" @click="page++">›</button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { allIcons, resolveAllIcon } from './allIcons.js'

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])
const visible = ref(false)
const keyword = ref('')
const page = ref(1)
const pageSize = 42
const selected = computed(() => resolveAllIcon(props.modelValue))
const matches = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  if (!query) return allIcons
  return allIcons.filter(icon => icon.label.toLowerCase().includes(query) || icon.name.includes(query) || icon.name.replaceAll('-', '').includes(query))
})
const pageCount = computed(() => Math.max(1, Math.ceil(matches.value.length / pageSize)))
const visibleIcons = computed(() => matches.value.slice((page.value - 1) * pageSize, page.value * pageSize))

watch(keyword, () => { page.value = 1 })

function choose(name) {
  emit('update:modelValue', name)
  visible.value = false
}
</script>

<style scoped>
.icon-picker { width: 100%; }
.icon-picker__trigger { display: flex; align-items: center; gap: 10px; width: 100%; min-height: 32px; padding: 0 12px; border: 1px solid #dfe8ee; border-radius: 8px; background: #fff; color: #45596c; text-align: left; cursor: pointer; }
.icon-picker__trigger:hover { border-color: #8ed4b9; }
.icon-picker__trigger > span:nth-child(2) { flex: 1; }
.icon-picker__chevron { color: #98a8b6; }
.icon-picker__search { margin-bottom: 9px; }
.icon-picker__count { margin-bottom: 9px; color: #8b9cac; font-size: 11px; }
.icon-picker__grid { display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); gap: 6px; min-height: 426px; }
.icon-picker__option { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 5px; min-width: 0; height: 56px; padding: 4px; border: 1px solid transparent; border-radius: 8px; background: #f7f9fb; color: #627589; font-size: 10px; cursor: pointer; }
.icon-picker__option .el-icon { font-size: 18px; }
.icon-picker__option span { max-width: 100%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.icon-picker__option:hover, .icon-picker__option.is-selected { border-color: #a7dfcb; background: #eaf8f2; color: #258d70; }
.icon-picker__empty { display: grid; place-items: center; min-height: 80px; color: #8fa0af; font-size: 12px; }
.icon-picker__footer { display: flex; align-items: center; justify-content: space-between; margin-top: 9px; }
.icon-picker__footer button { padding: 5px 7px; border: 0; border-radius: 6px; background: transparent; color: #8392a5; font-size: 12px; cursor: pointer; }
.icon-picker__footer button:hover:not(:disabled) { background: #eaf8f2; color: #258d70; }
.icon-picker__footer button:disabled { opacity: .4; cursor: default; }
.icon-picker__pages { display: flex; align-items: center; gap: 5px; color: #8392a5; font-size: 11px; }
.icon-picker__pages button { min-width: 25px; font-size: 18px; line-height: 1; }
</style>
