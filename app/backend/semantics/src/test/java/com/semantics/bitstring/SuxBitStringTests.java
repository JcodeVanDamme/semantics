package com.semantics.bitstring;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.NaiveBitString;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.SuxBitString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SuxBitStringTests {
    @Test
    void rankTests() {
        List<Boolean> bits1 = new ArrayList<>();
        bits1.add(false);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);
        bits1.add(true);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);

        // [0 1 0 0 1 0 0 1 1 1 0 0 0 1]
        SuxBitString b1 = new SuxBitString(bits1);

        assertEquals(0, b1.rank1(0));

        assertEquals(0, b1.rank1(1));

        assertEquals(1, b1.rank1(2));

        assertEquals(5, b1.rank1(13));

        assertEquals(6, b1.rank1(14));

        // Exceeding Bounds
        assertThrows(IndexOutOfBoundsException.class, () -> b1.rank1(69));
        assertThrows(IndexOutOfBoundsException.class, () -> b1.rank1(-1));

        // []
        List<Boolean> bits2 = new ArrayList<>();
        NaiveBitString b2 = new NaiveBitString(bits2);

        assertEquals(0, b2.rank1(bits2.size()));
    }

    @Test
    void selectTests() {
        List<Boolean> bits1 = new ArrayList<>();

        bits1.add(false);
        bits1.add(false);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);
        bits1.add(false);
        bits1.add(false);
        bits1.add(true);

        // [0 0 1 0 0 1 0 0 1 0 0 1]
        SuxBitString b1 = new SuxBitString(bits1);

        assertEquals(2, b1.select1(1));
        assertEquals(11, b1.select1(4));

        // Querying more than present
        assertThrows(IndexOutOfBoundsException.class, () -> b1.select1(20));
    }
    @Test
    public void accessTests() {
        List<Boolean> bits = new ArrayList<>();

        bits.add(false);
        bits.add(false);
        bits.add(true);

        // [0 0 1]
        SuxBitString b = new SuxBitString(bits);

        // gets the bit value at b[i]

        assertEquals(0, b.access(0));
        assertEquals(1, b.access(2));

        // Exceeding Bounds
        assertThrows(IndexOutOfBoundsException.class, () -> b.access(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> b.access(69));
    }

}
