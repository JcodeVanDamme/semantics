
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
    console.log('Queriying:')
    console.log('S: ' + filters?.s)
    console.log('P: ' + filters?.p)
    console.log('O: ' + filters?.o)

    const params = new URLSearchParams()
    if (filters?.s) params.append('s', filters.s)
    if (filters?.p) params.append('p', filters.p)
    if (filters?.o) params.append('o', filters.o)

    const response = await fetch(`${BASE_URL}/triples?${params.toString()}`)
    console.log(response)
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

  async getActiveStateCount(): Promise<{ count: number }> {
    const response = await fetch(`${BASE_URL}/semantics.rdf.system/activeStateCount`)
    return handleResponse<{ count: number }>(response)
  },

  async getStateChanges(): Promise<{ factor: number }> {
    const response = await fetch(`${BASE_URL}/semantics.rdf.system/stateChanges`)
    return handleResponse<{ factor: number }>(response)
  },

  async getActiveStates(): Promise<StateDataResponse> {
    const response = await fetch(`${BASE_URL}/semantics.rdf.system/activeStates`)
    return handleResponse<StateDataResponse>(response)
  },

  async getStates(): Promise<StateDataResponse> {
    const response = await fetch(`${BASE_URL}/semantics.rdf.system/states`)
    return handleResponse<StateDataResponse>(response)
  },

  async mediatizate(payload: { absorbed: string; into: string }): Promise<HistoryResponse> {
    console.log('Mediatizating')
    console.log(payload)

    const response = await fetch(`${BASE_URL}/semantics.rdf.system/mediatizate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    return handleResponse<HistoryResponse>(response)
  },

  async getHistory(): Promise<HistoryResponse> {
    const response = await fetch(`${BASE_URL}/semantics.rdf.system/history`)
    return handleResponse<HistoryResponse>(response)
  },
}
