package com.github.jcodevandamme.semantics.app.persistence;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

public class LogParser {

    public static Triple parseTriple(String line) {
        String[] tokens = line.split(",");
        String s = tokens[0];
        String p = tokens[1];
        String o = tokens[2];
        boolean isLiteral = Boolean.parseBoolean(tokens[3]);
        return new Triple(s, p, o, isLiteral);
    }
}
