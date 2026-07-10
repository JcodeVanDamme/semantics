package com.semantics.triplestore;

import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.StaticTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripleStoreOpsTests {

    static TripleStore tripleStore;

    @BeforeEach
    void init() {
        tripleStore = new TripleStore();
        StaticTripleProvider provider = new StaticTripleProvider();
        provider.initTriples(tripleStore);
    }

    @Test
    public void delete_valid_tripleDeleted() {
        Triple t = new Triple("DCC20", "has topic", "Text comp.");
        assertTrue(tripleStore.delete(t));
    }
    @Test
    public void delete_soUnknown_throwsCodingEx() {
        Triple t = new Triple("DCC20", "has topic", "Gabba");
        assertThrows(TripleCodingException.class, () -> tripleStore.delete(t));
    }
    @Test
    public void create_valid_tripleAdded() {
        Triple t = new Triple("DCC20", "has topic", "Gabba");
        assertTrue(tripleStore.create(t));
    }
    @Test
    public void create_invalid_none() {
        Triple t = new Triple("DCC20", "has topic", "Text comp.");
        assertThrows(TripleAlreadyExistsException.class, () -> tripleStore.create(t));
    }
    @Test
    public void update_valid_tripleUpdated() {
        Triple oldT = new Triple("DCC20", "has topic", "Text comp.");
        Triple newT = new Triple("DCC20", "has topic", "Gabba");

        assertTrue(tripleStore.update(oldT, newT));
    }
    @Test
    public void update_soUnknown_throwsCodingEx() {
        Triple oldT = new Triple("DCC20", "has topic", "Gabba");
        Triple newT = new Triple("DCC20", "has topic", "Atzen");
        assertThrows(TripleCodingException.class, () -> tripleStore.update(oldT, newT));
    }
    @Test
    public void update_newExists_throwsAlreadyExistsEx() {
        Triple oldT = new Triple("DCC20", "has topic", "Text comp.");
        Triple newT = new Triple("DCC20", "has topic", "Video cod.");
        assertThrows(TripleAlreadyExistsException.class, () -> tripleStore.update(oldT, newT));
    }
}
