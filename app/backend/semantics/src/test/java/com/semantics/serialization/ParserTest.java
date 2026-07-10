package com.semantics.serialization;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    void initTripleStore_ttl__withParser_init() {
        ParserTripleProvider provider = new ParserTripleProvider("src/test/java/com/semantics/serialization/parseTest.ttl");
        TripleStore store = new TripleStore();
        provider.initTriples(store);

        assertEquals(
                List.of(
                        new Triple("http://example.org/DCC20", "http://example.org/heldOn", "http://example.org/SLakeCity", false),
                        new Triple("http://example.org/DCC20", "http://example.org/hasTopic", "Text comp.", true),
                        new Triple("http://example.org/DCC20", "http://example.org/hasTopic", "Video cod.", true),
                        new Triple("http://example.org/SLakeCity", "http://example.org/capitalOf", "http://example.org/Utah", false),
                        new Triple("http://example.org/GNavarro", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/GNavarro", "http://example.org/livesIn", "http://example.org/Chile", false),
                        new Triple("http://example.org/GNavarro", "http://example.org/expertIn", "Text comp.", true),
                        new Triple("http://example.org/TGagie", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/TGagie", "http://example.org/livesIn", "http://example.org/Canada", false),
                        new Triple("http://example.org/TGagie", "http://example.org/expertIn", "Text comp.", true),
                        new Triple("http://example.org/ABovik", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/ABovik", "http://example.org/livesIn", "http://example.org/US", false),
                        new Triple("http://example.org/GSullivan", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/GSullivan", "http://example.org/livesIn", "http://example.org/US", false)
                ),
                store.query(null, null, null)
        );
    }

    @Test
    void initTripleStore_nt__withParser_init() {
        ParserTripleProvider provider = new ParserTripleProvider("src/test/java/com/semantics/serialization/parseTest.nt");
        TripleStore store = new TripleStore();
        provider.initTriples(store);

        assertEquals(
                List.of(
                        new Triple("http://example.org/DCC20", "http://example.org/heldOn", "http://example.org/SLakeCity", false),
                        new Triple("http://example.org/SLakeCity", "http://example.org/capitalOf", "http://example.org/Utah", false),
                        new Triple("http://example.org/DCC20", "http://example.org/hasTopic", "Text comp.", true),
                        new Triple("http://example.org/DCC20", "http://example.org/hasTopic", "Video cod.", true),
                        new Triple("http://example.org/GNavarro", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/GNavarro", "http://example.org/livesIn", "http://example.org/Chile", false),
                        new Triple("http://example.org/GNavarro", "http://example.org/expertIn", "Text comp.", true),
                        new Triple("http://example.org/TGagie", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/TGagie", "http://example.org/livesIn", "http://example.org/Canada", false),
                        new Triple("http://example.org/TGagie", "http://example.org/expertIn", "Text comp.", true),
                        new Triple("http://example.org/ABovik", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/ABovik", "http://example.org/livesIn", "http://example.org/US", false),
                        new Triple("http://example.org/GSullivan", "http://example.org/attends", "http://example.org/DCC20", false),
                        new Triple("http://example.org/GSullivan", "http://example.org/livesIn", "http://example.org/US", false)
                ),
                store.query(null, null, null)
        );
    }
}
