import { createApp } from 'vue'
import { createPinia } from 'pinia'
import elementPlus from './ui/elementPlus'
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
createApp(App).use(createPinia()).use(router).use(elementPlus).directive('stable-loading', stableLoading).mount('#app')
startIdleMonitor()
startTokenRefreshMonitor()
startPageSessionMonitor()
