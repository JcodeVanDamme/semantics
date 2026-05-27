package com.semantics.rdf.provider;

import com.semantics.rdf.model.Triple;

import java.util.List;

public interface TripleProvider {
    List<Triple> getTriples();
}
