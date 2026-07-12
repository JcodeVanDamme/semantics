package com.github.jcodevandamme.semantics.rdf.query.sparql;

import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;

import java.util.List;
import java.util.Map;

public class SparqlResult {
    private List<Map<String, Integer>> resultSet; // Für SELECT
    private List<EncodedTriple> tripleResult;     // Für CONSTRUCT / DESCRIBE
    private final SparqlQuery.Type type;

    public SparqlResult(List<Map<String, Integer>> resultSet, SparqlQuery.Type type) {
        this.resultSet = resultSet;
        this.type = type;
    }

    public SparqlResult(List<EncodedTriple> tripleResult, SparqlQuery.Type type, boolean isGraph) {
        this.tripleResult = tripleResult;
        this.type = type;
    }

    public boolean isResultSet() { return type == SparqlQuery.Type.SELECT; }
    public List<Map<String, Integer>> getResultSet() { return resultSet; }
    public List<EncodedTriple> getTripleResult() { return tripleResult; }
}
