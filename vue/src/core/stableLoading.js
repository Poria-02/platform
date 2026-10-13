const states = new WeakMap()
const iconSize = 12
const iconGap = 2

function overlaps(a, b) {
  return a.left < b.right && a.right > b.left && a.top < b.bottom && a.bottom > b.top
}

function hasRoom(button, iconRect) {
  const document = button.ownerDocument
  const view = document.defaultView
  if (iconRect.left < 0 || iconRect.top < 0 || iconRect.right > document.documentElement.clientWidth || iconRect.bottom > document.documentElement.clientHeight) return false

  // 实心按钮的图标留在原有内边距内，链接按钮可以使用文字左侧的空白。
  if (!button.classList.contains('is-link') && !button.classList.contains('is-text')) {
    const bounds = button.getBoundingClientRect()
    if (iconRect.left < bounds.left + button.clientLeft || iconRect.right > bounds.right) return false
  }

  let checkNeighbors = true
  for (let current = button; current.parentElement; current = current.parentElement) {
    const parent = current.parentElement
    const style = view.getComputedStyle(parent)
    const bounds = parent.getBoundingClientRect()
    if (style.overflowX !== 'visible' && (iconRect.left < bounds.left + parent.clientLeft || iconRect.right > bounds.left + parent.clientLeft + parent.clientWidth)) return false
    if (style.overflowY !== 'visible' && (iconRect.top < bounds.top + parent.clientTop || iconRect.bottom > bounds.top + parent.clientTop + parent.clientHeight)) return false

    if (checkNeighbors) {
      for (const sibling of parent.children) {
        if (sibling === current || !overlaps(iconRect, sibling.getBoundingClientRect())) continue
        const hasContent = sibling.textContent.trim() || sibling.matches('input, img, svg, canvas') || sibling.querySelector('input, img, svg, canvas')
        if (hasContent) return false
      }
      for (const node of parent.childNodes) {
        if (node.nodeType !== 3 || !node.textContent.trim()) continue
        const range = document.createRange()
        range.selectNodeContents(node)
        if (Array.from(range.getClientRects()).some(rect => overlaps(iconRect, rect))) return false
      }
      // 只检查图标所在操作区域，避免把弹窗背后的页面误判为遮挡。
      if (iconRect.left >= bounds.left && iconRect.right <= bounds.right && iconRect.top >= bounds.top && iconRect.bottom <= bounds.bottom) checkNeighbors = false
    }
  }
  return true
}

function positionIndicator(button) {
  const icon = button.querySelector(':scope > .el-icon.is-loading')
  const label = button.querySelector(':scope > span')
  if (!icon || !label) return

  const bounds = button.getBoundingClientRect()
  const text = label.getBoundingClientRect()
  if (!bounds.width || !text.width) return
  const scaleX = bounds.width / button.offsetWidth
  const scaleY = bounds.height / button.offsetHeight
  const left = text.left - (iconSize + iconGap) * scaleX
  const top = (text.top + text.bottom - iconSize * scaleY) / 2
  const rect = { left, top, right: left + iconSize * scaleX, bottom: top + iconSize * scaleY }

  button.style.setProperty('--loading-icon-left', `${(left - bounds.left) / scaleX - button.clientLeft}px`)
  button.style.setProperty('--loading-icon-top', `${(top - bounds.top) / scaleY - button.clientTop}px`)
  button.classList.toggle('has-loading-room', hasRoom(button, rect))
}

function stop(state) {
  state.observer?.disconnect()
  state.observer = null
  state.view.removeEventListener('resize', state.schedule)
  state.view.removeEventListener('scroll', state.schedule, true)
  state.view.cancelAnimationFrame(state.frame)
  state.frame = 0
}

function update(button, binding) {
  const state = states.get(button)
  const loading = Boolean(binding.value)
  button.classList.add('stable-loading')
  button.setAttribute('aria-busy', String(loading))
  if (!loading) {
    stop(state)
    state.loading = false
    button.classList.remove('has-loading-room')
    return
  }
  positionIndicator(button)
  if (state.loading) return
  state.loading = true
  if (state.view.ResizeObserver) {
    state.observer = new state.view.ResizeObserver(state.schedule)
    state.observer.observe(button)
    if (button.parentElement) state.observer.observe(button.parentElement)
  }
  state.view.addEventListener('resize', state.schedule)
  state.view.addEventListener('scroll', state.schedule, { capture: true, passive: true })
  state.schedule()
}

export default {
  mounted(button, binding) {
    button.classList.add('stable-loading')
    const state = { view: button.ownerDocument.defaultView, loading: false, frame: 0, observer: null }
    state.schedule = () => {
      if (state.frame || !state.loading) return
      state.frame = state.view.requestAnimationFrame(() => {
        state.frame = 0
        positionIndicator(button)
      })
    }
    states.set(button, state)
    update(button, binding)
  },
  updated: update,
  beforeUnmount(button) {
    stop(states.get(button))
    states.delete(button)
  }
}
