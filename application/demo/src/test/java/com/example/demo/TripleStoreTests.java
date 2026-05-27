package com.example.demo;
import com.example.demo.tripleStore.TripleStore;
import com.example.demo.tripleStore.triple.TestTripleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TripleStoreTests {

    static TripleStore tripleStore;

    @BeforeAll
    static void init() {
        tripleStore = new TripleStore();
        tripleStore.init(new TestTripleProvider());
    }

    @Test
    public void spoQueryTest() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Text comp.");
        assertTrue(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("DCC20");
        p = tripleStore.dict().encodeP("has topic");
        o = tripleStore.dict().encodeSO("Canada");
        assertFalse(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("G. Navarro");
        p = tripleStore.dict().encodeP("has topic");
        o = tripleStore.dict().encodeSO("Text comp.");
        assertFalse(tripleStore.bMatrix().spo(s, p, o));

        s = tripleStore.dict().encodeSO("DCC20");
        p = tripleStore.dict().encodeP("lives in");
        o = tripleStore.dict().encodeSO("Text comp.");
        assertFalse(tripleStore.bMatrix().spo(s, p, o));
    }

    //
}
