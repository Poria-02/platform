function join(prefix, path = '') {
  const normalizedPath = path ? `/${path.replace(/^\/+/, '')}` : ''
  return `${prefix}${normalizedPath}`
}

export function authUrl(path = '') {
  return join('/auth', path)
}

export function upmsUrl(path = '') {
  return join('/upms', path)
}
