import * as ElementIcons from '@element-plus/icons-vue'
import { findMenuIcon, menuIcons, normalizeIconName } from './menuIcons.js'

const labels = new Map(menuIcons.map(icon => [icon.name, icon.label]))
const favorites = new Set(menuIcons.map(icon => icon.name))

export const allIcons = Object.entries(ElementIcons)
  .map(([componentName, component]) => {
    const name = normalizeIconName(componentName)
    return { name, label: labels.get(name) || componentName, component }
  })
  .sort((left, right) => Number(favorites.has(right.name)) - Number(favorites.has(left.name)) || left.name.localeCompare(right.name))

const iconsByName = new Map(allIcons.map(icon => [icon.name, icon]))

export function resolveAllIcon(name) {
  const key = normalizeIconName(name)
  return iconsByName.get(key) || findMenuIcon(name) || iconsByName.get('grid')
}
