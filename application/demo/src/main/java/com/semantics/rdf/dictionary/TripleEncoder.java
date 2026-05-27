package com.semantics.rdf.dictionary;

import com.semantics.rdf.model.Triple;
import com.semantics.rdf.provider.TripleProvider;

import java.util.*;

public class TripleEncoder {
    public static List<Triple> encode(TripleProvider provider, TripleDictionary dict) {
        List<Triple> triples = provider.getTriples();
        registerTriples(triples, dict);
        return encodeTriples(triples, dict);
    }

    private static void registerTriples(List<Triple> triples, TripleDictionary dict) {
        Set<String> subjects = new HashSet<>();
        Set<String> objects = new HashSet<>();
        Set<String> predicates = new HashSet<>();

        // Collect unique Strings according to Triple-Type
        for (Triple t : triples) {
            subjects.add((String) t.s());
            objects.add((String) t.o());
            predicates.add((String) t.p());
        }

        // Subject-Objects need to be remembered to prevent multiple Inclusions
        Set<String> added = new HashSet<>();

        // SO
        for (String s : subjects) {
            if (objects.contains(s)) {
                added.add(s);
                dict.registerSO(s);
            }
        }
        // S
        for (String s : subjects) {
            if (!added.contains(s)) {
                dict.registerSO(s);
            }
        }
        // O
        for (String o : objects) {
            if (!added.contains(o)) {
                dict.registerSO(o);
            }
        }
        // P
        for (String p : predicates) {
            dict.registerP(p);
        }
    }

    private static List<Triple> encodeTriples(List<Triple> triples, TripleDictionary dict) {
        List<Triple> encodedTriples = new ArrayList<>();

        // Encode Triples by looking up a Strings respective ID using the Value
        for (Triple t : triples) {
            encodedTriples.add(
                    new Triple(
                            dict.encodeSO((String) t.s()),
                            dict.encodeP((String) t.p()),
                            dict.encodeSO((String) t.o())
                    )
            );
        }
        // Sorting by the Predicate ID groups the encoded Triples along their Predicates
        encodedTriples.sort(
                Comparator.comparingInt(t -> (Integer) t.p())
        );

        return encodedTriples;
    }
}
