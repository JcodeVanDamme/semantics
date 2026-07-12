package com.github.jcodevandamme.semantics.rdf.query;

import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;

public class QueryFactory {

    public static TripleQuery fromTriple(String s, String p, String o, TripleDictionary dict) {
        Integer encodedS = s != null ? dict.encodeSO(s) : null;
        Integer encodedP = p != null ? dict.encodeP(p) : null;
        Integer encodedO = o != null ? dict.encodeSO(o) : null;
        return new TripleQuery(encodedS, encodedP, encodedO);
    }
}