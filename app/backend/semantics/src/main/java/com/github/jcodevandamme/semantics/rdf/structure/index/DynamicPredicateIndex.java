package com.github.jcodevandamme.semantics.rdf.structure.index;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.*;

public class DynamicPredicateIndex {

    Map<Integer, List<Integer>> tripleIndexesByPredicateId;
    Map<Integer, Integer> predicateByTripleId;

    public DynamicPredicateIndex(List<Triple> triples) {
        tripleIndexesByPredicateId = new HashMap<>();
        predicateByTripleId = new HashMap<>();

        for (int tripleIdx = 0; tripleIdx < triples.size(); tripleIdx++) {
            int pId = (int) triples.get(tripleIdx).p();
            registerTriple(tripleIdx,  pId);
        }
    }
    public DynamicPredicateIndex() {
        tripleIndexesByPredicateId = new HashMap<>();
        predicateByTripleId = new HashMap<>();
    }

    public void registerTriple(int tripleIdx, int pId) {
        tripleIndexesByPredicateId
                .computeIfAbsent(pId, k -> new ArrayList<>())
                .add(tripleIdx);
        predicateByTripleId.put(tripleIdx, pId);
    }

    public void deregisterTriple(int tripleIdx, int pId) {
        List<Integer> cols = tripleIndexesByPredicateId.get(pId);
        if (cols != null) {
            cols.remove((Integer) tripleIdx);
            if (cols.isEmpty()) {
                tripleIndexesByPredicateId.remove(pId);
            }
        }
        predicateByTripleId.remove(tripleIdx);
    }

    // -> Given Triple with Index i; what Predicate belongs to it ?
    // Approximate the Predicate Range using sampled rankP

    public int rank1(int i) {
        return predicateByTripleId.get(i);
    }

    // -> Where does Predicate with ID j  begin in the Triple List ?
    // -> Which Columns in ST/OT / Triples belong to that predicate
    public List<Integer> select1(int j) {
        List<Integer> cols = tripleIndexesByPredicateId.get(j);
        return cols != null ? cols : Collections.emptyList();
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb
            .append("------------------------- ")
            .append("Index")
            .append(" ---------------------------\n");

        strb.append("\nPredicate - {Triples}:\n");
        for (Map.Entry<Integer, List<Integer>> e : tripleIndexesByPredicateId.entrySet()) {
            strb
                .append(e.getKey())
                .append(" - ")
                .append("{");

            for (Integer t : e.getValue()) {
                strb
                    .append(t)
                    .append(",");
            }
            strb.append("}\n");
        }
        strb.append("\nTriple - Predicate:\n");
        for (Map.Entry<Integer, Integer> e : predicateByTripleId.entrySet()) {
            strb
                    .append(e.getKey())
                    .append(" - ")
                    .append(e.getValue())
                    .append("\n");
        }
        return strb.toString();
    }
}
