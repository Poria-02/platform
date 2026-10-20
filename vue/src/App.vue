<template>
  <el-config-provider :locale="locale" :dialog="{ transition: 'dialog-motion' }">
    <router-view v-slot="{ Component }">
      <Transition name="app-fade" mode="out-in" appear><component :is="Component" /></Transition>
    </router-view>
  </el-config-provider>
</template>

<script setup>
import { ElConfigProvider } from 'element-plus/es/components/config-provider/index.mjs'
import locale from 'element-plus/es/locale/lang/zh-cn'
</script>
