package com.github.jcodevandamme.semantics.rdf.query.sparql;

import java.util.List;

public class SparqlQuery {
    public enum Type { SELECT, CONSTRUCT, DESCRIBE }

    private final Type type;
    private final JoinQuery whereClause;

    private List<String> selectVariables;
    private List<ParsedPattern> convertTemplates;
    private List<String> describeVariables;

    public Type getType() { return type; }
    public JoinQuery getWhereClause() { return whereClause; }
    public List<String> getSelectVariables() { return selectVariables; }
    public List<ParsedPattern> getConvertTemplates() { return convertTemplates; }
    public List<String> getDescribeVariables() { return describeVariables; }

    // SELECT
    public SparqlQuery(JoinQuery whereClause, List<String> selectVariables) {
        this.type = Type.SELECT;
        this.whereClause = whereClause;
        this.selectVariables = selectVariables;
    }

    // CONSTRUCT
    public SparqlQuery(JoinQuery whereClause, List<ParsedPattern> convertTemplates, boolean isConstruct) {
        this.type = Type.CONSTRUCT;
        this.whereClause = whereClause;
        this.convertTemplates = convertTemplates;
    }

    // DESCRIBE
    public SparqlQuery(List<String> describeVariables, JoinQuery whereClause) {
        this.type = Type.DESCRIBE;
        this.whereClause = whereClause;
        this.describeVariables = describeVariables;
    }
}