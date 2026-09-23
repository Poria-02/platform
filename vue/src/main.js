import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locale from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './core/router'
import './styles/index.css'

createApp(App).use(createPinia()).use(router).use(ElementPlus, { locale }).mount('#app')
