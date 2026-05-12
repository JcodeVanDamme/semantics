package com.example.matrix;


import com.example.model.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredicateMatrixBuilder {

    private final List<Triple> triples;

    public PredicateMatrixBuilder(List<Triple> triples) {
        this.triples = triples;
    }

    public Map<Integer, AdjacencyMatrix> buildMatrices() {

        Map<Integer, AdjacencyMatrix> matrices =
                new HashMap<>();

        int maxId = findMaxId();

        for (Triple triple : triples) {

            int predicate = triple.getPredicate();

            AdjacencyMatrix matrix =
                    matrices.computeIfAbsent(
                            predicate,
                            p -> new AdjacencyMatrix(maxId + 1)
                    );

            matrix.set(
                    triple.getSubject(),
                    triple.getObject()
            );
        }

        return matrices;
    }

    private int findMaxId() {

        int max = 0;

        for (Triple t : triples) {

            max = Math.max(max, t.getSubject());
            max = Math.max(max, t.getPredicate());
            max = Math.max(max, t.getObject());
        }

        return max;
    }
}