import { createApp } from 'vue'
import { createPinia } from 'pinia'
import elementPlus, { loadPageComponents } from './ui/elementPlus'
import App from './App.vue'
import router from './core/router'
import { startIdleMonitor } from './core/idle'
import { startPageSessionMonitor, startTokenRefreshMonitor } from './core/session'
import { initializePageSession } from './core/token'
import stableLoading from './core/stableLoading'
import './styles/index.css'
import './styles/motion.css'
import './styles/loading.css'

initializePageSession()
const app = createApp(App).use(createPinia()).use(elementPlus).directive('stable-loading', stableLoading)
router.beforeResolve(to => {
  if (to.name !== 'home' && to.matched.some(route => route.name === 'console')) return loadPageComponents(app)
})
app.use(router).mount('#app')
startIdleMonitor()
startTokenRefreshMonitor()
startPageSessionMonitor()
