package com.semantics.triplestore;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.parser.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    void initTripleStore_withParser_init() {
        ParserTripleProvider provider = new ParserTripleProvider("src/main/java/com/github/jcodevandamme/semantics/rdf/data.ttl");
        TripleStore store = new TripleStore();
        provider.initTriples(store);

        assertEquals(
                List.of(
                        new Triple("DCC20", "heldOn", "SLakeCity"),
                        new Triple("DCC20", "hasTopic", "Text comp."),
                        new Triple("DCC20", "hasTopic", "Video cod."),
                        new Triple("SLakeCity", "capitalOf", "Utah"),
                        new Triple("GNavarro", "attends", "DCC20"),
                        new Triple("GNavarro", "livesIn", "Chile"),
                        new Triple("GNavarro", "expertIn", "Text comp."),
                        new Triple("TGagie", "attends", "DCC20"),
                        new Triple("TGagie", "livesIn", "Canada"),
                        new Triple("TGagie", "expertIn", "Text comp."),
                        new Triple("ABovik", "attends", "DCC20"),
                        new Triple("ABovik", "livesIn", "US"),
                        new Triple("GSullivan", "attends", "DCC20"),
                        new Triple("GSullivan", "livesIn", "US")
                ),
                store.query(null, null, null)
        );
    }
}
