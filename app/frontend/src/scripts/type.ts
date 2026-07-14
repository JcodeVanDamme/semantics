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
  URI: string,
  stateType: string
  population: number
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
