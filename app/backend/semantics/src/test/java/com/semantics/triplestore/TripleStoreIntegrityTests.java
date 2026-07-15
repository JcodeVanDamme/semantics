package com.semantics.triplestore;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.StaticTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripleStoreIntegrityTests {

    List<Triple> triples;
    static TripleStore tripleStore;

    @BeforeEach
    void init() {
        tripleStore = new TripleStore();
    }

    @Test
    public void query_afterUpdate_returnsAll() {
        StaticTripleProvider provider = new StaticTripleProvider(
                List.of(
                        new Triple("s1", "p", "o"),
                        new Triple("s2", "p", "o"),
                        new Triple("s3", "p", "o"),
                        new Triple("s4", "p", "o"),
                        new Triple("s5", "p", "o"),
                        new Triple("s6", "p", "o")
                )
        );
        provider.initTriples(tripleStore);

        tripleStore.update(
                new Triple("s3", "p", "o"),
                new Triple("sNew", "p", "o")
        );

        assertEquals(6, tripleStore.query(null, "p", null).size());
    }
}
