const DATA_URL = 'https://fastly.jsdelivr.net/npm/@aurouscia/china-areas@0.7.0/dist/json/data-flat.json'
const MAP_URL = 'https://fastly.jsdelivr.net/gh/longwosion/geojson-map-china@master/'
let loadingDistricts

export async function loadDistricts() {
  if (!loadingDistricts) loadingDistricts = fetchDistricts().catch(error => { loadingDistricts = undefined; throw error })
  return loadingDistricts
}

async function fetchDistricts() {
  const response = await fetch(DATA_URL, { signal: AbortSignal.timeout(15000), credentials: 'omit', referrerPolicy: 'no-referrer' })
  if (!response.ok) throw new Error(`区划接口返回 ${response.status}`)
  const rows = await response.json()
  if (!Array.isArray(rows) || rows.length < 3000) throw new Error('区划接口数据不完整')
  const byCode = new Map(rows.map(item => [item.code, item.name]))
  return rows.filter(item => /^\d{6}$/.test(item.code) && !item.code.endsWith('00')).map(item => {
    const province = byCode.get(`${item.code.slice(0, 2)}0000`) || ''
    const city = byCode.get(`${item.code.slice(0, 4)}00`) || ''
    return {
      id: `district:${item.code}`,
      code: item.code,
      name: item.name,
      detail: [province, city].filter(Boolean).join(' · '),
      province,
      city
    }
  })
}

export async function resolveDistrictCoordinates(location, signal) {
  const provinceCode = location.code.slice(0, 2)
  const directCity = ['11', '12', '31', '50'].includes(provinceCode)
  const path = directCity ? `geometryProvince/${provinceCode}.json` : `geometryCouties/${location.code.slice(0, 4)}00.json`
  try {
    const response = await fetch(`${MAP_URL}${path}`, { signal, credentials: 'omit', referrerPolicy: 'no-referrer' })
    if (!response.ok) throw new Error(`地图接口返回 ${response.status}`)
    const data = await response.json()
    const feature = data.features?.find(item => String(item.properties?.id) === location.code)
    const point = feature?.properties?.cp || centerOfGeometry(feature?.geometry?.coordinates)
    if (point && point.every(Number.isFinite)) return { latitude: point[1], longitude: point[0] }
  } catch (error) {
    if (signal?.aborted) throw error
  }
  return geocodeDistrict(location, signal)
}

function centerOfGeometry(coordinates) {
  let west = Infinity
  let east = -Infinity
  let south = Infinity
  let north = -Infinity
  function collect(value) {
    if (!Array.isArray(value)) return
    if (value.length >= 2 && typeof value[0] === 'number' && typeof value[1] === 'number') {
      west = Math.min(west, value[0])
      east = Math.max(east, value[0])
      south = Math.min(south, value[1])
      north = Math.max(north, value[1])
    }
    else value.forEach(collect)
  }
  collect(coordinates)
  return Number.isFinite(west) ? [(west + east) / 2, (south + north) / 2] : null
}

async function geocodeDistrict(location, signal) {
  const name = location.name.replace(/(?:自治县|市辖区|新区|区|县|市)$/, '')
  const url = new URL('https://geocoding-api.open-meteo.com/v1/search')
  url.search = new URLSearchParams({ name, count: '100', language: 'zh', countryCode: 'CN' }).toString()
  const response = await fetch(url, { signal, credentials: 'omit', referrerPolicy: 'no-referrer' })
  if (!response.ok) throw new Error('区县坐标查询失败')
  const data = await response.json()
  const match = data.results?.find(item =>
    item.name === name &&
    String(item.admin1 || '').includes(location.province.replace(/(?:省|市|自治区)$/, '').slice(0, 2)) &&
    (!location.city || String(item.admin2 || '').includes(location.city.replace(/市$/, '')))
  )
  if (!match) throw new Error('该区县暂无可用坐标')
  return { latitude: match.latitude, longitude: match.longitude }
}
