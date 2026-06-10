package com.semantics.rdf.query;

import com.semantics.rdf.dictionary.TripleDictionary;

public class QueryFactory {

    TripleDictionary dict;
    public QueryFactory(TripleDictionary dict) {
        this.dict = dict;
    }
    public Query fromTriple(String s, String p, String o) {
        Integer encodedS = s != null ? dict.encodeSO(s) : null;
        Integer encodedP = p != null ? dict.encodeP(p) : null;
        Integer encodedO = o != null ? dict.encodeSO(o) : null;
        return new TripleQuery(encodedS, encodedP, encodedO);
    }

    public Query fromSparql(String query) {
        return null;
    }
}
