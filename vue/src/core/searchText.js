export function highlightParts(text, keyword) {
  const value = String(text ?? '')
  const terms = [...new Set(String(keyword ?? '').trim().split(/\s+/).filter(Boolean))]
    .sort((left, right) => right.length - left.length)
  if (!terms.length) return [{ text: value, highlighted: false }]
  const expression = new RegExp(terms.map(term => term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('|'), 'gi')
  const parts = []
  let offset = 0
  for (const match of value.matchAll(expression)) {
    if (match.index > offset) parts.push({ text: value.slice(offset, match.index), highlighted: false })
    parts.push({ text: match[0], highlighted: true })
    offset = match.index + match[0].length
  }
  if (offset < value.length) parts.push({ text: value.slice(offset), highlighted: false })
  return parts
}
