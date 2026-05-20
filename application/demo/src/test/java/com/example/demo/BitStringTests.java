package com.example.demo;
import com.example.demo.bMatrix.BitString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BitStringTests {
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
        BitString b1 = new BitString(bits1);

        // counts the number of occurrences of bit c in b up to position i

        assertEquals(6, b1.rank(true, bits1.size()));
        assertEquals(8, b1.rank(false, bits1.size()));
        assertEquals(2, b1.rank(true, 7));
        assertEquals(5, b1.rank(false, 7));

        // Exceeding Bounds
        assertThrows(IndexOutOfBoundsException.class, () -> b1.rank(true, bits1.size() + 1));
        assertThrows(IndexOutOfBoundsException.class, () -> b1.rank(true, -1));

        // []
        List<Boolean> bits2 = new ArrayList<>();
        BitString b2 = new BitString(bits2);

        assertEquals(0, b2.rank(true, bits2.size()));
        assertEquals(0, b2.rank(false, bits2.size()));
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
        BitString b1 = new BitString(bits1);

        // returns the position in b of the j-th bit set to c
        assertEquals(0, b1.select(false, 1));
        assertEquals(2, b1.select(true, 1));
        assertEquals(11, b1.select(true, 4));
        assertEquals(6, b1.select(false, 5));

        // Querying more than present
        assertThrows(ArrayStoreException.class, () -> b1.select(true, 20));
    }
}
