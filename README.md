# API Documentation

[Go to section](#gabba)

## Domain-Agnostic-Endpoints (CRUD)

- [**POST** `/triples`](#triplesCreate) - Create a new triple

- [**GET** `/triples`](#triplesRead) - Query triples with optional filters (`s`, `p`, `o`)

- [**POST** `/triples/sparql`](#triplesReadSparql) - Query triples using SPARQL

- [**PUT** `/triples`](#triplesUpdate) - Update an existing triple

- [**DELETE** `/triples`](#triplesDelete) - Delete a triple

## Domain-Specific-Endpoints

- [**GET** `/ops/activeStates`](#activeStates) - Retrieve the number of active States

- [**GET** `/ops/stateChanges`](#stateChanges) - Retrieve the difference of active States and original States

- [**GET** `/ops/states`](#states) - Retrieve relevant Information of all active States

- [**POST** `/ops/mediatizate`](#mediatizate) - Mediatizate a State into another one

- [**POST** `/ops/changeRuler`](#changeRuler) - Change the Ruler of a State

- [**POST** `/ops/foundState`](#foundState) - Found a new State

- [**GET** `/ops/history`](#history) - Retrieve the History of State-Relevant-Actions


---
# Domain-Agnostic-Endpoints (CRUD)

## Create Triple <a id="triplesCreate"></a>

- Writes the passed Triple into the Triple-Store

### Endpoint
```
/triples
```

### Method
```
POST
```

### Response Codes

| Code | Meaning |
|------|------|
| `201 CREATED` | Triple created successfully |
| `400 Bad Request` | Invalid query parameters|
| `409 Conflict` | Triple already exists in the Triple-Store |
| `500 Internal Server Error` | Server-side processing error |


### Request Body
```json

{
    "s": "gabba",
    "p": "gabba",
    "o": "gabba"
}

```

### Request Schema

| Field | Type | Description |
|------|------|------|
| `s` | string | Subject of the new Triple |
| `p` | string | Predicate of the new Triple |
| `o` | string | Object of the new Triple |

### Response Body
```json
{}
```

---

## Read Triples (Triple-Query) <a id="triplesRead"></a>

- Returns Triples matching the passed Filters
- Returns the whole Triple-Store if all Filters are left null

### Endpoint
```
/triples/?s=&p=&o=
```

### Method
```
GET
```

### Query Parameters

| Name | Type | Required | Description |
|------|------|------|------|
| `s` | string | No | Subject filter |
| `p` | string | No | Predicate filter |
| `o` | string | No | Object filter |


### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Query executed successfully |
| `400 Bad Request` | Invalid query parameters |
| `500 Internal Server Error` | Server-side processing error |


### Response Body
```json
{
  "count": 1,
  "triples": [
    {
      "s": "gabba",
      "p": "gabba",
      "o": "gabba"
    }
  ]
}
```

### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of returned triples |
| `triples` | array | List of matching triples |
| `triples[].s` | string | Subject |
| `triples[].p` | string | Predicate |
| `triples[].o` | string | Object |

---

## Read Triples (SPARQL-Query) <a id="triplesReadSparql"></a>

- Returns Triples matching the passed SPARQL-Query

### Endpoint
```
/triples/sparql
```

### Method
```
POST
```

### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Query executed successfully |
| `400 Bad Request` | Invalid Request Body |
| `500 Internal Server Error` | Server-side processing error |


### Request Body
```json
{
  "query": "SELECT ?s ?p ?o WHERE { ... }"
}
```

### Request Schema

| Field | Type | Description |
|------|------|------|
| `query` | string | SPARQL-Query |

### Response Body
```json
{
  "count": 1,
    "triples": [
      {
        "s": "gabba",
        "p": "gabba",
        "o": "gabba"
      }
    ]
}
```

### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of returned triples |
| `triples` | array | List of matching triples |
| `triples[].s` | string | Subject |
| `triples[].p` | string | Predicate |
| `triples[].o` | string | Object |

---

## Update Triple <a id="triplesUpdate"></a>

- Updates the passed Triple by first performing a delete, followed by a create

### Endpoint
```text
/triples
```

### Method
```text
PUT
```

### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Triple updated successfully |
| `400 Bad Request` | Invalid request body |
| `404 Not Found` | Original Triple does not exist |
| `409 Conflict` | Updated Triple already exists |
| `500 Internal Server Error` | Server-side processing error |

### Request Body
```json
{
  "original": {
    "s": "gabba",
    "p": "gabba",
    "o": "gabba"
  },
  "update": {
    "s": "gabba",
    "p": "gabba",
    "o": "gabba"
  }
}
```

### Request Schema

| Field | Type | Description |
|------|------|------|
| `original` | object | Triple to replace |
| `original.s` | string | Original Subject |
| `original.p` | string | Original Predicate |
| `original.o` | string | Original Object |
| `update` | object | New Triple |
| `update.s` | string | Updated Subject |
| `update.p` | string | Updated Predicate |
| `update.o` | string | Updated Object |

### Response Body
```json
{}
```

---

## Delete Triple <a id="triplesDelete"></a>

- Deletes the passed Triple from the Triple-Store

### Endpoint
```text
/triples
```

### Method
```text
DELETE
```

### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Triple deleted successfully |
| `400 Bad Request` | Invalid request body |
| `404 Not Found` | Triple does not exist |
| `500 Internal Server Error` | Server-side processing error |

### Request Body
```json
{
  "s": "gabba",
  "p": "gabba",
  "o": "gabba"
}
```

### Request Schema

| Field | Type | Description |
|------|------|------|
| `s` | string | Triple Subject |
| `p` | string | Triple Predicate |
| `o` | string | Triple Object |

### Response Body
```json
{}
```

---

# Domain-Specific-Endpoints <a id="domainSpecific"></a>

### Get active States <a id="activeStates"></a>

- Returns the number of currently active States

#### Endpoint
```text
/ops/activeStates
```

#### Method
```text
GET
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Active State count returned successfully |
| `500 Internal Server Error` | Server-side processing error |

#### Response Body
```json
{
  "count": 0
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of active States |

---

### Get State-Change-Factor

- Returns the difference between the original number of States and the currently active States

#### Endpoint
```text
/ops/stateChanges
```

#### Method
```text
GET
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | State-Change-Factor returned successfully |
| `500 Internal Server Error` | Server-side processing error |

#### Response Body
```json
{
  "factor": 0.0 
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `factor` | float | Difference factor between original and currently active States |

---

### Get State-Data <a id="states"></a>

- Returns all relevant data from all active States

#### Endpoint
```text
/ops/states
```

#### Method
```text
GET
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | State data returned successfully |
| `500 Internal Server Error` | Server-side processing error |

#### Response Body
```json
{
  "count": 0,
  "states": [
    {
      "name": "gabba",
      "ruler": {
        "name": "gabba",
        "title": "gabba"
      },
      "mediatizatedStates": {
        "count": 0,
        "states": [
          {
            "name": "gabba",
            "stateType": "gabba"
          }
        ]
      },
      "regions": {
        "count": 0,
        "regions": [
		  {
		    "name": "gabba",
            "type": "gabba"
		  }
        ]"
      },
      "population": 0,
      "stateType": "gabba"
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of active States |
| `states` | array | List of active States |
| `states[].name` | string | Name of the State |
| `states[].ruler` | object | Current ruler of the State |
| `states[].ruler.name` | string | Name of the ruler |
| `states[].ruler.title` | string | Title of the ruler |
| `states[].mediatizatedStates` | object | States mediatizated by this State |
| `states[].mediatizatedStates.count` | integer | Number of mediatizated States |
| `states[].mediatizatedStates.states` | array | List of mediatizated States |
| `states[].mediatizatedStates.states[].name` | string | Name of the mediatizated State |
| `states[].mediatizatedStates.states[].stateType` | string | Type of the mediatizated State |
| `states[].regions` | object | Region Data associated with this State |
| `states[].regions.count` | integer | Number of associated regions |
| `states[].regions.regions` | array | List of Regions associated with this State|
| `states[].regions.regions[].name` | string | Name of the region |
| `states[].regions.regions[].type` | string | Type of the region |
| `states[].population` | integer | Population of the State |
| `states[].stateType` | string | Type of the State |

---

### Mediatizate State <a id="mediatizate"></a>

- Performs all necessary Triple-Store-Actions to mark the State whose name is passed under `absorbed` as inactive aswell as performing all Actions for it to be mediatizated into the State whose Name is passed under `into`
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/ops/history**

#### Endpoint
```text
/ops/mediatizate
```

#### Method
```text
POST
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | State mediatizated successfully |
| `400 Bad Request` | Invalid request body |
| `404 Not Found` | One or more referenced States do not exist |
| `409 Conflict` | State cannot be mediatizated into itself |
| `500 Internal Server Error` | Server-side processing error |

#### Request Body
```json
{
  "absorbed": "gabba",
  "into": "gabba"
}
```

#### Request Schema

| Field | Type | Description |
|------|------|------|
| `absorbed` | string | Name of the State to mediatizate |
| `into` | string | Name of the target State |

#### Response Body
```json
{
  "count": 0,
  "triples": [
    {
      "action": "gabba",
      "triple": {
        "s": "gabba",
        "p": "gabba",
        "o": "gabba"
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of resulting Triple-Store-Actions |
| `triples` | array | List of resulting Triple-Store-Actions |
| `triples[].action` | string | Type of Triple-Store-Action |
| `triples[].triple` | object | Triple affected by the Action |
| `triples[].triple.s` | string | Triple Subject |
| `triples[].triple.p` | string | Triple Predicate |
| `triples[].triple.o` | string | Triple Object |

---

### Change State-Ruler <a id="changeRuler"></a>

- Performs all necessary Triple-Store-Actions to change the Ruler of the State whose Name is passed under `state` to the Ruler whose name is passed under `ruler`
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/ops/history**

#### Endpoint
```
/ops/changeRuler
```

#### Method
```
POST
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | State mediatizated successfully |
| `400 Bad Request` | Invalid request body |
| `404 Not Found` | Referenced State or Ruler does not exist |
| `500 Internal Server Error` | Server-side processing error |

#### Request Body
```json
{
  "state": "gabba",
  "ruler": "gabba"
}
```

#### Request Schema

| Field | Type | Description |
|------|------|------|
| `state` | string | Name of the State to have its Ruler updated |
| `ruler` | string | Name of the updated Ruler |

#### Response Body
```json
{
  "count": 0,
  "triples": [
    {
      "action": "gabba",
      "triple": {
        "s": "gabba",
        "p": "gabba",
        "o": "gabba"
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of resulting Triple-Store-Actions |
| `triples` | array | List of resulting Triple-Store-Actions |
| `triples[].action` | string | Type of Triple-Store-Action |
| `triples[].triple` | object | Triple affected by the Action |
| `triples[].triple.s` | string | Triple Subject |
| `triples[].triple.p` | string | Triple Predicate |
| `triples[].triple.o` | string | Triple Object |

---

### Found State  <a id="foundState"></a>

- Performs all necessary Triple-Store-Actions to create the State per Data passed in the Request-Body
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/ops/history**

#### Endpoint
```
/ops/foundState
```

#### Method
```
POST
```

#### Response Codes

| Code | Meaning |
|------|------|
| `201 CREATED` | State founded successfully |
| `400 Bad Request` | Invalid request body |
| `404 Not Found` | Referenced Ruler does not exist |
| `409 Conflict` | Triple already exists |
| `500 Internal Server Error` | Server-side processing error |

#### Request Body
```json
{
  "stateName": "gabba",
  "stateType": "gabba",
  "rulerName": "gabba"
}
```

#### Request Schema

| Field | Type | Description |
|------|------|------|
| `stateName` | string | Name of the new State |
| `stateType` | string | Type of the new State |
| `rulerName` | string | Name of the States Ruler |

#### Response Body
```json
{
  "count": 0,
  "triples": [
    {
      "action": "gabba",
      "triple": {
        "s": "gabba",
        "p": "gabba",
        "o": "gabba"
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of resulting Triple-Store-Actions |
| `triples` | array | List of resulting Triple-Store-Actions |
| `triples[].action` | string | Type of Triple-Store-Action |
| `triples[].triple` | object | Triple affected by the Action |
| `triples[].triple.s` | string | Triple Subject |
| `triples[].triple.p` | string | Triple Predicate |
| `triples[].triple.o` | string | Triple Object |

---

### Get Triple-Action-History <a id="history"></a>

- Returns a List of all State-Relevant-Triple-Store-Actions resulting from calls to **/ops/mediatizate**, **/ops/changeRuler** and **/ops/foundState**

#### Endpoint
```
/ops/history
```

#### Method
```
GET
```

#### Response Body
```json
{
  "count": 0,
  "triples": [
    {
      "action": "gabba",
      "triple": {
        "s": "gabba",
        "p": "gabba",
        "o": "gabba"
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
|------|------|------|
| `count` | integer | Number of logged Triple-Store-Actions |
| `triples` | array | List of logged Triple-Store-Actions |
| `triples[].action` | string | Type of Triple-Store-Action |
| `triples[].triple` | object | Logged Triple|
| `triples[].triple.s` | string | Triple Subject |
| `triples[].triple.p` | string | Triple Predicate |
| `triples[].triple.o` | string | Triple Object |