import { onBeforeUnmount } from 'vue'

export function useRequestController() {
  let controller
  onBeforeUnmount(() => controller?.abort())
  return () => {
    controller?.abort()
    controller = new AbortController()
    return { signal: controller.signal }
  }
}
