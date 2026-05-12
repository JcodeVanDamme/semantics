package com.example.store;


import com.example.model.Triple;

import java.util.ArrayList;
import java.util.List;

public class TripleStore {

    private final List<Triple> triples = new ArrayList<>();

    public void addTriple(Triple triple) {
        triples.add(triple);
    }

    public List<Triple> getTriples() {
        return triples;
    }

    public void printTriples() {

        System.out.println("\n=== Triple Store ===");

        for (Triple triple : triples) {
            System.out.println(triple);
        }
    }
}
