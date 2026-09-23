import { http } from '@/core/http'

// 工作台统计仅使用现有 UPMS 接口；单项失败不影响其他模块入口。
export async function fetchOverview() {
  const requests = [
    http.get('/upms/user/page', { params: { current: 1, size: 1 }, silent: true }),
    http.get('/upms/role/page', { params: { current: 1, size: 1 }, silent: true }),
    http.get('/upms/route/list', { silent: true })
  ]
  const results = await Promise.allSettled(requests)
  return results.map(result => {
    if (result.status !== 'fulfilled') return null
    const data = result.value
    return Array.isArray(data) ? data.length : Number(data?.total ?? data?.totalCount ?? 0)
  })
}
