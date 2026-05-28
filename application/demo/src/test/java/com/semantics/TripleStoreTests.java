package com.semantics;

import com.semantics.rdf.model.Triple;
import com.semantics.rdf.provider.TestTripleProvider;
import com.semantics.rdf.query.Query;
import com.semantics.rdf.query.QueryFactory;
import com.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void spoTest() {
        String s = "DCC20";
        String p = "has topic";
        String o1 = "Text comp.";
        String o2 = "Canada";

        List<Triple> res = new ArrayList<>(List.of(
                new Triple(s, p, o1)
        ));

        assertEquals(res, tripleStore.query(s, p, o1));
        assertEquals(List.of(), tripleStore.query(s, p, o2));
    }

    @Test
    public void sptest() {
        String s = "DCC20";
        String p1 = "has topic";
        String p2 = "lives in";
        String o1 = "Text comp.";
        String o2 = "Video cod.";

        List<Triple> res = new ArrayList<>(List.of(
                new Triple(s, p1, o1),
                new Triple(s, p1, o2)
        ));

        assertEquals(res, tripleStore.query(s, p1, null));
        assertEquals(List.of(), tripleStore.query(s, p2, null));
    }

    @Test
    public void poQueryTest() {

        String s1 = "G. Navarro";
        String s2 = "T. Gagie";
        String s3 = "A. Bovik";
        String s4 = "G. Sullivan";
        String p = "attends";
        String o = "DCC20";

        List<Triple> res = new ArrayList<>(List.of(
                new Triple(s1, p, o),
                new Triple(s2, p, o),
                new Triple(s3, p, o),
                new Triple(s4, p, o)
        ));

        assertEquals(res, tripleStore.query(null, p, o));
        assertEquals(List.of(), tripleStore.query(null, p, s1));
    }

    @Test
    public void s_oQueryTest() {

        String s = "G. Navarro";
        String p1 = "attends";
        String o1 = "DCC20";
        String o2 = "US";

        List<Triple> res1 = List.of(
                new Triple(s, p1, o1)
        );

        assertEquals(res1, tripleStore.query(s, null, o1));
        assertEquals(List.of(), tripleStore.query(s, null, o2));
    }

    @Test
    public void s__QueryTest() {

        String s1 = "DCC20";
        String s2 = "US";
        String p1 = "held on";
        String p2 = "has topic";
        String o1 = "S. Lake City";
        String o2 = "Text comp.";
        String o3 = "Video cod.";

        List<Triple> res1 = List.of(
                new Triple(s1, p2, o2),
                new Triple(s1, p2, o3),
                new Triple(s1, p1, o1)
        );

        assertEquals(res1, tripleStore.query(s1, null, null));
        assertEquals(List.of(), tripleStore.query(s2, null, null));
    }

    @Test
    public void __oQueryTest() {

        String s1 = "G. Navarro";
        String s2 = "T. Gagie";
        String s3 = "DCC20";
        String p1 = "expert in";
        String p2 = "has topic";
        String o1 = "Text comp.";

        List<Triple> res1 = List.of(
                new Triple(s1, p1, o1),
                new Triple(s2, p1, o1),
                new Triple(s3, p2, o1)
        );

        assertEquals(res1, tripleStore.query(null, null, o1));
        assertEquals(List.of(), tripleStore.query(null, null, s1));
    }

    @Test
    public void _p_QueryTest() {

        String s1 = "G. Navarro";
        String s2 = "T. Gagie";
        String s3 = "A. Bovik";
        String s4 = "G. Sullivan";
        String p1 = "lives in";
        String o1 = "Chile";
        String o2 = "Canada";
        String o3 = "US";

        List<Triple> res1 = List.of(
                new Triple(s1, p1, o1),
                new Triple(s2, p1, o2),
                new Triple(s4, p1, o3),
                new Triple(s3, p1, o3)
        );

        assertEquals(res1, tripleStore.query(null, p1, null));
    }
}
