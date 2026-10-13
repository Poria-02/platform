<template>
  <div class="empty-page"><div class="empty-page__number">404</div><h1>{{ route.meta.expectedFiles ? '菜单页面尚未实现' : '页面不存在' }}</h1><p v-if="route.meta.expectedFiles">UPMS 的路径 <code>{{ route.meta.menuPath }}</code> 需要同目录的页面与接口文件。</p><div v-if="route.meta.expectedFiles" class="empty-page__files"><code>{{ route.meta.expectedFiles.view }}</code><code>{{ route.meta.expectedFiles.api }}</code></div><router-link to="/index" class="text-link">返回工作台 →</router-link></div>
</template>
<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>
<style scoped>
.empty-page { max-width: 650px; margin: 14vh auto; padding: 36px; border: 1px solid #eeeeee; border-radius: 4px; background: #ffffff; text-align: center; }.empty-page__number { font-size: 110px; line-height: 1; font-weight: 850; letter-spacing: -.09em; color: #e8e8e8; }h1 { margin: 18px 0 8px; color: #323232; font-size: 29px; }p { color: #909090; }.empty-page__files { display: grid; gap: 8px; margin: 24px 0; }code { display: inline-block; padding: 8px 12px; border-radius: 4px; background: #f5f5f5; color: #757575; }.text-link { color: #757575; font-weight: 700; }
</style>
