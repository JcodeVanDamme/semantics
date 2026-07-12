# About

Implementation of a compact RDF store based on the BMatrix/k2-tree approach proposed by Nieves R. Brisaboa et al. in their 2020 Paper [*Revisiting compact RDF stores based on k2-trees*](https://arxiv.org/abs/2002.11622).
Developed in Java with a Spring Boot backend and Vue.js frontend.
The accompanying web application models and explores the mediatization and territorial restructuring of the german territories during the early 19th century.


# API Documentation

## Domain-Agnostic-Endpoints (CRUD)

- [**POST** `/triples`](#create-triple) - Create a new triple

- [**GET** `/triples`](#read-triples-triple-query) - Query triples with optional filters (`s`, `p`, `o`)

- [**POST** `/triples/sparql`](#read-triples-sparql-query) - Query triples using SPARQL

- [**PUT** `/triples`](#update-triple) - Update an existing triple

- [**DELETE** `/triples`](#delete-triple) - Delete a triple

## Domain-Specific-Endpoints

- [**GET** `/ops/activeStates`](#get-active-states) - Retrieve the number of active States

- [**GET** `/ops/stateChanges`](#get-state-change-factor) - Retrieve the difference of active States and original States

- [**GET** `/ops/states`](#get-state-data) - Retrieve relevant Information of all active States

- [**POST** `/ops/mediatizate`](#mediatizate-state) - Mediatizate a State into another one

- [**POST** `/ops/changeRuler`](#change-state-ruler) - Change the Ruler of a State

- [**POST** `/ops/foundState`](#found-state) - Found a new State

- [**GET** `/ops/history`](#get-state-action-history) - Retrieve the History of State-Relevant-Actions


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
| `400 Bad Request` | Invalid query parameters |
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
| `count` | integer | Number of returned triples |
| `triples` | array | List of matching triples |
| `triples[].s` | string | Subject |
| `triples[].p` | string | Predicate |
| `triples[].o` | string | Object |

---

# Read Triples (SPARQL-Query)

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
| `count` | integer | Number of returned triples |
| `triples` | array | List of matching triples |
| `triples[].s` | string | Subject |
| `triples[].p` | string | Predicate |
| `triples[].o` | string | Object |

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

## Get active States

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

## Get State-Change-Factor

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

## Get State-Data

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
        ]
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

## Mediatizate State

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
  "count": 1,
  "history": [
    {
      "action": "update",
      "triples": [
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Anhalt", "isLiteral": false },
          "type": "deleted"
        },
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Saxony", "isLiteral": false },
          "type": "added"
        }
      ]
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `count` | integer | Number of logged Triple-Store-Actions |
| `history` | array | List of logged Triple-Store-Actions |
| `history[].action` | string | Type of Triple-Store-Action (e.g., `"update"`) |
| `history[].triples` | array | List of Triples modified by this action |
| `history[].triples[].s` | object | Subject of the Action Triple|
| `history[].triples[].s.value` | string | The Value of the Subject |
| `history[].triples[].p` | object | Predicate of the Action Triple |
| `history[].triples[].p.value` | string | The Value of the Predicate |
| `history[].triples[].o` | object | Object of the Action Triple |
| `history[].triples[].o.value` | string | TThe Value of the Object (can be a URI or a literal text/number) |
| `history[].triples[].o.isLiteral` | boolean | `true` if the object value is a literal; `false` if it is a URI|
| `history[].triples[].type` | string | Mutation tracking direction: `"added"` or `"deleted"` |

---

## Change State-Ruler

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
  "count": 1,
  "history": [
    {
      "action": "update",
      "triples": [
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Anhalt", "isLiteral": false },
          "type": "deleted"
        },
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Saxony", "isLiteral": false },
          "type": "added"
        }
      ]
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `count` | integer | Number of logged Triple-Store-Actions |
| `history` | array | List of logged Triple-Store-Actions |
| `history[].action` | string | Type of Triple-Store-Action (e.g., `"update"`) |
| `history[].triples` | array | List of Triples modified by this action |
| `history[].triples[].s` | object | Subject of the Action Triple|
| `history[].triples[].s.value` | string | The Value of the Subject |
| `history[].triples[].p` | object | Predicate of the Action Triple |
| `history[].triples[].p.value` | string | The Value of the Predicate |
| `history[].triples[].o` | object | Object of the Action Triple |
| `history[].triples[].o.value` | string | TThe Value of the Object (can be a URI or a literal text/number) |
| `history[].triples[].o.isLiteral` | boolean | `true` if the object value is a literal; `false` if it is a URI|
| `history[].triples[].type` | string | Mutation tracking direction: `"added"` or `"deleted"` |

---

## Found State

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
  "count": 1,
  "history": [
    {
      "action": "update",
      "triples": [
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Anhalt", "isLiteral": false },
          "type": "deleted"
        },
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Saxony", "isLiteral": false },
          "type": "added"
        }
      ]
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `count` | integer | Number of logged Triple-Store-Actions |
| `history` | array | List of logged Triple-Store-Actions |
| `history[].action` | string | Type of Triple-Store-Action (e.g., `"update"`) |
| `history[].triples` | array | List of Triples modified by this action |
| `history[].triples[].s` | object | Subject of the Action Triple|
| `history[].triples[].s.value` | string | The Value of the Subject |
| `history[].triples[].p` | object | Predicate of the Action Triple |
| `history[].triples[].p.value` | string | The Value of the Predicate |
| `history[].triples[].o` | object | Object of the Action Triple |
| `history[].triples[].o.value` | string | TThe Value of the Object (can be a URI or a literal text/number) |
| `history[].triples[].o.isLiteral` | boolean | `true` if the object value is a literal; `false` if it is a URI|
| `history[].triples[].type` | string | Mutation tracking direction: `"added"` or `"deleted"` |

---

## Get State-Action-History

- Returns a complete List of all State-Relevant-Triple-Store-Actions resulting from calls to **/ops/mediatizate**, **/ops/changeRuler** and **/ops/foundState**

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
  "count": 1,
  "history": [
    {
      "action": "update",
      "triples": [
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Anhalt", "isLiteral": false },
          "type": "deleted"
        },
        {
          "s": { "value": "gabba" },
          "p": { "value": "rules" },
          "o": { "value": "Saxony", "isLiteral": false },
          "type": "added"
        }
      ]
    }
  ]
}
```

#### Response Schema

| Field | Type | Description |
| :--- | :--- | :--- |
| `count` | integer | Number of logged Triple-Store-Actions |
| `history` | array | List of logged Triple-Store-Actions |
| `history[].action` | string | Type of Triple-Store-Action (e.g., `"update"`) |
| `history[].triples` | array | List of Triples modified by this action |
| `history[].triples[].s` | object | Subject of the Action Triple|
| `history[].triples[].s.value` | string | The Value of the Subject |
| `history[].triples[].p` | object | Predicate of the Action Triple |
| `history[].triples[].p.value` | string | The Value of the Predicate |
| `history[].triples[].o` | object | Object of the Action Triple |
| `history[].triples[].o.value` | string | TThe Value of the Object (can be a URI or a literal text/number) |
| `history[].triples[].o.isLiteral` | boolean | `true` if the object value is a literal; `false` if it is a URI|
| `history[].triples[].type` | string | Mutation tracking direction: `"added"` or `"deleted"` |

---

[Jump to Top](#api-documentation)