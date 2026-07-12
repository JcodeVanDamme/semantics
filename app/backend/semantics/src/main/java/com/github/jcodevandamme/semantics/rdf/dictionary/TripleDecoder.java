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

            System.out.println("Decoded: " + decoded);
            results.add(decoded);
        }
        return results;
    }

    public static List<Map<String, String>> decodeSelectResults(
            List<Map<String, Integer>> encodedBindings, TripleDictionary dict) {

        return encodedBindings.stream()
                .map(binding -> decodeSingleBinding(binding, dict))
                .collect(Collectors.toList());
    }

    private static Map<String, String> decodeSingleBinding(
            Map<String, Integer> encodedBinding, TripleDictionary dict) {

        Map<String, String> decoded = new HashMap<>();
        for (Map.Entry<String, Integer> entry : encodedBinding.entrySet()) {
            // Wichtig: Hier musst du die passende Methode deines Dictionaries nutzen.
            // Falls das Dictionary verschiedene Methoden für P und SO hat,
            // müsste man hier die Variable prüfen.
            // Meist reicht für allgemeine Ergebnisse ein decodeSO() oder get(id).
            String value = dict.decodeSO(entry.getValue());
            decoded.put(entry.getKey(), value);
        }
        return decoded;
    }
}
