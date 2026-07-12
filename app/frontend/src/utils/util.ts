export const DEFAULT_NAMESPACE = 'http://semantics.rdf.system/'
export const URI_REGEX = /^[a-zA-Z][a-zA-Z0-9+.-]*:\S+$/

export interface TableTriple {
  id?: number | string
  subject: string
  predicate: string
  object: string
  isLiteral?: boolean
}

export function isValidUri(uri: string): boolean {
  return URI_REGEX.test(uri.trim())
}

export function stripSpaces(val: string): string {
  return val.replace(/\s+/g, '')
}

export function concatUri(base: string, token: string): string {
  const cleanToken = token.trim()
  if (!cleanToken) return ''

  const cleanBase = base.trim() || DEFAULT_NAMESPACE
  const needsSlash = !cleanBase.endsWith('/') && !cleanBase.endsWith('#')

  return `${cleanBase}${needsSlash ? '/' : ''}${cleanToken}`
}

export function humanizeString(str: string): string {
  if (!str) return '';

  const separated = str
    // 1. Replace all global instances of underscores with a standard space
    .replace(/_/g, ' ')
    // 2. Insert a space between any lowercase letter followed immediately by an uppercase letter
    .replace(/([a-z])([A-Z])/g, '$1 $2');

  // 3. Find the first letter of every word boundary (\b) and capitalize it
  return separated.replace(/\b([a-z])/g, (match) => match.toUpperCase());
}

export function stripUri(uriStr: string): string {
  if (!uriStr) return ''

  if (!uriStr.includes('://')) {
    return humanizeString(uriStr)
  }

  const lastSlash = uriStr.lastIndexOf('/')
  const lastHash = uriStr.lastIndexOf('#')
  const splitIndex = Math.max(lastSlash, lastHash)

  if (splitIndex !== -1 && splitIndex < uriStr.length - 1) {
    const localName = uriStr.substring(splitIndex + 1)
    return humanizeString(localName)
  }

  return humanizeString(uriStr)
}

export function cleanTripleForDisplay(triple: any) {
  return {
    id: triple.id,
    isLiteral: triple.isLiteral,

    // 1. Humanized display values (clean strings)
    subject: stripUri(triple.subject),
    predicate: stripUri(triple.predicate),
    object: triple.isLiteral ? humanizeString(triple.object) : stripUri(triple.object),

    // 2. Preserved raw values (full URIs)
    rawSubject: triple.subject,
    rawPredicate: triple.predicate,
    rawObject: triple.object,
  }
}

export function getRdfValidationError(payload: {
  subjectUri: string
  predicateUri: string
  objectUri: string
  objectMode: 'uri' | 'literal' | string
}): string | null {
  const formatMsg = (field: string) =>
    `Invalid URI format in ${field} URI field.\n\nMust start with a letter, contain ":" and look like an absolute namespace protocol with no whitespaces.`

  if (payload.subjectUri.trim() && !isValidUri(payload.subjectUri)) return formatMsg('Subject')
  if (payload.predicateUri.trim() && !isValidUri(payload.predicateUri))
    return formatMsg('Predicate')
  if (payload.objectMode === 'uri' && payload.objectUri.trim() && !isValidUri(payload.objectUri)) {
    return formatMsg('Object')
  }
  return null
}

export function getRdfValidationNote(payload: {
  subject: string
  predicate: string
  object: string
}): string | null {
  const lines: string[] = []

  if (/\s/.test(payload.subject)) {
    lines.push(`Resulting Subject: "${stripSpaces(payload.subject)}"`)
  }
  if (/\s/.test(payload.predicate)) {
    lines.push(`Resulting Predicate: "${stripSpaces(payload.predicate)}"`)
  }

  return lines.length > 0
    ? `Spaces will be automatically stripped upon transmission:\n\n${lines.join('\n\n')}`
    : null
}
