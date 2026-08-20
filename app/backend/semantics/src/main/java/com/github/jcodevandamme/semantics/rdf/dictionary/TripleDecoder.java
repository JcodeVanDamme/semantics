package com.github.jcodevandamme.semantics.rdf.dictionary;

import com.github.jcodevandamme.semantics.rdf.bmatrix.QueryResult;
import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;
import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripleDecoder {

    public static List<Triple> decode(List<EncodedTriple> triples, TripleDictionary dict) {
        List<Triple> results = new ArrayList<>();
        for (EncodedTriple t : triples) {

            boolean objectIsLiteral = dict.isLiteral(t.o());

            Triple decoded = new Triple(
                    dict.decodeSO(t.s()),
                    dict.decodeP(t.p()),
                    dict.decodeSO(t.o()),
                    objectIsLiteral
            );

            results.add(decoded);
        }
        return results;
    }
}
