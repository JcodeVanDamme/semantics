package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import org.apache.el.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class TestTripleProvider implements TripleProvider{
    @Override
    public List<Triple> getTriples() {
        ArrayList<Triple> triples = new ArrayList<>();
        triples.add(new Triple("DCC20", "held on", "S. Lake City"));
        triples.add(new Triple("S. Lake City", "capital of", "Utah"));
        triples.add(new Triple("DCC20", "has topic", "Text comp."));
        triples.add(new Triple("DCC20", "has topic", "Video cod."));
        triples.add(new Triple("G. Navarro", "attends", "DCC20"));
        triples.add(new Triple("G. Navarro", "lives in", "Chile"));
        triples.add(new Triple("G. Navarro", "expert in", "Text comp."));
        triples.add(new Triple("T. Gagie", "attends", "DCC20"));
        triples.add(new Triple("T. Gagie", "lives in", "Canada"));
        triples.add(new Triple("T. Gagie", "expert in", "Text comp."));
        triples.add(new Triple("A. Bovik", "attends", "DCC20"));
        triples.add(new Triple("A. Bovik", "lives in", "US"));
        triples.add(new Triple("G. Sullivan", "attends", "DCC20"));
        triples.add(new Triple("G. Sullivan", "lives in", "US"));
        return triples;
    }
}
