package com.github.jcodevandamme.semantics.rdf.dictionary;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.ArrayList;
import java.util.List;

public class TripleDecoder {

    public static List<Triple> decode(List<Triple> triples, TripleDictionary dict) {
        List<Triple> results = new ArrayList<>();
        for (Triple t : triples) {
            results.add(
                    new Triple(
                            dict.decodeSO((int) t.s()),
                            dict.decodeP((int) t.p()),
                            dict.decodeSO((int) t.o())
                    )
            );
        }
        return results;
    }
}
