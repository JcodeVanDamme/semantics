package com.semantics.bmatrix.staticbmatrix;
import com.github.jcodevandamme.semantics.rdf.tripleStore.StaticTripleStore;
import com.github.jcodevandamme.semantics.rdf.provider.TestTripleProvider;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StaticBMatrixQueryTests {

    static StaticTripleStore tripleStore;
    static List<Triple> none;

    @BeforeAll
    static void init() {
        tripleStore = new StaticTripleStore();
        tripleStore.init(new TestTripleProvider());
        none = new ArrayList<>();
    }

    @Test
    public void spoQueryTest() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Text comp.");

        // (DCC20, has topic, Text comp.)
        assertTrue(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("DCC20");
        p = tripleStore.dict().encodeP("has topic");
        o = tripleStore.dict().encodeSO("Canada");

        // (DCC20, has topic, Canada)
        assertFalse(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("G. Navarro");
        p = tripleStore.dict().encodeP("has topic");
        o = tripleStore.dict().encodeSO("Text comp.");

        // (G. Navarro, has topic, Text comp.)
        assertFalse(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("DCC20");
        p = tripleStore.dict().encodeP("lives in");
        o = tripleStore.dict().encodeSO("Text comp.");

        // (DCC20, lives in, Text comp.)
        assertFalse(tripleStore.bMatrix().spo(s, p, o));
    }

    @Test
    public void sp_QueryTest() {

        int s = tripleStore.dict().encodeSO("DCC20");
        int p1 = tripleStore.dict().encodeP("has topic");
        int p2 = tripleStore.dict().encodeP("lives in");
        int o1 = tripleStore.dict().encodeSO("Text comp.");
        int o2 = tripleStore.dict().encodeSO("Video cod.");

        List<Triple> results1 = new ArrayList<>();

        // (DCC20, has topic, Text comp.)
        results1.add(new Triple(s, p1, o1));

        // (DCC20, has topic, Video cod.)
        results1.add(new Triple(s, p1, o2));

        // (DCC20, has topic, ?)
        assertEquals(results1, tripleStore.bMatrix().sp_(s, p1));

        // (DCC20, lives in, ?)
        assertEquals(none, tripleStore.bMatrix().sp_(s, p2));
    }

    @Test
    public void _poQueryTest() {

        int s1 = tripleStore.dict().encodeSO("G. Navarro");
        int s2 = tripleStore.dict().encodeSO("T. Gagie");
        int s3 = tripleStore.dict().encodeSO("A. Bovik");
        int s4 = tripleStore.dict().encodeSO("G. Sullivan");
        int p = tripleStore.dict().encodeP("attends");
        int o = tripleStore.dict().encodeSO("DCC20");

        List<Triple> results1 = new ArrayList<>();

        // (G. Navarro, attends, DCC20)
        results1.add(new Triple(s1, p, o));

        // (T. Gagie, attends, DCC20)
        results1.add(new Triple(s2, p, o));

        // (A. Bovik, attends, DCC20)
        results1.add(new Triple(s3, p, o));

        // (G. Sullivan, attends, DCC20)
        results1.add(new Triple(s4, p, o));

        // (?, attends, DCC20)
        assertEquals(results1, tripleStore.bMatrix()._po(p, o));

        // (?, attends, G. Navarro)
        assertEquals(none, tripleStore.bMatrix()._po(p, s1));
    }

    @Test
    public void s_oQueryTest() {

        int s = tripleStore.dict().encodeSO("G. Navarro");
        int p1 = tripleStore.dict().encodeP("attends");
        int o1 = tripleStore.dict().encodeSO("DCC20");
        int o2 = tripleStore.dict().encodeSO("US");

        List<Triple> results1 = new ArrayList<>();

        // (G. Navarro, attends, DCC20)
        results1.add(new Triple(s, p1, o1));

        // (G. Navarro, ?, DCC20)
        assertEquals(results1, tripleStore.bMatrix().s_o(s, o1));

        // (G. Navarro, ?, US)
        assertEquals(none, tripleStore.bMatrix().s_o(s, o2));
    }

    @Test
    public void s__QueryTest() {

        int s1 = tripleStore.dict().encodeSO("DCC20");
        int s2 = tripleStore.dict().encodeSO("US");
        int p1 = tripleStore.dict().encodeP("held on");
        int p2 = tripleStore.dict().encodeP("has topic");
        int o1 = tripleStore.dict().encodeSO("S. Lake City");
        int o2 = tripleStore.dict().encodeSO("Text comp.");
        int o3 = tripleStore.dict().encodeSO("Video cod.");

        List<Triple> results1 = new ArrayList<>();

        // (DCC20, has topic, S. Text comp.)
        results1.add(new Triple(s1, p2, o2));

        // (DCC20, has topic, S. Video cod.)
        results1.add(new Triple(s1, p2, o3));

        // (DCC20, held on, S. Lake City)
        results1.add(new Triple(s1, p1, o1));

        // (DCC20. ?, ?)
        assertEquals(results1, tripleStore.bMatrix().s__(s1));

        // (US, ?, ?)
        assertEquals(none, tripleStore.bMatrix().s__(s2));
    }

    @Test
    public void __oQueryTest() {

        int s1 = tripleStore.dict().encodeSO("G. Navarro");
        int s2 = tripleStore.dict().encodeSO("T. Gagie");
        int s3 = tripleStore.dict().encodeSO("DCC20");
        int p1 = tripleStore.dict().encodeP("expert in");
        int p2 = tripleStore.dict().encodeP("has topic");
        int o1 = tripleStore.dict().encodeSO("Text comp.");

        List<Triple> results1 = new ArrayList<>();

        // (G. Navarro, expert in, Text comp.)
        results1.add(new Triple(s1, p1, o1));

        // (G. Navarro, expert in, Text comp.)
        results1.add(new Triple(s2, p1, o1));

        // (DCC20, has topic, Text comp.)
        results1.add(new Triple(s3, p2, o1));

        // (?. ?, Text comp.)
        assertEquals(results1, tripleStore.bMatrix().__o(o1));

        // (?, ?, G. Navarro)
        assertEquals(none, tripleStore.bMatrix().__o(s1));
    }

    @Test
    public void _p_QueryTest() {

        int s1 = tripleStore.dict().encodeSO("G. Navarro");
        int s2 = tripleStore.dict().encodeSO("T. Gagie");
        int s3 = tripleStore.dict().encodeSO("A. Bovik");
        int s4 = tripleStore.dict().encodeSO("G. Sullivan");
        int p1 = tripleStore.dict().encodeP("lives in");
        int o1 = tripleStore.dict().encodeSO("Chile");
        int o2 = tripleStore.dict().encodeSO("Canada");
        int o3 = tripleStore.dict().encodeSO("US");


        List<Triple> results1 = new ArrayList<>();

        // (G. Navarro, lives in, Chile)
        results1.add(new Triple(s1, p1, o1));

        // (T. Gagie, lives in, Canada)
        results1.add(new Triple(s2, p1, o2));

        // (G. Sullivan, lives in, US)
        results1.add(new Triple(s4, p1, o3));

        // (A. Bovik, lives in, US)
        results1.add(new Triple(s3, p1, o3));

        // (?. lives in, ?)
        assertEquals(results1, tripleStore.bMatrix()._p_(p1));
    }
}
