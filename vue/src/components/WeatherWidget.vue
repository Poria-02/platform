<template>
  <div ref="root" class="weather-widget">
    <button type="button" class="weather-location" :aria-expanded="menuOpen" aria-label="切换天气地点" @click="toggleMenu">
      <span>⌖ {{ selectedLocation.name }}</span><span class="weather-chevron">⌄</span>
    </button>

    <div v-if="weather" class="weather-current">
      <span class="weather-icon" aria-hidden="true">{{ condition.icon }}</span>
      <strong>{{ Math.round(weather.temperature_2m) }}°</strong>
      <span class="weather-condition">{{ condition.label }}</span>
    </div>
    <div v-else-if="weatherLoading" class="weather-message">正在获取天气…</div>
    <button v-else class="weather-message weather-retry" type="button" @click="refreshWeather">{{ weatherError || '天气暂不可用' }}，重试</button>
    <div v-if="weather" class="weather-detail">体感 {{ Math.round(weather.apparent_temperature) }}° · 湿度 {{ weather.relative_humidity_2m }}%</div>
    <div class="weather-source">{{ weather?.time ? `${weather.time.slice(11, 16)} 更新 · ` : '' }}Open-Meteo</div>

    <div v-if="menuOpen" class="weather-menu">
      <div class="weather-menu-title"><strong>切换地点</strong><span>右键或点星标设为默认</span></div>
      <input v-model="query" class="weather-search" type="search" placeholder="搜索省、市、区县，如陕西" aria-label="搜索天气地点" autocomplete="off">
      <template v-if="query.trim().length >= 2">
        <div class="weather-results-title">区县结果<span v-if="districtMatches.length"> · {{ districtMatches.length }} 条</span></div>
        <div v-if="districtLoading" class="weather-search-status">正在读取全国区县…</div>
        <div v-else-if="districtError" class="weather-search-status">区县读取失败。<button type="button" @click="loadAreaData">重试</button></div>
        <div v-else-if="districtMatches.length" class="weather-list">
          <div v-for="location in districtMatches" :key="location.id" class="weather-option" @contextmenu.prevent="setDefault(location)">
            <button type="button" class="weather-option-name" @click="selectLocation(location)">{{ location.name }}<small>{{ location.detail }}</small></button>
            <button type="button" class="weather-default" :aria-label="`将${location.name}设为默认`" :title="location.id === defaultLocationId ? '当前默认地点' : '设为默认地点'" @click="setDefault(location)">{{ location.id === defaultLocationId ? '★' : '☆' }}</button>
          </div>
        </div>
        <div v-else-if="searching" class="weather-search-status">正在搜索其他地点…</div>
        <div v-else-if="searchError" class="weather-search-status">搜索失败，请重试</div>
        <div v-else-if="!searchResults.length && !matchingLocations.length" class="weather-search-status">未找到地点，请试试城市名称</div>
        <div v-else-if="searchResults.length" class="weather-list">
          <div v-for="location in searchResults" :key="location.id" class="weather-option" @contextmenu.prevent="setDefault(location)">
            <button type="button" class="weather-option-name" @click="selectLocation(location)">{{ location.name }}<small v-if="location.detail">{{ location.detail }}</small></button>
            <button type="button" class="weather-default" :aria-label="`将${location.name}设为默认`" title="设为默认地点" @click="setDefault(location)">☆</button>
          </div>
        </div>
      </template>
      <template v-if="matchingLocations.length">
        <div class="weather-results-title">常用地点</div>
        <div class="weather-list">
          <div v-for="location in matchingLocations" :key="location.id" class="weather-option" :class="{ 'weather-option--selected': location.id === selectedLocation.id }" @contextmenu.prevent="setDefault(location)">
            <button type="button" class="weather-option-name" @click="selectLocation(location)">{{ location.name }}<small v-if="location.detail">{{ location.detail }}</small></button>
            <button type="button" class="weather-default" :aria-label="`将${location.name}设为默认`" :title="location.id === defaultLocationId ? '当前默认地点' : '设为默认地点'" @click="setDefault(location)">{{ location.id === defaultLocationId ? '★' : '☆' }}</button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onActivated, onDeactivated, onMounted, onUnmounted, ref, watch } from 'vue'
import { loadDistricts, resolveDistrictCoordinates } from './districts'
import { findDistricts, findWeatherLocations, matchesSavedLocation } from './weatherSearch'

const BASE_LOCATIONS = [
  { id: 'yubei', name: '重庆渝北区', detail: '重庆市', latitude: 29.718, longitude: 106.631 },
  { id: 'chongqing', name: '重庆', detail: '重庆市', latitude: 29.56026, longitude: 106.55771 },
  { id: 'beijing', name: '北京', detail: '北京市', latitude: 39.9075, longitude: 116.39723 },
  { id: 'shanghai', name: '上海', detail: '上海市', latitude: 31.22222, longitude: 121.45806 },
  { id: 'guangzhou', name: '广州', detail: '广东省', latitude: 23.11667, longitude: 113.25 },
  { id: 'shenzhen', name: '深圳', detail: '广东省', latitude: 22.54554, longitude: 114.0683 },
  { id: 'chengdu', name: '成都', detail: '四川省', latitude: 30.66667, longitude: 104.06667 }
]
const LOCATIONS_KEY = 'poria.weather.locations'
const DEFAULT_KEY = 'poria.weather.default'
const root = ref(null)
const locations = ref([...BASE_LOCATIONS])
const selectedLocation = ref(BASE_LOCATIONS[0])
const defaultLocationId = ref(BASE_LOCATIONS[0].id)
const menuOpen = ref(false)
const query = ref('')
const searchResults = ref([])
const searching = ref(false)
const searchError = ref(false)
const weather = ref(null)
const weatherLoading = ref(false)
const weatherError = ref('')
const districts = ref([])
const districtLoading = ref(false)
const districtError = ref(false)
const districtMatches = computed(() => query.value.trim().length >= 2 ? findDistricts(districts.value, query.value) : [])
const matchingLocations = computed(() => locations.value.filter(location => matchesSavedLocation(location, query.value)))
let weatherRequest
let searchRequest
let searchTimer
let refreshTimer
let lastWeatherAt = 0

const condition = computed(() => {
  const code = weather.value?.weather_code
  const day = weather.value?.is_day !== 0
  if (code === 0) return { icon: day ? '☀️' : '🌙', label: '晴' }
  if ([1, 2].includes(code)) return { icon: day ? '🌤️' : '☁️', label: '多云' }
  if (code === 3) return { icon: '☁️', label: '阴' }
  if ([45, 48].includes(code)) return { icon: '🌫️', label: '有雾' }
  if (code >= 51 && code <= 67) return { icon: '🌧️', label: '有雨' }
  if (code >= 71 && code <= 77) return { icon: '❄️', label: '有雪' }
  if (code >= 80 && code <= 82) return { icon: '🌦️', label: '阵雨' }
  if (code >= 85 && code <= 86) return { icon: '🌨️', label: '阵雪' }
  if (code >= 95) return { icon: '⛈️', label: '雷雨' }
  return { icon: '🌡️', label: '实时天气' }
})

onMounted(async () => {
  restoreLocations()
  document.addEventListener('pointerdown', handleOutside)
  document.addEventListener('keydown', handleKeydown)
  await loadAreaData()
  refreshWeather()
})
onActivated(() => {
  if (lastWeatherAt && Date.now() - lastWeatherAt > 30 * 60 * 1000) refreshWeather()
  refreshTimer = window.setInterval(refreshWeather, 30 * 60 * 1000)
})
onDeactivated(() => {
  closeMenu()
  window.clearInterval(refreshTimer)
})
onUnmounted(() => {
  closeMenu()
  weatherRequest?.abort()
  window.clearInterval(refreshTimer)
  document.removeEventListener('pointerdown', handleOutside)
  document.removeEventListener('keydown', handleKeydown)
})

watch(query, value => {
  window.clearTimeout(searchTimer)
  searchRequest?.abort()
  searchRequest = undefined
  searchResults.value = []
  searching.value = false
  searchError.value = false
  const name = value.trim()
  if (name.length < 2 || districtLoading.value || districtMatches.value.length) return
  searching.value = true
  searchTimer = window.setTimeout(() => searchLocations(name), 350)
})

async function loadAreaData() {
  districtLoading.value = true
  districtError.value = false
  try {
    districts.value = await loadDistricts()
    if (query.value.trim().length >= 2 && !districtMatches.value.length) searchLocations(query.value.trim())
  } catch { districtError.value = true }
  finally { districtLoading.value = false }
}

function validLocation(value) {
  return value && typeof value.id === 'string' && typeof value.name === 'string' && (
    (typeof value.code === 'string' && /^\d{6}$/.test(value.code)) ||
    (Number.isFinite(value.latitude) && Number.isFinite(value.longitude) && Math.abs(value.latitude) <= 90 && Math.abs(value.longitude) <= 180)
  )
}
function restoreLocations() {
  try {
    const saved = JSON.parse(localStorage.getItem(LOCATIONS_KEY) || '[]')
    if (Array.isArray(saved)) {
      const baseIds = new Set(BASE_LOCATIONS.map(item => item.id))
      locations.value.push(...saved.filter(item => validLocation(item) && !baseIds.has(item.id)).slice(0, 12))
    }
    const preferred = locations.value.find(item => item.id === localStorage.getItem(DEFAULT_KEY))
    if (preferred) {
      defaultLocationId.value = preferred.id
      selectedLocation.value = preferred
    }
  } catch { /* 无法读取浏览器存储时继续使用渝北区。 */ }
}
function rememberLocation(location) {
  const index = locations.value.findIndex(item => item.id === location.id)
  if (index < 0) locations.value.push(location)
  else locations.value[index] = location
  try { localStorage.setItem(LOCATIONS_KEY, JSON.stringify(locations.value.filter(item => !BASE_LOCATIONS.some(base => base.id === item.id)).slice(-12))) }
  catch { /* 禁用本地存储时，本次页面访问仍可切换。 */ }
}
function selectLocation(location) {
  rememberLocation(location)
  selectedLocation.value = location
  closeMenu()
  refreshWeather()
}
function setDefault(location) {
  rememberLocation(location)
  defaultLocationId.value = location.id
  try { localStorage.setItem(DEFAULT_KEY, location.id) }
  catch { /* 禁用本地存储时，本次页面访问仍可设默认。 */ }
  selectLocation(location)
}
function toggleMenu() {
  menuOpen.value = !menuOpen.value
  if (menuOpen.value) nextTick(() => root.value?.querySelector('.weather-search')?.focus())
  else closeMenu()
}
function closeMenu() {
  menuOpen.value = false
  query.value = ''
  window.clearTimeout(searchTimer)
  searchRequest?.abort()
  searchRequest = undefined
  searchResults.value = []
}
function handleOutside(event) { if (!root.value?.contains(event.target)) closeMenu() }
function handleKeydown(event) { if (event.key === 'Escape') closeMenu() }

async function searchLocations(name) {
  const controller = new AbortController()
  searchRequest = controller
  const timeout = window.setTimeout(() => controller.abort(), 10000)
  try {
    const results = await findWeatherLocations(name, controller.signal)
    if (searchRequest !== controller) return
    searchResults.value = results.filter(item => !locations.value.some(saved => saved.id === item.id || (saved.name === item.name && Math.abs(saved.latitude - item.latitude) < 0.03 && Math.abs(saved.longitude - item.longitude) < 0.03)))
  } catch {
    if (searchRequest === controller) searchError.value = true
  } finally {
    window.clearTimeout(timeout)
    if (searchRequest === controller) searching.value = false
  }
}

async function refreshWeather() {
  weatherRequest?.abort()
  const controller = new AbortController()
  weatherRequest = controller
  const timeout = window.setTimeout(() => controller.abort(), 12000)
  weatherLoading.value = true
  weather.value = null
  weatherError.value = ''
  try {
    let location = selectedLocation.value
    if (location.code && (!Number.isFinite(location.latitude) || !Number.isFinite(location.longitude))) {
      const coordinates = await resolveDistrictCoordinates(location, controller.signal)
      if (weatherRequest !== controller) return
      location = { ...location, ...coordinates }
      selectedLocation.value = location
      rememberLocation(location)
    }
    const url = new URL('https://api.open-meteo.com/v1/forecast')
    url.search = new URLSearchParams({ latitude: String(location.latitude), longitude: String(location.longitude), current: 'temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,is_day', timezone: 'auto' }).toString()
    const response = await fetch(url, { signal: controller.signal, credentials: 'omit', referrerPolicy: 'no-referrer', cache: 'no-store' })
    if (!response.ok) throw new Error('天气查询失败')
    const data = await response.json()
    if (!Number.isFinite(data.current?.temperature_2m)) throw new Error('天气数据无效')
    if (weatherRequest === controller) {
      weather.value = data.current
      lastWeatherAt = Date.now()
    }
  } catch (error) { if (!controller.signal.aborted && weatherRequest === controller) weatherError.value = error.message || '天气暂不可用' }
  finally {
    window.clearTimeout(timeout)
    if (weatherRequest === controller) weatherLoading.value = false
  }
}
</script>

<style scoped>
.weather-widget { position: relative; width: 226px; min-height: 105px; padding: 10px 14px; border: 1px solid #e4ecef; border-radius: 13px; background: #fff; box-shadow: 0 5px 18px #52718b0a; text-align: left; }
.weather-location { display: flex; align-items: center; justify-content: space-between; gap: 8px; width: 100%; padding: 0; border: 0; background: none; color: #52718b; font-size: 12px; font-weight: 700; text-align: left; cursor: pointer; }
.weather-location:hover { color: #238d6d; }
.weather-chevron { color: #8998a7; font-size: 16px; line-height: 1; }
.weather-current { display: flex; align-items: center; gap: 7px; margin-top: 5px; }
.weather-icon { width: 27px; font-size: 23px; line-height: 1; }
.weather-current strong { color: #26384e; font-size: 25px; line-height: 1; }
.weather-condition { color: #52657b; font-size: 12px; }
.weather-detail, .weather-source { color: #8998a7; font-size: 10px; line-height: 1.5; }
.weather-message { display: block; margin-top: 9px; color: #8998a7; font-size: 12px; }
.weather-retry { padding: 0; border: 0; background: none; color: #238d6d; cursor: pointer; }
.weather-menu { position: absolute; top: calc(100% + 6px); right: 0; z-index: 25; width: 290px; max-height: 350px; overflow-y: auto; padding: 12px; border: 1px solid #e4ecef; border-radius: 12px; background: #fff; box-shadow: 0 16px 38px #36526725; }
.weather-menu-title { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 9px; }
.weather-menu-title strong { color: #26384e; font-size: 13px; }
.weather-menu-title span { color: #98a6b4; font-size: 10px; }
.weather-search { width: 100%; height: 34px; padding: 0 10px; border: 1px solid #dfe8ed; border-radius: 8px; outline: none; background: #f8fafb; color: #26384e; font-size: 12px; }
.weather-search:focus { border-color: #83cdb4; background: #fff; }
.weather-list { margin-top: 8px; }
.weather-option { display: flex; align-items: center; gap: 4px; border-radius: 7px; }
.weather-option:hover, .weather-option--selected { background: #f1f8f5; }
.weather-option-name { display: flex; flex: 1; align-items: baseline; gap: 7px; min-width: 0; padding: 8px; border: 0; background: none; color: #34495f; font-size: 12px; text-align: left; cursor: pointer; }
.weather-option-name small { overflow: hidden; color: #98a6b4; font-size: 10px; text-overflow: ellipsis; white-space: nowrap; }
.weather-default { flex: 0 0 27px; padding: 4px; border: 0; background: none; color: #d6a64d; font-size: 18px; cursor: pointer; }
.weather-results-title { margin: 10px 0 3px; padding-top: 9px; border-top: 1px solid #edf1f4; color: #8392a5; font-size: 11px; }
.weather-search-status { padding: 10px 7px; color: #98a6b4; font-size: 11px; }
@media (max-width: 700px) { .weather-widget { width: min(100%, 300px); } .weather-menu { right: auto; left: 0; width: min(290px, calc(100vw - 34px)); } }
</style>
