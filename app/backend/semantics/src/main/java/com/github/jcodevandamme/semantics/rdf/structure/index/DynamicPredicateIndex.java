package com.github.jcodevandamme.semantics.rdf.structure.index;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.*;

public class DynamicPredicateIndex {

    Map<Integer, List<Integer>> tripleIndexesByPredicateId;
    Map<Integer, Integer> predicateByTripleId;

    public DynamicPredicateIndex(List<Triple> triples) {
        initializeTripleIndexesByPredicateId(triples);
        initializePredicatesByTripleId(triples);
    }

    private void initializeTripleIndexesByPredicateId(List<Triple> triples) {
        tripleIndexesByPredicateId = new HashMap<>();
        for (int i = 0; i < triples.size(); i++) {
            int pId = (int) triples.get(i).p();

            tripleIndexesByPredicateId
                    .computeIfAbsent(pId, k -> new ArrayList<>())
                    .add(i);
        }
    }
    private void initializePredicatesByTripleId(List<Triple> triples) {
        predicateByTripleId = new HashMap<>();
        for (int i = 0; i < triples.size(); i++) {
            int pId = (int) triples.get(i).p();

            predicateByTripleId.put(i, pId);
        }
    }

    // -> Given Triple with Index i; what Predicate belongs to it ?
    // Approximate the Predicate Range using sampled rankP

    public int rank1(int i) {
        return predicateByTripleId.get(i);
    }

    // -> Where does Predicate with ID j  begin in the Triple List ?
    // -> Which Columns in ST/OT / Triples belong to that predicate
    public List<Integer> select1(int j) {
        return tripleIndexesByPredicateId.get(j);
    }
}
