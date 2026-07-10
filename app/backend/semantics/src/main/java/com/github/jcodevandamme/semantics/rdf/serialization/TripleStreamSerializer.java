package com.github.jcodevandamme.semantics.rdf.serialization;

import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TripleStreamSerializer {

    public static void serialize(String filePath, TripleStore tripleStore) throws IOException {
        Path path = Paths.get(filePath);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        System.out.println("Starting Serialization to file: " + filePath);
        try (OutputStream out = Files.newOutputStream(path)) {
            serialize(out, tripleStore);
        }
        System.out.println("Serialization finished.");
    }

    public static void serialize(OutputStream out, TripleStore tripleStore) {
        StreamRDF writer = StreamRDFWriter.getWriterStream(out, Lang.TURTLE);

        writer.start();
        for (com.github.jcodevandamme.semantics.rdf.model.Triple t : tripleStore.query(null, null, null)) {

            Node s = NodeFactory.createURI((String) t.s().value());
            Node p = NodeFactory.createURI((String) t.p().value());
            Node o;
            if (t.o().isLiteral()) {
                o = NodeFactory.createLiteralString((String) t.o().value());
            } else {
                o = NodeFactory.createURI((String) t.o().value());
            }

            Triple jenaTriple = Triple.create(s, p, o);
            writer.triple(jenaTriple);
        }
        writer.finish();
    }

    public static boolean isLiteral(String node) {
        return !node.startsWith("http");
    }
}