package com.semantics.serialization;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.serialization.TripleStreamSerializer;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SerializationTest {

    TripleStore tripleStore;

    @BeforeEach
    void init() {
        tripleStore = new TripleStore();
        ParserTripleProvider provider = new ParserTripleProvider("src/test/java/com/semantics/serialization//serializationTest.ttl");
        provider.initTriples(tripleStore);
    }

    @Test
    void serialize() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TripleStreamSerializer.serialize(outputStream, tripleStore);
        String turtleResult = outputStream.toString(StandardCharsets.UTF_8);
        System.out.println(turtleResult);
    }
}
