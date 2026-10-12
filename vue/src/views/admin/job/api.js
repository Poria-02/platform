// 调度中心通过独立页面和 Cookie 登录，不经过平台的 HTTP 令牌拦截器。
export function readJobAdminConfig(rawUrl = import.meta.env.VITE_XXL_JOB_ADMIN_URL || '', pageUrl = window.location.href) {
  const value = String(rawUrl).trim()
  if (!value) return { url: '', error: '调度中心尚未配置，请联系平台管理员。' }
  if (!/^https?:\/\//i.test(value) && !/^\/(?!\/)/.test(value)) {
    return { url: '', error: '调度中心地址配置无效，请联系平台管理员。' }
  }
  try {
    const page = new URL(pageUrl)
    const target = new URL(value, page)
    if (!['http:', 'https:'].includes(target.protocol) || target.username || target.password || target.search || target.hash) {
      return { url: '', error: '调度中心地址配置无效，请联系平台管理员。' }
    }
    if (page.protocol === 'https:' && target.protocol !== 'https:') {
      return { url: '', error: '当前平台使用 HTTPS，调度中心也需要使用 HTTPS。' }
    }
    target.pathname = target.pathname.replace(/\/+$/, '') + '/'
    return { url: target.href, error: '' }
  } catch {
    return { url: '', error: '调度中心地址配置无效，请联系平台管理员。' }
  }
}
