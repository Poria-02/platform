import { watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useSession } from './session'
import { activityKey, saveSession } from './token'
import router, { clearMenuRoutes } from './router'

const idleTimeout = 30 * 60 * 1000
const activityEvents = ['pointerdown', 'pointermove', 'keydown', 'wheel', 'touchstart', 'scroll']
export function startIdleMonitor() {
  const session = useSession()
  let timer
  let lastRecorded = 0
  let revoking = false

  function lastActivity() {
    const current = session.current
    if (!current) return 0
    return Math.max(Number(current.lastActivityAt) || 0, Number(localStorage.getItem(activityKey(current.idleId))) || 0)
  }

  function schedule() {
    clearTimeout(timer)
    if (!session.authenticated || revoking) return
    const remaining = idleTimeout - (Date.now() - lastActivity())
    if (remaining <= 0) void expire()
    else timer = setTimeout(check, remaining)
  }

  async function expire() {
    if (revoking || !session.authenticated) return
    revoking = true
    clearTimeout(timer)
    try { await session.signOut() }
    catch { session.clearLocal() }
    clearMenuRoutes()
    await router.replace('/login')
    ElMessage.info('已超过 30 分钟未操作，请重新登录')
    revoking = false
  }

  function check() {
    if (!session.authenticated) return
    schedule()
  }

  function record(event) {
    if (!session.authenticated || revoking) return
    if (Date.now() - lastActivity() >= idleTimeout) { void expire(); return }
    const now = Date.now()
    if (['pointermove', 'wheel', 'scroll'].includes(event.type) && now - lastRecorded < 5000) return
    session.current.lastActivityAt = now
    saveSession(session.current)
    localStorage.setItem(activityKey(session.current.idleId), String(now))
    lastRecorded = now
    schedule()
  }

  window.addEventListener('storage', event => {
    if (!session.current?.idleId || event.key !== activityKey(session.current.idleId)) return
    if (event.newValue === null) {
      session.clearLocal()
      clearMenuRoutes()
      void router.replace('/login')
    } else schedule()
  })
  document.addEventListener('visibilitychange', () => { if (!document.hidden) check() })
  window.addEventListener('focus', check)
  for (const type of activityEvents) window.addEventListener(type, record, { passive: true })

  watch(() => session.current, current => {
    clearTimeout(timer)
    if (!current) return
    if (!current.idleId) current.idleId = crypto.randomUUID()
    if (!current.lastActivityAt) current.lastActivityAt = Date.now()
    saveSession(current)
    const key = activityKey(current.idleId)
    if (localStorage.getItem(key) === null) localStorage.setItem(key, String(current.lastActivityAt))
    schedule()
  }, { immediate: true })
}
