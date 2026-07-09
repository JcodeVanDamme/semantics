package com.semantics.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.RoaringBitString;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorBuilder;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.InternalNode;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.LeafNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddTests {

    @Test
    void addToRoot_capacityNotReached() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true,
                        true, true, true, true
                )
        );
        BitInterface b = new RoaringBitString(bits);

        // Root [1,1,1,1, 1,1,1,1]
        DynamicBitVector bitVector = DynamicBitVectorBuilder.build(b, new DynamicBitVectorConfiguration(12, 1, 1, 1));

        // insert k*K = 4 zeros at index 2
        bitVector.addK2Bits((LeafNode) bitVector.root(), 2, 4);

        // Root [1,1,1,1, 0,0,0,0, 1,1,1,1]

        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(0));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(1));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(2));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(3));
        assertEquals(0, ((LeafNode) bitVector.root()).bits().access(4));
        assertEquals(0, ((LeafNode) bitVector.root()).bits().access(5));
        assertEquals(0, ((LeafNode) bitVector.root()).bits().access(6));
        assertEquals(0, ((LeafNode) bitVector.root()).bits().access(7));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(8));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(9));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(10));
        assertEquals(1, ((LeafNode) bitVector.root()).bits().access(11));
    }

    @Test
    void addToRoot_capacityReached() {
        List<Boolean> bits = new ArrayList<>(
                List.of(
                        true, true, true, true,
                        true, true, true, true
                )
        );
        BitInterface b = new RoaringBitString(bits);

        // Root [1,1,1,1, 1,1,1,1]
        DynamicBitVector bitVector = DynamicBitVectorBuilder.build(b, new DynamicBitVectorConfiguration(8, 1, 1, 1));

        // insert k*K = 4 zeros at index 2
        bitVector.addK2Bits((LeafNode) bitVector.root(), 2, 4);
        LeafNode l1 = (LeafNode) ((InternalNode) bitVector.root()).entries().get(0).p();
        LeafNode l2 = (LeafNode) ((InternalNode) bitVector.root()).entries().get(1).p();

        // Root L1 [1,1,1,1, 0,0,0,0] L2 [1,1,1,1]

        assertEquals(1, (l1.bits().access(0)));
        assertEquals(1, (l1.bits().access(1)));
        assertEquals(1, (l1.bits().access(2)));
        assertEquals(1, (l1.bits().access(3)));
        assertEquals(0, (l1.bits().access(4)));
        assertEquals(0, (l1.bits().access(5)));
        assertEquals(0, (l1.bits().access(6)));
        assertEquals(0, (l1.bits().access(7)));
        assertEquals(1, (l2.bits().access(0)));
        assertEquals(1, (l2.bits().access(1)));
        assertEquals(1, (l2.bits().access(2)));
        assertEquals(1, (l2.bits().access(3)));
    }

    @Test
    void addToMultiLevel_capacityNotReached() {
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

                        true, true, true, true
                )
        );
        BitInterface b = new RoaringBitString(bits);

        // Root
        // I1
        //   L1 {2, 3, 6, 7}
        //   L2 {2, 6}
        //   L3 {3, 6}
        // I2
        //   L4 {1, 6}
        //   L5 {0, 6}
        //   L6 {0, 1, 2, 3}
        DynamicBitVector bitVector = DynamicBitVectorBuilder.build(b, new DynamicBitVectorConfiguration(8, 1, 1, 3));

        InternalNode i1 = (InternalNode) ((InternalNode) bitVector.root()).entries().get(0).p();
        LeafNode l1 = (LeafNode) i1.entries().get(0).p();
        LeafNode l2 = (LeafNode) i1.entries().get(1).p();
        LeafNode l3 = (LeafNode) i1.entries().get(2).p();

        InternalNode i2 = (InternalNode) ((InternalNode) bitVector.root()).entries().get(1).p();
        LeafNode l4 = (LeafNode) i2.entries().get(0).p();
        LeafNode l5 = (LeafNode) i2.entries().get(1).p();
        LeafNode l6 = (LeafNode) i2.entries().get(2).p();

        // Root
        // I1
        //   L1 {2, 3, 6, 7}
        //   L2 {2, 6}
        //   L3 {3, 6}
        // I2
        //   L4 {1, 6}
        //   L5 {0, 6}
        //   L6 {4,5, 6, 7}
        bitVector.addK2Bits(l6, 2, 0);

        assertEquals(0, (l1.bits().access(0)));
        assertEquals(0, (l1.bits().access(1)));
        assertEquals(1, (l1.bits().access(2)));
        assertEquals(1, (l1.bits().access(3)));
        assertEquals(0, (l1.bits().access(4)));
        assertEquals(0, (l1.bits().access(5)));
        assertEquals(1, (l1.bits().access(6)));
        assertEquals(1, (l1.bits().access(7)));

        assertEquals(0, (l2.bits().access(0)));
        assertEquals(0, (l2.bits().access(1)));
        assertEquals(1, (l2.bits().access(2)));
        assertEquals(0, (l2.bits().access(3)));
        assertEquals(0, (l2.bits().access(4)));
        assertEquals(0, (l2.bits().access(5)));
        assertEquals(1, (l2.bits().access(6)));
        assertEquals(0, (l2.bits().access(7)));

        assertEquals(0, (l3.bits().access(0)));
        assertEquals(0, (l3.bits().access(1)));
        assertEquals(0, (l3.bits().access(2)));
        assertEquals(1, (l3.bits().access(3)));
        assertEquals(0, (l3.bits().access(4)));
        assertEquals(0, (l3.bits().access(5)));
        assertEquals(1, (l3.bits().access(6)));
        assertEquals(0, (l3.bits().access(7)));

        assertEquals(0, (l4.bits().access(0)));
        assertEquals(1, (l4.bits().access(1)));
        assertEquals(0, (l4.bits().access(2)));
        assertEquals(0, (l4.bits().access(3)));
        assertEquals(0, (l4.bits().access(4)));
        assertEquals(0, (l4.bits().access(5)));
        assertEquals(1, (l4.bits().access(6)));
        assertEquals(0, (l4.bits().access(7)));

        assertEquals(1, (l5.bits().access(0)));
        assertEquals(0, (l5.bits().access(1)));
        assertEquals(0, (l5.bits().access(2)));
        assertEquals(0, (l5.bits().access(3)));
        assertEquals(0, (l5.bits().access(4)));
        assertEquals(0, (l5.bits().access(5)));
        assertEquals(1, (l5.bits().access(6)));
        assertEquals(0, (l5.bits().access(7)));

        assertEquals(0, (l6.bits().access(0)));
        assertEquals(0, (l6.bits().access(1)));
        assertEquals(0, (l6.bits().access(2)));
        assertEquals(0, (l6.bits().access(3)));
        assertEquals(1, (l6.bits().access(4)));
        assertEquals(1, (l6.bits().access(5)));
        assertEquals(1, (l6.bits().access(6)));
        assertEquals(1,  (l6.bits().access(7)));
    }
}
