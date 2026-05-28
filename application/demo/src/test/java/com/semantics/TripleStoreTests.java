package com.semantics;

import com.semantics.rdf.model.Triple;
import com.semantics.rdf.provider.TestTripleProvider;
import com.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TripleStoreTests {

    static TripleStore tripleStore;
    static List<Triple> none;

    @BeforeAll
    static void init() {
        tripleStore = new TripleStore();
        tripleStore.init(new TestTripleProvider());
        none = new ArrayList<>();
    }

    @Test
    public void SPOTest() {
        assertEquals(
                List.of(new Triple("DCC20", "has topic", "Text comp.")),
                tripleStore.query("DCC20", "has topic", "Text comp.")
        );

        assertEquals(
                List.of(),
                tripleStore.query("DCC20", "has topic", "Canada")
        );
    }

    @Test
    public void sptest() {

        assertEquals(
                List.of(new Triple("DCC20", "has topic", "Text comp."),
                        new Triple("DCC20", "has topic", "Video cod.")
                ),
                tripleStore.query("DCC20", "has topic", null)
        );

        assertEquals(
                List.of(),
                tripleStore.query("DCC20", "lives in", null)
        );
    }

    @Test
    public void poQueryTest() {

        assertEquals(
                List.of(new Triple("G. Navarro", "attends", "DCC20"),
                        new Triple("T. Gagie", "attends", "DCC20"),
                        new Triple("A. Bovik", "attends", "DCC20"),
                        new Triple("G. Sullivan", "attends", "DCC20")
                ),
                tripleStore.query(null, "attends", "DCC20")
        );

        assertEquals(
                List.of(),
                tripleStore.query(null, "attends", "G. Navarro")
        );
    }

    @Test
    public void s_oQueryTest() {

        assertEquals(
                List.of(new Triple("G. Navarro", "attends", "DCC20")
                ),
                tripleStore.query("G. Navarro", null, "DCC20")
        );

        assertEquals(
                List.of(),
                tripleStore.query("G. Navarro", null, "US")
        );
    }

    @Test
    public void s__QueryTest() {

        assertEquals(
                List.of(new Triple("DCC20", "has topic", "Text comp."),
                        new Triple("DCC20", "has topic", "Video cod."),
                        new Triple("DCC20", "held on", "S. Lake City")
                ),
                tripleStore.query("DCC20", null, null)
        );

        assertEquals(List.of(),
                tripleStore.query("US", null, null)
        );
    }

    @Test
    public void __oQueryTest() {

        assertEquals(
                List.of(new Triple("G. Navarro", "expert in", "Text comp."),
                        new Triple("T. Gagie", "expert in", "Text comp."),
                        new Triple("DCC20", "has topic", "Text comp.")
                ),
                tripleStore.query(null, null, "Text comp.")
        );

        assertEquals(
                List.of(),
                tripleStore.query(null, null, "G. Navarro")
        );
    }

    @Test
    public void _p_QueryTest() {

        assertEquals(
                List.of(
                        new Triple("G. Navarro", "lives in", "Chile"),
                        new Triple("T. Gagie", "lives in", "Canada"),
                        new Triple("G. Sullivan", "lives in", "US"),
                        new Triple("A. Bovik", "lives in", "US")
                ),
                tripleStore.query(null, "lives in", null)
        );
    }
}
