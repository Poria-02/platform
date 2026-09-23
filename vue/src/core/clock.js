import { ref } from 'vue'

export const now = ref(new Date())

let timer

export function startClock() {
  now.value = new Date()
  if (!timer) timer = window.setInterval(() => { now.value = new Date() }, 1000)
  return () => { window.clearInterval(timer); timer = undefined }
}
