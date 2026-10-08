<template>
  <section class="calendar-card" aria-label="日历">
    <div class="calendar-summary">
      <div class="date-badge"><span>{{ selectedWeekday }}</span><strong>{{ selectedDate.getDate() }}</strong></div>
      <div class="summary-date">
        <h3>{{ selectedDate.getFullYear() }}年{{ selectedDate.getMonth() + 1 }}月</h3>
        <p>今年第{{ dayOfYear(selectedDate) }}天 · 第{{ weekOfYear(selectedDate) }}周</p>
        <span>{{ isToday(selectedDate) ? '今天' : fullDate(selectedDate) }}</span>
      </div>
      <div class="summary-lunar">
        <span>农历日期</span>
        <strong>{{ selectedLunar.full }}</strong>
        <small v-if="selectedHoliday">{{ selectedHoliday.kind === 'rest' ? `${selectedHoliday.name}假期` : '调休上班' }}</small>
        <small v-else-if="selectedFestival">{{ selectedFestival }}</small>
      </div>
    </div>

    <div class="calendar-toolbar">
      <div class="calendar-controls">
        <label class="sr-only" for="calendar-year">年份</label>
        <select id="calendar-year" :value="viewYear" @change="setYear(Number($event.target.value))">
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}年</option>
        </select>
        <button type="button" class="month-step" aria-label="上个月" @click="moveMonth(-1)">‹</button>
        <label class="sr-only" for="calendar-month">月份</label>
        <select id="calendar-month" :value="viewMonth" @change="setMonth(Number($event.target.value))">
          <option v-for="month in 12" :key="month" :value="month - 1">{{ month }}月</option>
        </select>
        <button type="button" class="month-step" aria-label="下个月" @click="moveMonth(1)">›</button>
        <label class="sr-only" for="calendar-week-start">每周起始日</label>
        <select id="calendar-week-start" v-model.number="weekStartsOn">
          <option :value="1">周一起始</option>
          <option :value="0">周日起始</option>
        </select>
      </div>
      <div class="calendar-actions">
        <button v-if="holidayAvailable" type="button" class="holiday-toggle" :aria-pressed="showHolidaySchedule" @click="showHolidaySchedule = !showHolidaySchedule">放假安排<span>{{ showHolidaySchedule ? '已显示' : '已隐藏' }}</span></button>
        <button type="button" class="today-button" @click="goToday">返回今天</button>
      </div>
    </div>

    <div class="calendar-grid" role="group" :aria-label="`${viewYear}年${viewMonth + 1}月`">
      <div v-for="day in orderedWeekdays" :key="day" class="weekday">{{ day }}</div>
      <button
        v-for="day in calendarDays"
        :key="day.key"
        type="button"
        class="calendar-day"
        :class="{ 'calendar-day--outside': !day.inMonth, 'calendar-day--weekend': day.weekend, 'calendar-day--today': day.today, 'calendar-day--selected': day.selected, 'calendar-day--festival': day.festival, 'calendar-day--holiday': displayHoliday && day.schedule?.kind === 'rest', 'calendar-day--makeup': displayHoliday && day.schedule?.kind === 'work' }"
        :aria-label="`${fullDate(day.date)}，农历${day.lunar.full}${day.festival ? `，${day.festival}` : ''}${displayHoliday && day.schedule ? `，${day.schedule.name}${day.schedule.kind === 'rest' ? '放假' : '调休上班'}` : ''}`"
        :aria-current="day.today ? 'date' : undefined"
        :aria-pressed="day.selected"
        @click="selectDay(day.date)"
      >
        <span v-if="displayHoliday && day.schedule" class="schedule-mark">{{ day.schedule.kind === 'rest' ? '休' : '班' }}</span>
        <span v-if="day.today" class="today-mark">今</span>
        <span class="day-number">{{ day.date.getDate() }}</span>
        <span class="day-caption" :class="{ 'day-caption--festival': day.festival }">{{ day.festival || day.lunar.short }}</span>
      </button>
    </div>

    <div class="calendar-footer">
      <span class="footer-label">选中日期</span>
      <span>{{ fullDate(selectedDate) }} · 农历{{ selectedLunar.full }}</span>
      <div v-if="selectedFestival || selectedHoliday" class="footer-events">
        <strong v-if="selectedFestival">{{ selectedFestival }}</strong>
        <span v-if="selectedHoliday" class="holiday-detail" :class="`holiday-detail--${selectedHoliday.kind}`">{{ selectedHoliday.kind === 'rest' ? `${selectedHoliday.name}放假` : '调休上班' }}</span>
      </div>
    </div>
    <div v-if="displayHoliday" class="holiday-source">{{ viewYear }}年放假安排来自<a :href="holidaySource" target="_blank" rel="noopener noreferrer">节假日接口</a></div>
    <div v-else-if="holidayLoading" class="holiday-source" role="status">正在获取放假安排…</div>
    <div v-else-if="holidayError" class="holiday-source" role="status">放假安排获取失败。<button type="button" @click="refreshHolidaySchedule(viewYear)">重试</button></div>
  </section>
</template>

<script setup>
import { computed, onUnmounted, ref, shallowRef, watch } from 'vue'
import { now } from '@/core/clock'
import { fetchHolidaySchedule, holidayFor, holidaySource } from './holidaySchedule'

const weekdays = ['一', '二', '三', '四', '五', '六', '日']
const lunarDays = ['初一', '初二', '初三', '初四', '初五', '初六', '初七', '初八', '初九', '初十', '十一', '十二', '十三', '十四', '十五', '十六', '十七', '十八', '十九', '二十', '廿一', '廿二', '廿三', '廿四', '廿五', '廿六', '廿七', '廿八', '廿九', '三十']
const solarFestivals = { '1-1': '元旦', '5-1': '劳动节', '10-1': '国庆节' }
const lunarFestivals = { '正月-1': '春节', '正月-15': '元宵节', '五月-5': '端午节', '七月-7': '七夕', '八月-15': '中秋节', '九月-9': '重阳节', '十二月-8': '腊八节' }
const lunarFormatter = new Intl.DateTimeFormat('zh-CN-u-ca-chinese', { year: 'numeric', month: 'long', day: 'numeric' })
const initialDate = new Date(now.value.getFullYear(), now.value.getMonth(), now.value.getDate(), 12)
const selectedDate = ref(initialDate)
const viewYear = ref(initialDate.getFullYear())
const viewMonth = ref(initialDate.getMonth())
const weekStartsOn = ref(1)
const showHolidaySchedule = ref(true)
const holidaySchedule = shallowRef(null)
const holidayLoading = ref(false)
const holidayError = ref(false)
let holidayRequest

const yearOptions = Array.from({ length: 201 }, (_, index) => initialDate.getFullYear() - 100 + index)
const holidayAvailable = computed(() => Boolean(holidaySchedule.value?.size))
const displayHoliday = computed(() => holidayAvailable.value && showHolidaySchedule.value)
const orderedWeekdays = computed(() => weekStartsOn.value === 1 ? weekdays : [weekdays[6], ...weekdays.slice(0, 6)])
const selectedWeekday = computed(() => new Intl.DateTimeFormat('zh-CN', { weekday: 'long' }).format(selectedDate.value))
const selectedLunar = computed(() => lunarDate(selectedDate.value))
const selectedFestival = computed(() => festival(selectedDate.value, selectedLunar.value))
const selectedHoliday = computed(() => displayHoliday.value ? holidayFor(holidaySchedule.value, selectedDate.value) : null)
const calendarDays = computed(() => {
  const firstWeekday = (new Date(viewYear.value, viewMonth.value, 1).getDay() - weekStartsOn.value + 7) % 7
  const daysInMonth = new Date(viewYear.value, viewMonth.value + 1, 0).getDate()
  const cellCount = Math.ceil((firstWeekday + daysInMonth) / 7) * 7
  return Array.from({ length: cellCount }, (_, index) => {
    const date = new Date(viewYear.value, viewMonth.value, index - firstWeekday + 1, 12)
    const lunar = lunarDate(date)
    return {
      key: `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`,
      date,
      lunar,
      festival: festival(date, lunar),
      schedule: holidayFor(holidaySchedule.value, date),
      inMonth: date.getMonth() === viewMonth.value,
      weekend: date.getDay() === 0 || date.getDay() === 6,
      today: isToday(date),
      selected: sameDay(date, selectedDate.value)
    }
  })
})

async function refreshHolidaySchedule(year) {
  holidayRequest?.abort()
  const controller = new AbortController()
  holidayRequest = controller
  holidaySchedule.value = null
  holidayError.value = false
  holidayLoading.value = true
  try {
    const schedule = await fetchHolidaySchedule(year, controller.signal)
    if (holidayRequest === controller && !controller.signal.aborted) holidaySchedule.value = schedule
  } catch {
    if (holidayRequest === controller && !controller.signal.aborted) holidayError.value = true
  } finally {
    if (holidayRequest === controller) {
      holidayLoading.value = false
      holidayRequest = undefined
    }
  }
}

watch(viewYear, refreshHolidaySchedule, { immediate: true })
onUnmounted(() => holidayRequest?.abort())

function lunarDate(date) {
  const parts = Object.fromEntries(lunarFormatter.formatToParts(date).map(part => [part.type, part.value]))
  const day = Number(parts.day)
  const month = parts.month || ''
  const short = day === 1 ? month : lunarDays[day - 1] || ''
  return { month, day, short, full: `${parts.yearName || ''}年${month}${lunarDays[day - 1] || ''}` }
}
function festival(date, lunar) {
  return solarFestivals[`${date.getMonth() + 1}-${date.getDate()}`] || lunarFestivals[`${lunar.month}-${lunar.day}`] || ''
}
function sameDay(a, b) {
  return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate()
}
function isToday(date) { return sameDay(date, now.value) }
function fullDate(date) { return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日` }
function dayOfYear(date) {
  const start = Date.UTC(date.getFullYear(), 0, 1)
  const current = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate())
  return Math.floor((current - start) / 86400000) + 1
}
function weekOfYear(date) {
  const current = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()))
  current.setUTCDate(current.getUTCDate() + 4 - (current.getUTCDay() || 7))
  const start = new Date(Date.UTC(current.getUTCFullYear(), 0, 1))
  return Math.ceil((((current - start) / 86400000) + 1) / 7)
}
function selectDay(date) {
  selectedDate.value = date
  viewYear.value = date.getFullYear()
  viewMonth.value = date.getMonth()
}
function moveMonth(offset) {
  const date = new Date(viewYear.value, viewMonth.value + offset, 1, 12)
  selectDay(date)
}
function setYear(year) { selectDay(new Date(year, viewMonth.value, 1, 12)) }
function setMonth(month) { selectDay(new Date(viewYear.value, month, 1, 12)) }
function goToday() { selectDay(new Date(now.value.getFullYear(), now.value.getMonth(), now.value.getDate(), 12)) }
</script>

<style scoped>
.calendar-card { overflow: hidden; border: 1px solid #e9eff3; border-radius: 16px; background: #fff; box-shadow: 0 5px 20px #52718b09; }
.calendar-summary { display: flex; align-items: center; gap: 18px; min-height: 114px; padding: 17px 22px; border-bottom: 1px solid #e9eff3; }
.date-badge { display: flex; flex: 0 0 70px; flex-direction: column; align-items: center; justify-content: center; width: 70px; height: 76px; border-radius: 12px; background: #f1f8f5; color: #24866b; }
.date-badge span { font-size: 12px; font-weight: 700; }
.date-badge strong { margin-top: 1px; font-size: 33px; line-height: 1.1; }
.summary-date h3 { margin: 0 0 4px; color: #26384e; font-size: 20px; }
.summary-date p, .summary-date span { display: block; margin: 0; color: #7c8da0; font-size: 12px; line-height: 1.5; }
.summary-lunar { display: flex; flex-direction: column; gap: 5px; margin-left: auto; min-width: 0; color: #26384e; text-align: right; }
.summary-lunar span { color: #8392a5; font-size: 11px; }
.summary-lunar strong { font-size: 14px; font-weight: 700; }
.summary-lunar small { color: #dd694c; font-size: 11px; }
.calendar-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 13px 21px 8px; }
.calendar-controls { display: flex; align-items: center; gap: 5px; }
.calendar-actions { display: flex; align-items: center; gap: 8px; }
.calendar-controls select, .today-button, .holiday-toggle { height: 34px; padding: 0 10px; border: 1px solid #dfe8ed; border-radius: 8px; background: #fff; color: #34495f; font-size: 12px; cursor: pointer; white-space: nowrap; }
.calendar-controls select:hover, .today-button:hover, .holiday-toggle:hover { border-color: #83cdb4; color: #188768; }
.holiday-toggle[aria-pressed="true"] { border-color: #b8e5d5; background: #f1f9f5; color: #188768; }
.holiday-toggle span { margin-left: 6px; font-size: 10px; opacity: .72; }
.month-step { width: 30px; height: 34px; border: 0; border-radius: 7px; background: transparent; color: #52657b; font-size: 23px; line-height: 1; cursor: pointer; }
.month-step:hover { background: #e9f8f3; color: #188768; }
.calendar-grid { display: grid; grid-template-columns: repeat(7, minmax(0, 1fr)); gap: 4px; padding: 0 21px 19px; }
.weekday { padding: 7px 0 9px; color: #8493a2; font-size: 12px; text-align: center; }
.calendar-day { position: relative; display: flex; flex-direction: column; align-items: center; justify-content: center; min-width: 0; height: 65px; border: 1px solid transparent; border-radius: 9px; background: transparent; color: #26384e; cursor: pointer; }
.calendar-day:hover { background: #f1f8f5; }
.calendar-day--weekend .day-number { color: #db5d52; }
.calendar-day--outside { opacity: .37; }
.calendar-day--festival:not(.calendar-day--outside) { background: #fff3f0; }
.calendar-day--festival:hover { background: #ffe9e4; }
.calendar-day--holiday:not(.calendar-day--outside) { background: #fff0ee; }
.calendar-day--holiday .day-number, .calendar-day--holiday .day-caption { color: #d95448; }
.calendar-day--makeup:not(.calendar-day--outside) { background: #f2f5f7; }
.calendar-day--makeup .day-number { color: #34495f; }
.calendar-day--today { border-color: #8dcfba; background: #f1f9f5; }
.calendar-day--selected, .calendar-day--selected:hover { border: 2px solid #38ad89; background: #f1f9f5; }
.calendar-day--selected.calendar-day--holiday { background: #fff0ee; }
.calendar-day--selected.calendar-day--makeup { background: #f2f5f7; }
.day-number { font-size: 20px; font-weight: 700; line-height: 1.1; }
.day-caption { max-width: 100%; overflow: hidden; color: #8998a7; font-size: 11px; line-height: 1.4; text-overflow: ellipsis; white-space: nowrap; }
.day-caption--festival { color: #d85d4d; }
.schedule-mark { position: absolute; top: 3px; left: 6px; color: #d95448; font-size: 10px; }
.calendar-day--makeup .schedule-mark { color: #52657b; }
.today-mark { position: absolute; top: 3px; right: 6px; color: #238c6c; font-size: 10px; }
.calendar-footer { display: flex; align-items: center; gap: 10px; min-height: 52px; padding: 12px 22px; border-top: 1px solid #e9eff3; color: #52657b; font-size: 12px; }
.footer-label { flex: 0 0 auto; color: #8998a7; }
.footer-events { display: flex; align-items: center; gap: 9px; margin-left: auto; }
.footer-events strong { color: #d85d4d; font-size: 12px; }
.holiday-detail { padding: 3px 7px; border-radius: 5px; background: #fff0ee; color: #d95448; white-space: nowrap; }
.holiday-detail--work { background: #f2f5f7; color: #52657b; }
.holiday-source { padding: 0 22px 13px; color: #9aa8b5; font-size: 11px; }
.holiday-source a { margin-left: 4px; color: #438e78; text-decoration: underline; text-underline-offset: 2px; }
.holiday-source button { padding: 0; border: 0; background: none; color: #438e78; font-size: inherit; text-decoration: underline; cursor: pointer; }
.sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; }
@media (max-width: 1050px) { .calendar-toolbar { flex-wrap: wrap; } }
@media (max-width: 600px) {
  .calendar-summary { gap: 12px; padding: 14px; }
  .date-badge { flex-basis: 58px; width: 58px; height: 65px; }
  .date-badge strong { font-size: 27px; }
  .summary-date h3 { font-size: 17px; }
  .summary-lunar strong { font-size: 12px; }
  .calendar-toolbar { flex-wrap: wrap; padding: 11px 10px 8px; }
  .calendar-controls { flex-wrap: wrap; }
  .calendar-actions { flex-wrap: wrap; }
  .calendar-grid { gap: 2px; padding: 0 9px 14px; }
  .calendar-day { height: 54px; }
  .day-number { font-size: 17px; }
  .day-caption { font-size: 10px; }
  .calendar-footer { flex-wrap: wrap; padding: 12px 14px; }
  .footer-events { margin-left: 0; }
  .holiday-source { padding: 0 14px 12px; }
}
@media (max-width: 390px) { .summary-lunar { display: none; } .calendar-controls { gap: 1px; } .calendar-controls select, .today-button, .holiday-toggle { padding: 0 6px; font-size: 11px; } }
</style>
