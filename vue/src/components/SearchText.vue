<template>
  <template v-for="(part, index) in parts" :key="index"><mark v-if="part.highlighted" class="search-match">{{ part.text }}</mark><template v-else>{{ part.text }}</template></template>
</template>

<script setup>
import { computed } from 'vue'
import { highlightParts } from '@/core/searchText'

const props = defineProps({ text: { type: String, default: '' }, keyword: { type: String, default: '' } })
const parts = computed(() => highlightParts(props.text, props.keyword))
</script>

<style scoped>
.search-match { padding: 0; border-radius: 2px; background: #e7e3d8; color: inherit; font: inherit; }
</style>
