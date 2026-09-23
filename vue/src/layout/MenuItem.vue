<template>
  <div v-if="visible">
    <div v-if="children.length" class="menu-group">
      <button class="menu-group__title" type="button" :aria-expanded="open" @click="open = !open"><span class="menu-symbol">{{ symbol }}</span><span>{{ item.title }}</span><span class="menu-chevron" :class="{ open }">⌄</span></button>
      <transition name="menu-expand"><div v-if="open" class="menu-group__children"><MenuItem v-for="child in children" :key="child.id || child.path" :item="child" :depth="depth + 1" /></div></transition>
    </div>
    <a v-else-if="external" class="menu-link" :style="{ paddingLeft: `${20 + depth * 16}px` }" :href="item.path" target="_blank" rel="noopener noreferrer"><span class="menu-symbol">{{ symbol }}</span><span>{{ item.title }}</span><span class="external-mark">↗</span></a>
    <router-link v-else class="menu-link" :style="{ paddingLeft: `${20 + depth * 16}px` }" :to="item.path"><span class="menu-symbol">{{ symbol }}</span><span>{{ item.title }}</span></router-link>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { isExternal } from '@/core/menu'

defineOptions({ name: 'MenuItem' })
const props = defineProps({ item: { type: Object, required: true }, depth: { type: Number, default: 0 } })
const children = computed(() => (props.item.children || []).filter(child => String(child.type) !== '1' && (child.path || child.children?.length)))
const visible = computed(() => String(props.item.type) !== '1' && (props.item.path || children.value.length))
const external = computed(() => isExternal(props.item.path))
const open = ref(false)
const symbol = computed(() => {
  const icon = String(props.item.icon || '').toLowerCase()
  if (icon.includes('user') || icon.includes('people')) return '◎'
  if (icon.includes('role') || icon.includes('shield')) return '◇'
  if (icon.includes('menu') || icon.includes('list')) return '☷'
  if (icon.includes('log')) return '◷'
  if (icon.includes('route') || icon.includes('link')) return '⌁'
  if (icon.includes('dict') || icon.includes('book')) return '▦'
  return '◈'
})
</script>
