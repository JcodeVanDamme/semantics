// --- INTERFACES ---

export interface RDFObject {
  value: string
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

export interface HistoryApiResponse {
  history: HistoryEvent[]
}

export interface TripleAction {
  action: string
  triple: BackendTriple
}

export interface HistoryEvent {
  action: string
  timeStamp: string
  triples: TripleAction[]
}

export interface Ruler {
  name: string // LITERAL
  URI: string // URI
  title: string // LITERAL
}

export interface Region {
  name: string // LITERAL
  type: string // LITERAL
  population: number // LITERAL
}

export interface MediatizatedState {
  name: string // LITERAL
  stateType: string // LITERAL
  ruler: Ruler
}

export interface StateData {
  name: string // LITERAL
  URI: string // URI
  stateType: string // LITERAL
  population: number // LITERAL
  ruler: Ruler
  regions: Region[]
  mediatizatedStates: MediatizatedState[]
}

export interface StateDataResponse {
  states: StateData[]
}

export interface EnhancedTriple {
  id: string | number
  subject: string
  rawSubject: string
  predicate: string
  rawPredicate: string
  object: string
  rawObject: string
  isLiteral: boolean
  action?: string
  raw?: BackendTriple
}

export interface ChangeRulerRequest {
  state: string // URI
  ruler: string // URI
  label: string // LITERAL
  title: string // LITERAL
}

export interface FoundStateRequest {
  state: string // URI
  label: string // LITERAL
  ruler: string // URI
  population: number // LITERAL
  type: string // LITERAL
}

interface MediatizationRequest {
  absorbed: string // URI
  into: string // URI
}
