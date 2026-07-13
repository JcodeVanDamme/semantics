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
  s: RDFTerm
  p: RDFTerm
  o: RDFObject
}

export interface TripleQueryResponse {
  count: number
  triples: BackendTriple[]
}

export interface HistoryAction {
  action: string
  triple: {
    s: string
    p: string
    o: string
  }
}

export interface HistoryResponse {
  count: number
  triples: HistoryAction[]
}

export interface Ruler {
  name: string
  title: string
}

export interface MediatizedStateSummary {
  name: string
  stateType: string
}

export interface RegionSummary {
  name: string
  type: string
}

export interface StateData {
  name: string
  ruler: Ruler
  mediatizatedStates: {
    count: number
    states: MediatizedStateSummary[]
  }
  regions: {
    count: number
    regions: RegionSummary[]
  }
  population: number
  stateType: string
}

export interface StateDataResponse {
  states: StateData[]
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text().catch(() => 'Unknown Error')
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
    console.log('Querying Triple Store...')
    console.log('S: ', filters?.s)
    console.log('P: ', filters?.p)
    console.log('O: ', filters?.o)

    const params = new URLSearchParams()
    if (filters?.s) params.append('s', filters.s)
    if (filters?.p) params.append('p', filters.p)
    if (filters?.o) params.append('o', filters.o)

    const response = await fetch(`${BASE_URL}/triples?${params.toString()}`)
    return handleResponse<TripleQueryResponse>(response)
  },

  async createTriple(triple: BackendTriple): Promise<void> {
    const response = await fetch(`${BASE_URL}/triples`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(triple),
    })
    return handleResponse<void>(response)
  },

  async updateTriple(payload: { original: BackendTriple; update: BackendTriple }): Promise<void> {
    const response = await fetch(`${BASE_URL}/triples`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<void>(response)
  },

  async deleteTriple(triple: BackendTriple): Promise<void> {
    const response = await fetch(`${BASE_URL}/triples`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(triple),
    })
    return handleResponse<void>(response)
  },

  async sparqlQuery(query: string): Promise<TripleQueryResponse> {
    const response = await fetch(`${BASE_URL}/triples/sparql`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query }),
    })
    return handleResponse<TripleQueryResponse>(response)
  },

  /* --- DOMAIN SPECIFIC ENDPOINTS --- */

  async getActiveStates(): Promise<{ count: number }> {
    const response = await fetch(`${BASE_URL}/ops/activeStates`)
    return handleResponse<{ count: number }>(response)
  },

  async getStateChanges(): Promise<{ factor: number }> {
    const response = await fetch(`${BASE_URL}/ops/stateChanges`)
    return handleResponse<{ factor: number }>(response)
  },

  async getStates(): Promise<StateDataResponse> {
    const response = await fetch(`${BASE_URL}/ops/states`)
    return handleResponse<StateDataResponse>(response)
  },

  async mediatizate(payload: { absorbed: string; into: string }): Promise<HistoryResponse> {
    const response = await fetch(`${BASE_URL}/ops/mediatizate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryResponse>(response)
  },

  async changeRuler(payload: { state: string; ruler: string }): Promise<HistoryResponse> {
    const response = await fetch(`${BASE_URL}/ops/changeRuler`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryResponse>(response)
  },

  async foundState(payload: {
    stateName: string
    stateType: string
    rulerName: string
  }): Promise<HistoryResponse> {
    const response = await fetch(`${BASE_URL}/ops/foundState`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryResponse>(response)
  },

  async getHistory(): Promise<HistoryResponse> {
    const response = await fetch(`${BASE_URL}/ops/history`)
    return handleResponse<HistoryResponse>(response)
  },
}
