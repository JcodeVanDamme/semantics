import type {
  BackendTriple,
  TripleQueryResponse,
  StateDataResponse,
  HistoryApiResponse,
} from './type.ts'

// --- CONFIG ---
const BASE_URL = 'http://localhost:8080'
const RDF_ENDPOINT = '/semantics.rdf.system'
const DOMAIN_ENDPOINT = '/triples'

export const ONT_URI = 'http://semantics.rdf.system.ontology/'
export const DATA_URI = 'http://semantics.rdf.system.data/'

// --- UTIL ---
const logger = (method: string, url: string, payload?: any) => {
  const timestamp = new Date().toISOString()
  console.info(`[${timestamp}] API ${method} ${url}`, payload || '')
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

// --- 3. DOMAIN AGNOSTIC SERVICE ---
const triplesService = {
  async getTriples(filters?: { s?: string; p?: string; o?: string }): Promise<TripleQueryResponse> {
    const params = new URLSearchParams()
    if (filters?.s) params.append('s', filters.s)
    if (filters?.p) params.append('p', filters.p)
    if (filters?.o) params.append('o', filters.o)

    const query = params.toString()
    const url = query ? `${BASE_URL}${DOMAIN_ENDPOINT}?${query}` : `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('GET', url)
    return handleResponse<TripleQueryResponse>(await fetch(url))
  },

  async createTriple(triple: BackendTriple): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('POST', url, triple)
    return handleResponse<void>(
      await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(triple),
      }),
    )
  },

  async updateTriple(payload: { original: BackendTriple; update: BackendTriple }): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('PUT', url, payload)
    return handleResponse<void>(
      await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    )
  },

  async deleteTriple(triple: BackendTriple): Promise<void> {
    const url = `${BASE_URL}${DOMAIN_ENDPOINT}`
    logger('DELETE', url, triple)
    return handleResponse<void>(
      await fetch(url, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(triple),
      }),
    )
  },
}

// --- DOMAIN SPECIFIC SERVICE ---
const statesService = {
  async getActiveStateCount(): Promise<{ count: number }> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/activeStateCount`
    logger('GET', url)
    return handleResponse<{ count: number }>(await fetch(url))
  },

  async getStateChanges(): Promise<{ factor: number }> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/stateChanges`
    logger('GET', url)
    return handleResponse<{ factor: number }>(await fetch(url))
  },

  async getStates(activeOnly: boolean = false): Promise<StateDataResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/states?activeOnly=${activeOnly}`
    logger('GET', url)
    return handleResponse<StateDataResponse>(await fetch(url))
  },

  async mediatizate(payload: { absorbed: string; into: string }): Promise<HistoryApiResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/mediatizate`
    logger('POST', url, payload)
    return handleResponse<HistoryApiResponse>(
      await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    )
  },

  async changeRuler(payload: { state: string; ruler: string }): Promise<HistoryApiResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/changeRuler`
    logger('POST', url, payload)
    return handleResponse<HistoryApiResponse>(
      await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    )
  },

  async foundState(payload: {
    state: string
    population: number
    ruler: string
    label: string
    type: string
  }): Promise<HistoryApiResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/foundState`
    logger('POST', url, payload)
    return handleResponse<HistoryApiResponse>(
      await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    )
  },

  async getHistory(): Promise<HistoryApiResponse> {
    const url = `${BASE_URL}${RDF_ENDPOINT}/history`
    logger('GET', url)
    return handleResponse<HistoryApiResponse>(await fetch(url))
  },
}

// --- PUBLIC API EXPORT ---
export const api = {
  ...triplesService,
  ...statesService,
}
