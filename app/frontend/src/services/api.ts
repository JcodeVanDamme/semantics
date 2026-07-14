const BASE_URL = 'http://localhost:8080'
const RDF_ENDPOINT = '/semantics.rdf.system'
const DOMAIN_ENDPOINT = '/triples'

export interface RDFTerm {
  value: string
}

export interface RDFObject extends RDFTerm {
  isLiteral: boolean
}

export interface BackendTriple {
  s: RDFObject
  p: RDFObject
  o: RDFObject
}

export interface TripleQueryResponse {
  count: number
  triples: BackendTriple[]
}

export interface TripleAction {
  action: string
  triple: BackendTriple
}

export interface HistoryResponse {
  action: string
  timeStamp: string
  triples: TripleAction[]
}

export interface Ruler {
  name: string
  URI: string
  title: string
}

export interface Region {
  name: string
  type: string
  population: number
}

export interface MediatizatedState {
  name: string
  stateType: string
  ruler: Ruler
}

export interface StateData {
  name: string
  URI: string
  stateType: string
  population: number
  ruler: Ruler
  regions: Region[]
  mediatizatedStates: MediatizatedState[]
}

export interface StateDataResponse {
  states: StateData[]
}

// --- HELPER FUNCTIONS ---

const logger = (method: string, url: string, payload?: any) => {
  const timestamp = new Date().toISOString()
  if (payload) {
    console.info(`[${timestamp}] API ${method} ${url}`, payload)
  } else {
    console.info(`[${timestamp}] API ${method} ${url}`)
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text().catch(() => 'Unknown Error')
    console.error(`[API ERROR] ${response.url} - ${response.status}: ${errorText}`)
    throw new Error(`${errorText || response.statusText}`)
  }

  if (response.status === 201 || response.headers.get('content-length') === '0') {
    return {} as T
  }

  return response.json()
}

export const api = {
  /* --- DOMAIN-AGNOSTIC CRUD ENDPOINTS --- */

  async getTriples(filters?: { s?: string; p?: string; o?: string }): Promise<TripleQueryResponse> {
    const params = new URLSearchParams()
    if (filters?.s) params.append('s', filters.s)
    if (filters?.p) params.append('p', filters.p)
    if (filters?.o) params.append('o', filters.o)

    const query = params.toString()
    const url = query ? `${BASE_URL}${DOMAIN_ENDPOINT}?${query}` : `${BASE_URL}${DOMAIN_ENDPOINT}`

    logger('GET', url)
    const response = await fetch(url)
    return handleResponse<TripleQueryResponse>(response)
  },

  async createTriple(triple: BackendTriple): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('POST', url, triple)

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(triple),
    })
    return handleResponse<void>(response)
  },

  async updateTriple(payload: { original: BackendTriple; update: BackendTriple }): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('PUT', url, payload)

    const response = await fetch(url, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<void>(response)
  },

  async deleteTriple(triple: BackendTriple): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('DELETE', url, triple)

    const response = await fetch(url, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(triple),
    })
    return handleResponse<void>(response)
  },

  async sparqlQuery(query: string): Promise<TripleQueryResponse> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}/sparql`
    logger('POST', url, { query })

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query }),
    })
    return handleResponse<TripleQueryResponse>(response)
  },

  /* --- DOMAIN SPECIFIC ENDPOINTS --- */

  async getActiveStateCount(): Promise<{ count: number }> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/activeStateCount`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<{ count: number }>(response)
  },

  async getStateChanges(): Promise<{ factor: number }> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/stateChanges`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<{ factor: number }>(response)
  },

  async getActiveStates(): Promise<StateDataResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/activeStates`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<StateDataResponse>(response)
  },

  async getStates(): Promise<StateDataResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/states`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<StateDataResponse>(response)
  },

  async mediatizate(payload: { absorbed: string; into: string }): Promise<HistoryResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/mediatizate`
    logger('POST', url, payload)

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryResponse>(response)
  },

  async getHistory(): Promise<HistoryResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/history`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<HistoryResponse>(response)
  },
}
