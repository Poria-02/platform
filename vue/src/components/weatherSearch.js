const PROVINCES = '北京 天津 上海 重庆 河北 山西 辽宁 吉林 黑龙江 江苏 浙江 安徽 福建 江西 山东 河南 湖北 湖南 广东 海南 四川 贵州 云南 陕西 甘肃 青海 台湾 内蒙古 广西 西藏 宁夏 新疆 香港 澳门'.split(' ')

export function parseCityQuery(value) {
  const text = value.trim().replace(/[\s,，·]+/g, '')
  const province = PROVINCES.find(item => text.startsWith(item)) || ''
  const city = province
    ? text.slice(province.length).replace(/^(?:省|市|特别行政区|壮族自治区|回族自治区|维吾尔自治区|自治区)/, '')
    : text
  return { city, province }
}

export function matchesSavedLocation(location, value) {
  const { city, province } = parseCityQuery(value)
  if (!city && !province) return true
  return (!city || location.name.includes(city) || location.detail?.includes(city)) && (!province || location.name.includes(province) || location.detail?.includes(province))
}

export function findDistricts(districts, value) {
  const { city, province } = parseCityQuery(value)
  if (!city && !province) return []
  return districts.filter(location =>
    (!province || location.province.includes(province)) &&
    (!city || location.city.includes(city) || location.name.includes(city) || `${location.city.replace(/市$/, '')}${location.name}`.includes(city))
  )
}

export async function findWeatherLocations(value, signal) {
  const { city, province } = parseCityQuery(value)
  if (!city) return []
  const url = new URL('https://geocoding-api.open-meteo.com/v1/search')
  url.search = new URLSearchParams({ name: city, count: '20', language: 'zh', format: 'json' }).toString()
  const response = await fetch(url, { signal, credentials: 'omit', referrerPolicy: 'no-referrer' })
  if (!response.ok) throw new Error('地点搜索失败')
  const data = await response.json()
  const results = Array.isArray(data.results) ? data.results : []
  return results
    .filter(item => Number.isFinite(item.latitude) && Number.isFinite(item.longitude))
    .filter(item => !province || String(item.admin1 || '').includes(province))
    .sort((a, b) => Number(b.name === city) - Number(a.name === city))
    .slice(0, 8)
    .map(item => ({
      id: `geo:${item.id}`,
      name: item.name,
      detail: [item.admin1, item.admin2, item.country].filter(Boolean).filter((part, index, parts) => parts.indexOf(part) === index).join(' · '),
      latitude: item.latitude,
      longitude: item.longitude
    }))
}
