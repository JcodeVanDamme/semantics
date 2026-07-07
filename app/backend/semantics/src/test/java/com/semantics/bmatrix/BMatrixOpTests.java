package com.semantics.bmatrix;

import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.TestTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BMatrixOpTests {

    static TripleStore tripleStore;
    static List<Triple> none;

    @BeforeEach
    void init() {
        tripleStore = new TripleStore();
        tripleStore.init(new TestTripleProvider());
        none = new ArrayList<>();
    }

    public void assertAll() {
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("DCC20"), tripleStore.dict().encodeP("held on"), tripleStore.dict().encodeSO("S. Lake City")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("S. Lake City"), tripleStore.dict().encodeP("capital of"), tripleStore.dict().encodeSO("Utah")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("DCC20"), tripleStore.dict().encodeP("has topic"), tripleStore.dict().encodeSO("Text comp.")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("DCC20"), tripleStore.dict().encodeP("has topic"), tripleStore.dict().encodeSO("Video cod.")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("G. Navarro"), tripleStore.dict().encodeP("attends"), tripleStore.dict().encodeSO("DCC20")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("G. Navarro"), tripleStore.dict().encodeP("lives in"), tripleStore.dict().encodeSO("Chile")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("G. Navarro"), tripleStore.dict().encodeP("expert in"), tripleStore.dict().encodeSO("Text comp.")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("T. Gagie"), tripleStore.dict().encodeP("attends"), tripleStore.dict().encodeSO("DCC20")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("T. Gagie"), tripleStore.dict().encodeP("lives in"), tripleStore.dict().encodeSO("Canada")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("T. Gagie"), tripleStore.dict().encodeP("expert in"), tripleStore.dict().encodeSO("Text comp.")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("A. Bovik"), tripleStore.dict().encodeP("attends"), tripleStore.dict().encodeSO("DCC20")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("A. Bovik"), tripleStore.dict().encodeP("lives in"), tripleStore.dict().encodeSO("US")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("G. Sullivan"), tripleStore.dict().encodeP("attends"), tripleStore.dict().encodeSO("DCC20")));
        assertTrue(tripleStore.bMatrix().spoQuery(tripleStore.dict().encodeSO("G. Sullivan"), tripleStore.dict().encodeP("lives in"), tripleStore.dict().encodeSO("US")));
    }

    @Test
    public void delete_valid_tripleRemoved() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Text comp.");

        assertTrue(tripleStore.bMatrix().spoQuery(s, p, o));
        System.out.println(tripleStore.bMatrix());
        assertTrue(tripleStore.bMatrix().delete(s, p, o));
        System.out.println(tripleStore.bMatrix());
        assertFalse(tripleStore.bMatrix().spoQuery(s, p, o));
    }

    @Test
    public void delete_invalid_throws() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Chile");

        assertThrows(TripleNotFoundException.class, () -> tripleStore.bMatrix().delete(s, p, o));
    }

    @Test
    public void add_valid_noRootExpansion_tripleAdded() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Chile");

        assertFalse(tripleStore.bMatrix().spoQuery(s, p, o));
        System.out.println(tripleStore.bMatrix());
        assertTrue(tripleStore.bMatrix().add(s, p, o));
        System.out.println(tripleStore.bMatrix());
        assertTrue(tripleStore.bMatrix().spoQuery(s, p, o));
    }

    @Test
    public void add_valid_rootExpansion_triplesAdded() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o1 = tripleStore.dict().encodeSO("Chile");
        int o2 = tripleStore.dict().encodeSO("Canada");
        int o3 = tripleStore.dict().encodeSO("US");

        assertFalse(tripleStore.bMatrix().spoQuery(s, p, o1));
        assertFalse(tripleStore.bMatrix().spoQuery(s, p, o2));
        assertFalse(tripleStore.bMatrix().spoQuery(s, p, o3));

        assertTrue(tripleStore.bMatrix().add(s, p, o1));
        assertTrue(tripleStore.bMatrix().add(s, p, o2));
        assertTrue(tripleStore.bMatrix().add(s, p, o3));

        assertTrue(tripleStore.bMatrix().spoQuery(s, p, o1));
        assertTrue(tripleStore.bMatrix().spoQuery(s, p, o2));
        assertTrue(tripleStore.bMatrix().spoQuery(s, p, o3));

        assertAll();
    }

    @Test
    public void add_invalid_throws() {
        int s = tripleStore.dict().encodeSO("DCC20");
        int p = tripleStore.dict().encodeP("has topic");
        int o = tripleStore.dict().encodeSO("Text comp.");

        assertThrows(TripleAlreadyExistsException.class, () -> tripleStore.bMatrix().add(s, p, o));
    }

    @Test
    public void deleteThenAdd_valid_tripleDeletedAndAdded() {
        int sDel = tripleStore.dict().encodeSO("DCC20");
        int pDel = tripleStore.dict().encodeP("has topic");
        int oDel = tripleStore.dict().encodeSO("Text comp.");
        int sAdd = tripleStore.dict().encodeSO("DCC20");
        int pAdd = tripleStore.dict().encodeP("has topic");
        int oAdd = tripleStore.dict().encodeSO("Chile");

        assertTrue(tripleStore.bMatrix().delete(sDel, pDel, oDel));
        assertTrue(tripleStore.bMatrix().add(sAdd, pAdd, oAdd));
    }

    @Test
    public void update_valid_tripleUpdated() {
        int sDel = tripleStore.dict().encodeSO("DCC20");
        int pDel = tripleStore.dict().encodeP("has topic");
        int oDel = tripleStore.dict().encodeSO("Text comp.");
        int sAdd = tripleStore.dict().encodeSO("DCC20");
        int pAdd = tripleStore.dict().encodeP("has topic");
        int oAdd = tripleStore.dict().encodeSO("Chile");

        assertTrue(tripleStore.bMatrix().update(sDel, pDel, oDel, sAdd, pAdd, oAdd));
        assertTrue(tripleStore.bMatrix().spoQuery(sAdd, pAdd, oAdd));
        assertFalse(tripleStore.bMatrix().spoQuery(sDel, pDel, oDel));
    }

    @Test
    public void gabba() {
        assertTrue(tripleStore.delete(
                new Triple("A. Bovik", "lives in", "US")
        ));
        assertTrue(tripleStore.delete(
                new Triple("G. Sullivan", "lives in", "US")
        ));
        assertTrue(tripleStore.create(
                new Triple("Gabba", "und", "Atzen")
        ));
    }
}
