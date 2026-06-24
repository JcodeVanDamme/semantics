package com.semantics.bitstring.roaringbitstring;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.RoaringBitString;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.InternalNode;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.LeafNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddRemoveBitsTests {

    DynamicBitVector bitVector;
    InternalNode root;
    InternalNode internal1;
    InternalNode internal2;
    LeafNode leaf1;

    @Test
    void addBits_four_Test() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true
                )
        );
        // [1,1,1,1] / {0,1,2,3}
        BitInterface b = new RoaringBitString(bits);

        // [0,0,0,0 1,1,1,1] / {4,5,6,7}
        b.addBits(0, 4);

        assertEquals(8, b.size());
        assertEquals(0, b.access(0));
        assertEquals(0, b.access(1));
        assertEquals(0, b.access(2));
        assertEquals(0, b.access(3));
        assertEquals(1, b.access(4));
        assertEquals(1, b.access(5));
        assertEquals(1, b.access(6));
        assertEquals(1, b.access(7));
    }

    @Test
    void addBits_zero_Test() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true
                )
        );
        // [1,1,1,1] / {0,1,2,3}
        BitInterface b = new RoaringBitString(bits);

        // [1,1,1,1] / {0,1,2,3}
        b.addBits(0, 0);

        assertEquals(4, b.size());
        assertEquals(1, b.access(0));
        assertEquals(1, b.access(1));
        assertEquals(1, b.access(2));
        assertEquals(1, b.access(3));
    }

    @Test
    void removeBits_mid_four_Test() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true,
                        true, true, true, true,
                        false, false, false, true
                )
        );
        // [1,1,1,1, 1,1,1,1, 0,0,0,1] / {0,1,2,3 4,5,6,7,11}
        BitInterface b = new RoaringBitString(bits);

        // [1,1,1,1,0,0,0,1] / {0,1,2,3,7}
        b.removeBits(4, 4);

        assertEquals(8, b.size());
        assertEquals(1, b.access(0));
        assertEquals(1, b.access(1));
        assertEquals(1, b.access(2));
        assertEquals(1, b.access(3));
        assertEquals(0, b.access(4));
        assertEquals(0, b.access(5));
        assertEquals(0, b.access(6));
        assertEquals(1, b.access(7));
    }

    @Test
    void removeBits_zero_Test() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true,
                        true, true, true, true
                )
        );
        // [1,1,1,1] / {0,1,2,3}
        BitInterface b = new RoaringBitString(bits);

        // [1,1] / {0,1}
        b.removeBits(0, 0);

        assertEquals(8, b.size());
        assertEquals(1, b.access(0));
        assertEquals(1, b.access(1));
        assertEquals(1, b.access(2));
        assertEquals(1, b.access(3));
    }

    @Test
    void removeBits_start_four_Test() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true,
                        false, false, false, true,
                        false, false, false, true
                )
        );
        // [1,1,1,1, 0,0,0,1, 0,0,0,1] / {0,1,2,3,7,11}
        BitInterface b = new RoaringBitString(bits);

        // [0,0,0,1, 0,0,0,1] / {3,7}
        b.removeBits(0, 4);

        assertEquals(8, b.size());
        assertEquals(0, b.access(0));
        assertEquals(0, b.access(1));
        assertEquals(0, b.access(2));
        assertEquals(1, b.access(3));
        assertEquals(0, b.access(4));
        assertEquals(0, b.access(5));
        assertEquals(0, b.access(6));
        assertEquals(1, b.access(7));
    }
}
