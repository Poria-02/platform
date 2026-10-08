const HOLIDAY_API = 'https://timor.tech/api/holiday/year/'

export const holidaySource = 'https://timor.tech/api/holiday/'

export async function fetchHolidaySchedule(year, signal) {
  const response = await fetch(`${HOLIDAY_API}${year}`, {
    signal,
    cache: 'no-store',
    credentials: 'omit',
    referrerPolicy: 'no-referrer'
  })
  if (!response.ok) throw new Error(`节假日接口返回 ${response.status}`)

  const result = await response.json()
  if (result?.code !== 0 || !result.holiday || typeof result.holiday !== 'object' || Array.isArray(result.holiday)) {
    throw new Error('节假日接口数据格式错误')
  }

  const days = new Map()
  for (const [monthDay, entry] of Object.entries(result.holiday)) {
    const date = `${year}-${monthDay}`
    if (!/^\d{2}-\d{2}$/.test(monthDay) || entry?.date !== date || typeof entry.holiday !== 'boolean') continue
    days.set(date, { kind: entry.holiday ? 'rest' : 'work', name: entry.holiday ? String(entry.name || '假期') : '' })
  }
  return days.size ? days : null
}

export function holidayFor(schedule, date) {
  if (!schedule) return null
  const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  return schedule.get(key) || null
}
