package com.semantics.triplestore;

import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripleStoreEmptyTest {

    static TripleStore tripleStore;

    @BeforeEach
    void init() {
        tripleStore = new TripleStore();
    }

    @Test
    public void query_any_returnsEmpty() {
        assertEquals(Collections.emptyList(), tripleStore.query("Gabba", "und", "Atzen"));
    }

    @Test
    public void query_all_returnsEmptyList() {
        assertEquals(
                Collections.emptyList(),
                tripleStore.query(null, null, null)
        );
    }

    @Test
    public void add_valid_noRootExpansion_TripleAdded() {
        assertTrue(tripleStore.create(new Triple("Hacke", "Racke", "Gabba und Atzen")));
        assertEquals(
                List.of(new Triple("Hacke", "Racke", "Gabba und Atzen")),
                tripleStore.query("Hacke", "Racke", "Gabba und Atzen")
        );
    }


    @Test
    public void add_valid_RootExpansion_TripleAdded() {
        assertTrue(tripleStore.create(new Triple("1", "1", "1")));
        assertTrue(tripleStore.create(new Triple("2", "2", "2")));
        assertTrue(tripleStore.create(new Triple("3", "3", "3")));
        assertTrue(tripleStore.create(new Triple("4", "4", "4")));

        assertEquals(
                List.of(new Triple("1", "1", "1")),
                tripleStore.query("1", "1", "1")
        );
        assertEquals(
                List.of(new Triple("2", "2", "2")),
                tripleStore.query("2", "2", "2")
        );
        assertEquals(
                List.of(new Triple("3", "3", "3")),
                tripleStore.query("3", "3", "3")
        );
        assertEquals(
                List.of(new Triple("4", "4", "4")),
                tripleStore.query("4", "4", "4")
        );
    }
}
