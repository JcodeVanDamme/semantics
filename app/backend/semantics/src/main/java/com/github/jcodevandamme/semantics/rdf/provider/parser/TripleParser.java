package com.github.jcodevandamme.semantics.rdf.provider.parser;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TripleParser {

    public static List<Triple> loadRdfFile(String filePath) {
        List<Triple> triples = new ArrayList<>();
        TripleStreamHandler handler = new TripleStreamHandler(triples);

        try {
            RDFParser.create()
                    .source(filePath)
                    .checking(true)
                    .parse(handler);

            return triples;

        } catch (RiotException ex) {
            System.err.println("RDF Parse Error: " + ex.getMessage());
            return Collections.emptyList();

        } catch (Exception ex) {
            System.err.println("File System Error: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}
