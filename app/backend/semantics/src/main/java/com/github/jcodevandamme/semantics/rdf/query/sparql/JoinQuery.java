package com.github.jcodevandamme.semantics.rdf.query.sparql;

import com.github.jcodevandamme.semantics.rdf.query.Query;

import java.util.List;

public class JoinQuery implements Query {
    private final List<ParsedPattern> patterns;

    public JoinQuery(List<ParsedPattern> patterns) {
        this.patterns = patterns;
    }

    public List<ParsedPattern> getPatterns() {
        return patterns;
    }

    @Override
    public Integer s() { return null; }

    @Override
    public Integer p() { return null; }

    @Override
    public Integer o() { return null; }
}