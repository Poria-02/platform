<template>
  <div v-if="!item.hidden && !isButton(item)">
    <el-sub-menu v-if="visibleChildren.length" ref="subMenu" :index="menuIndex(item)" teleported>
      <template #title>
        <svg-icon :icon-class="menuIcon(item)" />
        <span class="menu-title" :title="menuTitle(item)">{{ menuTitle(item) }}</span>
      </template>
      <sidebar-item v-for="(child, index) in visibleChildren" :key="menuIndex(child, index)" :is-nest="true" :item="child" :base-path="resolvePath(item.path)" class="nest-menu" />
    </el-sub-menu>
    <app-link v-else :to="resolvePath(item.path, item.query)">
      <el-menu-item :index="menuIndex(item)" :class="{ 'submenu-title-noDropdown': !isNest }">
        <svg-icon :icon-class="menuIcon(item)" />
        <template #title><span class="menu-title" :title="menuTitle(item)">{{ menuTitle(item) }}</span></template>
      </el-menu-item>
    </app-link>
  </div>
</template>

<script setup>
import { isExternal } from '@/utils/validate'
import AppLink from './Link'
import { getNormalPath } from '@/utils/ruoyi'

const props = defineProps({
  item: { type: Object, required: true },
  isNest: { type: Boolean, default: false },
  basePath: { type: String, default: '' }
})

const visibleChildren = computed(() => (Array.isArray(props.item.children) ? props.item.children : [])
  .filter(child => !child.hidden && !isButton(child)))

function menuTitle(menu) {
  return menu?.meta?.title || menu?.name || menu?.label || menu?.path || '未命名菜单'
}

function menuIcon(menu) {
  return menu?.meta?.icon || menu?.icon || 'menu'
}

function isButton(menu) {
  return String(menu?.type) === '1'
}

function menuIndex(menu, index = '') {
  return menu.path || `menu-${menu.id ?? menu.menuId ?? index}`
}

function resolvePath(routePath, routeQuery) {
  if (isExternal(routePath)) return routePath
  // Poria 菜单 path 已是完整前端路径，不再重复拼接父级路径。
  if (routePath?.startsWith('/')) return routePath
  if (isExternal(props.basePath)) return props.basePath
  if (routeQuery) {
    return { path: getNormalPath(props.basePath + '/' + routePath), query: JSON.parse(routeQuery) }
  }
  return getNormalPath(props.basePath + '/' + routePath)
}
</script>
