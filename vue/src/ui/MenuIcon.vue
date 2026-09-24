<template>
  <el-icon :class="{ 'menu-icon--loading': loading }"><component :is="component" /></el-icon>
</template>

<script setup>
import { ref, shallowRef, watch } from 'vue'
import { findMenuIcon, resolveMenuIcon } from './menuIcons.js'

const props = defineProps({ name: { type: String, default: '' } })
const component = shallowRef(resolveMenuIcon(props.name).component)
const loading = ref(false)

watch(() => props.name, async (name, _previous, onCleanup) => {
  let active = true
  onCleanup(() => { active = false })
  const common = findMenuIcon(name)
  if (common || !name) {
    component.value = (common || resolveMenuIcon()).component
    loading.value = false
    return
  }
  loading.value = true
  try {
    const { resolveAllIcon } = await import('./allIcons.js')
    if (active) component.value = resolveAllIcon(name).component
  } catch {
    if (active) component.value = resolveMenuIcon().component
  } finally {
    if (active) loading.value = false
  }
}, { immediate: true })
</script>

<style scoped>
.el-icon { transition: opacity .18s ease; }
.menu-icon--loading { opacity: 0; }
@media (prefers-reduced-motion: reduce) { .el-icon { transition: none; } }
</style>
