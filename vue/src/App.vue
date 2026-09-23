<template>
  <router-view v-slot="{ Component }">
    <transition name="page"><component :is="Component" /></transition>
  </router-view>
</template>
<style>
.page-enter-active, .page-leave-active { transition: opacity .18s ease, transform .18s ease; }
.page-enter-from { opacity: 0; transform: translateY(6px); }
.page-leave-to { opacity: 0; transform: translateY(-4px); }
@media (prefers-reduced-motion: reduce) { .page-enter-active, .page-leave-active { transition: none; } }
</style>
