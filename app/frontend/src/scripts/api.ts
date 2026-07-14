import type {
  BackendTriple,
  TripleQueryResponse,
  StateDataResponse,
  HistoryEvent,
} from './type.ts'

const BASE_URL = 'http://localhost:8080'
const RDF_ENDPOINT = '/semantics.rdf.system'
const DOMAIN_ENDPOINT = '/triples'

export const ONT_URI = 'http://semantics.rdf.system.ontology/'
export const DATA_URI = 'http://semantics.rdf.system.data/'

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

  // Handle successful empty responses (like 201 Created or 204 No Content)
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

  /**
   * Fetches state information.
   * @param activeOnly Filters response to return exclusively currently active states when true.
   */
  async getStates(activeOnly: boolean = false): Promise<StateDataResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/states?activeOnly=${activeOnly}`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<StateDataResponse>(response)
  },

  async mediatizate(payload: { absorbed: string; into: string }): Promise<HistoryEvent[]> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/mediatizate`
    logger('POST', url, payload)

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryEvent[]>(response)
  },

  async changeRuler(payload: { state: string; ruler: string }): Promise<HistoryEvent[]> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/changeRuler`
    logger('POST', url, payload)

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryEvent[]>(response)
  },

  async foundState(payload: {
    state: string // URI
    population: number // LITERAL
    ruler: string // URI
    label: string // LITERAL
    type: string // LITERAL
  }): Promise<HistoryEvent[]> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/foundState`
    logger('POST', url, payload)

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryEvent[]>(response)
  },

  async getHistory(): Promise<HistoryEvent[]> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/history`
    logger('GET', url)

    const response = await fetch(url)
    return handleResponse<HistoryEvent[]>(response)
  },
}
