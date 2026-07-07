package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserTripleProvider implements TripleProvider {

    String PATH = "src/main/java/com/github/jcodevandamme/semantics/rdf/data.ttl";

    @Override
    public List<Triple> getTriples() {
        return loadRdfFile();
    }

    public List<Triple> loadRdfFile() {
        List<Triple> triples = new ArrayList<>();
        TripleStreamHandler handler = new TripleStreamHandler(triples);

        try {
            RDFParser.create()
                    .source(PATH)
                    .checking(true)
                    .parse(handler);

            return triples;

        } catch (RiotException ex) {
            System.err.println("RDF Parse Error (Jena syntax issue): " + ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            System.err.println("File System Error (File not found at path): " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}