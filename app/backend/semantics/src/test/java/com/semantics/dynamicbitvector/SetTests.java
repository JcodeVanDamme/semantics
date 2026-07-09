package com.semantics.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.RoaringBitString;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetTests {

    DynamicBitVector bitVector;
    InternalNode root;
    InternalNode internal1;
    InternalNode internal2;
    LeafNode leaf1;

    @BeforeEach
    void init() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        false, false, true, true,
                        false, false, true, true,
                        false, false, true, false,
                        false, false, true, false,
                        false, false, false, true,
                        false, false, true, false,
                        false, true, false, false,
                        false, false, true, false,
                        true, false, false, false,
                        false, false, true, false,
                        true, false, true, false
                )
        );
        BitInterface b = new RoaringBitString(bits);

        bitVector = DynamicBitVectorBuilder.build(b, new DynamicBitVectorConfiguration(8, 1, 1, 3));
        root = ((InternalNode)bitVector.root());
        internal1 = ((InternalNode)((InternalNode) bitVector.root()).entries().get(0).p());
        internal2 = ((InternalNode)((InternalNode) bitVector.root()).entries().get(1).p());
        leaf1 = ((LeafNode) internal1.entries().get(0).p());
    }

    @Test
    void setOne_OneCount() {
        bitVector.set(true, leaf1, 0);

        assertEquals(9 , ((InternalNode) root).entries().get(0).o());

        assertEquals(5 , internal1.entries().get(0).o());
        assertEquals(2 , internal1.entries().get(1).o());
        assertEquals(2 , internal1.entries().get(2).o());

        assertEquals(6 , ((InternalNode) root).entries().get(1).o());

        assertEquals(2 , internal2.entries().get(0).o());
        assertEquals(2 , internal2.entries().get(1).o());
        assertEquals(2 , internal2.entries().get(2).o());
    }

    @Test
    void unsetOne_OneCount() {
        bitVector.set(false, leaf1, 7);

        assertEquals(7 , ((InternalNode) root).entries().get(0).o());

        assertEquals(3 , internal1.entries().get(0).o());
        assertEquals(2 , internal1.entries().get(1).o());
        assertEquals(2 , internal1.entries().get(2).o());

        assertEquals(6 , ((InternalNode) root).entries().get(1).o());

        assertEquals(2 , internal2.entries().get(0).o());
        assertEquals(2 , internal2.entries().get(1).o());
        assertEquals(2 , internal2.entries().get(2).o());
    }
}
