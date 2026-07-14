# About

Implementation of a compact RDF store based on the BMatrix/k2-tree approach proposed by Nieves R. Brisaboa et al. in their 2020 Paper [*Revisiting compact RDF stores based on k2-trees*](https://arxiv.org/abs/2002.11622).
Developed in Java with a Spring Boot backend and Vue.js frontend.
The accompanying web application models and explores the mediatization and territorial restructuring of the german territories during the early 19th century.


# API Documentation

## Domain-Agnostic-Endpoints (CRUD)

- [**POST** `/triples`](#create-triple) - Create a new triple

- [**GET** `/triples`](#read-triples-triple-query) - Query triples with optional filters (`s`, `p`, `o`)

- [**PUT** `/triples`](#update-triple) - Update an existing triple

- [**DELETE** `/triples`](#delete-triple) - Delete a triple

## Domain-Specific-Endpoints

- [**GET** `/semantics.rdf.system/activeStateCount`](#get-active-states) - Retrieve the number of active States

- [**GET** `/semantics.rdf.system/stateChanges`](#get-state-change-factor) - Retrieve the difference of active States and original States

- [**GET** `/semantics.rdf.system/states`](#get-state-data) - Retrieve relevant Information of all active States

- [**POST** `/semantics.rdf.system/mediatizate`](#mediatizate-state) - Mediatizate a State into another one

- [**POST** `/semantics.rdf.system/changeRuler`](#change-state-ruler) - Change the Ruler of a State

- [**POST** `/semantics.rdf.system/foundState`](#found-state) - Found a new State

- [**GET** `/semantics.rdf.system/history`](#get-state-action-history) - Retrieve the History of State-Relevant-Actions


---

# Create Triple

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
  "s": { "value": "gabba" },
  "p": { "value": "gabba" },
  "o": { "value": "gabba", "isLiteral": false }
}

```

### Request Schema

| Field | Type | Description |
|:---|:---|:---|
| `s` | object | Subject of the Triple to be created. |
| `s.value` | String | The value of the subject (**Must always be a URI**). |
| `p` | object | Predicate of the Triple to be created. |
| `p.value` | String | The value of the predicate (**Must always be a URI**). |
| `o` | object | Object of the Triple to be created. |
| `o.value` | String | The value of the object (can be a URI or a literal text/number). |
| `o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

### Response Body
```json
{}
```

---

# Read Triples (Triple-Query)

- Returns Triples matching the passed Filters
- Returns the whole Triple-Store if all Filters are left null

### Endpoint
```
/triples?s=&p=&o=
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
| `500 Internal Server Error` | Server-side processing error |


### Response Body
```json
{
  "count": 1,
  "triples": [
    {
      "s": { "value": "gabba" },
      "p": { "value": "gabba" },
      "o": { "value": "gabba", "isLiteral": false }
    }
  ]
}
```

### Response Schema

| Field | Type | Description |
|------|------|------|
|count|integer|The Number of Triples matching the Query.|#
|triples|array| Array containing the matched Triple Objects. |
| `triples[].s` | object | The Triple Subject. |
| `triples[].s.value` | String | The value of the subject. |
| `triples[].s.isLiteral` | Boolean | Always `false`. |
| `triples[].p` | object | The Triple Predicate. |
| `triples[].p.value` | String | The value of the predicate. |
| `triples[].p.isLiteral` | Boolean | Always `false`. |
| `triples[].o` | object | The Triple Object. |
| `triples[].o.value` | String | The value of the object (can be a URI or a literal. |
| `triples[].o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

---

# Update Triple

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
    "s": { "value": "gabba"},
    "p": { "value": "gabba"},
    "o": { "value": "gabba", "isLiteral": false }
  },
  "update": {
    "s": { "value": "gabba"},
    "p": { "value": "gabba"},
    "o": { "value": "gabba", "isLiteral": false }
  }
}
```

### Request Schema

| Field | Type | Description |
|:---|:---|:---|
| **`original`** | object | The existing triple to be removed from the matrix. |
| `original.s` | object | The original subject. |
| `original.s.value` | String | The value of the subject (**Must always be a URI**). |
| `original.p` | object | The original predicate. |
| `original.p.value` | String | The value of the predicate (**Must always be a URI**). |
| `original.o` | object | The original object. |
| `original.o.value` | String | The value of the object (can be a URI or a literal text/number). |
| `original.o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |
| **`update`** | object | The new triple that will replace the original one. |
| `update.s` | object | The new subject. |
| `update.s.value` | String | The value of the new subject (**Must always be a URI**). |
| `update.p` | object | The new predicate. |
| `update.p.value` | String | The value of the new predicate (**Must always be a URI**). |
| `update.o` | object | The new object. |
| `update.o.value` | String | The value of the new object. |
| `update.o.isLiteral` | Boolean | `true` if the new object value is a literal; `false` if it is a URI. |

### Response Body
```json
{}
```

---

# Delete Triple

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
  "s": { "value": "gabba"},
  "p": { "value": "gabba"},
  "o": { "value": "gabba", "isLiteral": false }
}
```

### Request Schema

| Field | Type | Description |
|:---|:---|:---|
| `s` | object | Subject of the Triple to be deleted. |
| `s.value` | String | The value of the subject (**Must always be a URI**). |
| `p` | object | Predicate of the Triple to be deleted. |
| `p.value` | String | The value of the predicate (**Must always be a URI**). |
| `o` | object | Object of the Triple to be deleted. |
| `o.value` | String | The value of the object (can be a URI or a literal text/number). |
| `o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

### Response Body
```json
{}
```

---

## Get Number of active  States

- Returns the number of currently active States

#### Endpoint
```text
/semantics.rdf.system/activeStateCount
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

## Get State-Change-Factor

- Returns the difference between the original number of States and the currently active States

#### Endpoint
```text
/semantics.rdf.system/stateChanges
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

## Get State-Data

- Returns all relevant data from all active States

#### Endpoint
```text
/semantics.rdf.system/states
```

#### Method
```text
GET
```

### Query Parameters

| Name | Type | Required | Default | Description |
|------|------|------|------|------|
| `activeOnly` | Boolean | No | `false` | If true, filters the response to return exclusively currently active states.|

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | State data returned successfully |
| `500 Internal Server Error` | Server-side processing error |

#### Response Body
```json
{
  "states": [
    {
      "name": "Kingdom of Prussia",
      "stateType": "Kingdom",
      "population": 18491000,
      "ruler": {
          "name": "Wilhelm I",
          "title": "King of Prussia"
      },
      "regions": [
        {
            "name": "East Prussia",
            "type": "Province",
            "population": 1500000
        },
        {
            "name": "West Prussia",
            "type": "Province",
            "population": 900000
        }
      ],
      "mediatizatedStates": [
        {
          "name": "Duchy of Anhalt",
          "stateType": "Duchy",
          "ruler": {
              "name": "Leopold IV",
              "title": "Duke of Anhalt"
          }
        }
      ]
    }
  ]
}
```

#### Response Schema

### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `states` | Array | List of state objects. |
| `states[].name` | String | Name of the state. |
| `states[].stateType` | String | Classification of the state. |
| `states[].population` | Integer | Population of the state itself. |
| `states[].ruler.name` | String | Name of the states ruler. |
| `states[].ruler.title` | String | Title of the states ruler. |
| `states[].regions` | Array | List of regions within the state. |
| `states[].regions[].name` | String | Name of the region. |
| `states[].regions[].type` | String | Type of the region. |
| `states[].regions[].population` | Integer | Population of the region. |
| `states[].mediatizatedStates` | Array | List states mediatized by this state. |
| `states[].mediatizatedStates[].name` | String | Name of the mediatized state. |
| `states[].mediatizatedStates[].stateType` | String | Classification of the mediatized state. |
| `states[].mediatizatedStates[].ruler.name` | String | Name of the mediatized state's ruler. |
| `states[].mediatizatedStates[].ruler.title` | String | Title of the mediatized state's ruler. |

---

## Mediatizate State

- Performs all necessary Triple-Store-Actions to mark the State whose name is passed under `absorbed` as inactive aswell as performing all Actions for it to be mediatizated into the State whose Name is passed under `into`
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/semantics.rdf.system/history**

#### Endpoint
```text
/semantics.rdf.system/mediatizate
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
  "history": [
      {
        "action": "STATE_FOUNDING",
        "timeStamp": "2026-07-14T06:38:49.010883400Z",
        "triples": [
            {
                "action": "Created",
                "triple": {
                    "s": {
                        "value": "http://semantics.rdf.system.data/State_new",
                        "isLiteral": false
                    },
                    "p": {
                        "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                        "isLiteral": false
                    },
                    "o": {
                        "value": "http://semantics.rdf.system.ontology/State",
                        "isLiteral": false
                    }
                }
            }
        ]
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `history` | Array | Wrapper containing the list of events. |
| `history[].action` | String | The event identifier. |
| `history[].timeStamp` | String | The ISO-8601 timestamp when the event occurred. |
| `history[].triples` | Array | A collection of modifications applied to the triple store during this event. |
| `history[].triples[].action` | String | The operation performed with the Triple. |
| `history[].triples[].triple` | Object | The Action Triple containing subject, predicate, and object. |
| `history[].triples[].triple.s` | Object | Subject of the Action Triple. |
| `history[].triples[].triple.s.value` | String | The URI string of the Subject. |
| `history[].triples[].triple.s.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.p` | Object | Predicate of the Action Triple. |
| `history[].triples[].triple.p.value` | String | The URI string of the Predicate. |
| `history[].triples[].triple.p.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.o` | Object | Object of the Action Triple. |
| `history[].triples[].triple.o.value` | String | The URI string or the literal value of the Object. |
| `history[].triples[].triple.o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

---

## Change State-Ruler

- Performs all necessary Triple-Store-Actions to change the Ruler of the State whose Name is passed under `state` to the Ruler whose name is passed under `ruler`
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/semantics.rdf.system/history**

#### Endpoint
```
/semantics.rdf.system/changeRuler
```

#### Method
```
POST
```

#### Response Codes

| Code | Meaning |
|------|------|
| `200 OK` | Ruler changed successfully |
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
  "history": [
      {
        "action": "STATE_FOUNDING",
        "timeStamp": "2026-07-14T06:38:49.010883400Z",
        "triples": [
            {
                "action": "Created",
                "triple": {
                    "s": {
                        "value": "http://semantics.rdf.system.data/State_new",
                        "isLiteral": false
                    },
                    "p": {
                        "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                        "isLiteral": false
                    },
                    "o": {
                        "value": "http://semantics.rdf.system.ontology/State",
                        "isLiteral": false
                    }
                }
            }
        ]
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `history` | Array | Wrapper containing the list of events. |
| `history[].action` | String | The event identifier. |
| `history[].timeStamp` | String | The ISO-8601 timestamp when the event occurred. |
| `history[].triples` | Array | A collection of modifications applied to the triple store during this event. |
| `history[].triples[].action` | String | The operation performed with the Triple. |
| `history[].triples[].triple` | Object | The Action Triple containing subject, predicate, and object. |
| `history[].triples[].triple.s` | Object | Subject of the Action Triple. |
| `history[].triples[].triple.s.value` | String | The URI string of the Subject. |
| `history[].triples[].triple.s.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.p` | Object | Predicate of the Action Triple. |
| `history[].triples[].triple.p.value` | String | The URI string of the Predicate. |
| `history[].triples[].triple.p.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.o` | Object | Object of the Action Triple. |
| `history[].triples[].triple.o.value` | String | The URI string or the literal value of the Object. |
| `history[].triples[].triple.o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

---

## Found State

- Performs all necessary Triple-Store-Actions to create the State per Data passed in the Request-Body
- Returns all resulting Triple-Store-Actions including their Action-Type along with their respective Subject, Predicate and Object
- Logs the resulting Triple-Store-Actions to be receivable via a call to **/semantics.rdf.system/history**

#### Endpoint
```
/semantics.rdf.system/foundState
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
  "history": [
      {
        "action": "STATE_FOUNDING",
        "timeStamp": "2026-07-14T06:38:49.010883400Z",
        "triples": [
            {
                "action": "Created",
                "triple": {
                    "s": {
                        "value": "http://semantics.rdf.system.data/State_new",
                        "isLiteral": false
                    },
                    "p": {
                        "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                        "isLiteral": false
                    },
                    "o": {
                        "value": "http://semantics.rdf.system.ontology/State",
                        "isLiteral": false
                    }
                }
            }
        ]
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `history` | Array | Wrapper containing the list of events. |
| `history[].action` | String | The event identifier. |
| `history[].timeStamp` | String | The ISO-8601 timestamp when the event occurred. |
| `history[].triples` | Array | A collection of modifications applied to the triple store during this event. |
| `history[].triples[].action` | String | The operation performed with the Triple. |
| `history[].triples[].triple` | Object | The Action Triple containing subject, predicate, and object. |
| `history[].triples[].triple.s` | Object | Subject of the Action Triple. |
| `history[].triples[].triple.s.value` | String | The URI string of the Subject. |
| `history[].triples[].triple.s.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.p` | Object | Predicate of the Action Triple. |
| `history[].triples[].triple.p.value` | String | The URI string of the Predicate. |
| `history[].triples[].triple.p.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.o` | Object | Object of the Action Triple. |
| `history[].triples[].triple.o.value` | String | The URI string or the literal value of the Object. |
| `history[].triples[].triple.o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

---

## Get State-Action-History

- Returns a complete List of all State-Relevant-Triple-Store-Actions resulting from calls to **/semantics.rdf.system/mediatizate**, **/semantics.rdf.system/changeRuler** and **/semantics.rdf.system/foundState**

#### Endpoint
```
/semantics.rdf.system/history
```

#### Method
```
GET
```

#### Response Body
```json
{
  "history": [
      {
        "action": "STATE_FOUNDING",
        "timeStamp": "2026-07-14T06:38:49.010883400Z",
        "triples": [
            {
                "action": "Created",
                "triple": {
                    "s": {
                        "value": "http://semantics.rdf.system.data/State_new",
                        "isLiteral": false
                    },
                    "p": {
                        "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                        "isLiteral": false
                    },
                    "o": {
                        "value": "http://semantics.rdf.system.ontology/State",
                        "isLiteral": false
                    }
                }
            }
        ]
      }
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `history` | Array | Wrapper containing the list of events. |
| `history[].action` | String | The event identifier. |
| `history[].timeStamp` | String | The ISO-8601 timestamp when the event occurred. |
| `history[].triples` | Array | A collection of modifications applied to the triple store during this event. |
| `history[].triples[].action` | String | The operation performed with the Triple. |
| `history[].triples[].triple` | Object | The Action Triple containing subject, predicate, and object. |
| `history[].triples[].triple.s` | Object | Subject of the Action Triple. |
| `history[].triples[].triple.s.value` | String | The URI string of the Subject. |
| `history[].triples[].triple.s.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.p` | Object | Predicate of the Action Triple. |
| `history[].triples[].triple.p.value` | String | The URI string of the Predicate. |
| `history[].triples[].triple.p.isLiteral` | Boolean | Always `false`. |
| `history[].triples[].triple.o` | Object | Object of the Action Triple. |
| `history[].triples[].triple.o.value` | String | The URI string or the literal value of the Object. |
| `history[].triples[].triple.o.isLiteral` | Boolean | `true` if the object value is a literal; `false` if it is a URI. |

---

[Jump to Top](#api-documentation)