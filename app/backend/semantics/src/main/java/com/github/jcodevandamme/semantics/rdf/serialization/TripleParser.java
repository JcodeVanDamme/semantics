package com.github.jcodevandamme.semantics.rdf.serialization;

import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;


public class TripleParser {

    public static void loadRdfFile(String filePath, TripleStore tripleStore) {
        TripleStreamParser handler = new TripleStreamParser(tripleStore);

        try {
            RDFParser.create()
                    .source(filePath)
                    .checking(true)
                    .parse(handler);

        } catch (RiotException ex) {
            System.err.println("RDF Parse Error: " + ex.getMessage());

        } catch (Exception ex) {
            System.err.println("File System Error: " + ex.getMessage());
        }
    }
}
