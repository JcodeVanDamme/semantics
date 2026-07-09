package com.github.jcodevandamme.semantics.rdf.serialization;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;

public class TripleStreamSerializer {

    public static void serialize() {
        StreamRDF writer = StreamRDFWriter.getWriterStream(System.out, Lang.TURTLE);

        writer.prefix("ex", "http://example.org/");

        writer.start();

        Node subject = NodeFactory.createURI("http://example.org/DCC20");
        Node predicate = NodeFactory.createURI("http://example.org/hasTopic");
        Node object = NodeFactory.createLiteralString("Text comp.");

        Triple triple = Triple.create(subject, predicate, object);
        writer.triple(triple);

        writer.finish();
    }
}
