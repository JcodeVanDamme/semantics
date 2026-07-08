package com.github.jcodevandamme.semantics.rdf.provider.parser;

import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.graph.Node;

import java.util.List;

public class TripleStreamHandler implements StreamRDF {

    private final List<com.github.jcodevandamme.semantics.rdf.model.Triple> triples;

    public TripleStreamHandler(List<com.github.jcodevandamme.semantics.rdf.model.Triple> triples) {
        this.triples = triples;
    }

    @Override
    public void start() {
        System.out.println("Starting RDF Parsing...");
    }

    @Override
    public void triple(Triple triple) {
        String s = extractNodeString(triple.getSubject());
        String p = extractNodeString(triple.getPredicate());
        String o = extractNodeString(triple.getObject());

        com.github.jcodevandamme.semantics.rdf.model.Triple parsedTriple =
                new com.github.jcodevandamme.semantics.rdf.model.Triple(s, p, o);

        triples.add(parsedTriple);
    }

    private String extractNodeString(Node node) {
        if (node.isURI()) {
            return node.getLocalName();
        } else if (node.isLiteral()) {
            return node.getLiteralLexicalForm();
        } else if (node.isBlank()) {
            return node.getBlankNodeLabel();
        }
        return node.toString();
    }

    @Override
    public void quad(Quad quad) {
        triple(quad.asTriple());
    }

    @Override
    public void base(String base) {

    }

    @Override
    public void prefix(String prefix, String iri) {

    }

    @Override
    public void finish() {
        System.out.println("RDF Parsing Complete.");
    }
}