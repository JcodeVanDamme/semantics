package com.semantics.dynamicK2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.SuxBitString;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.DynamicBitVectorBuilder;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.Entry;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.InternalNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class Dk2BuildTests {
    // Tests done using prefabricated BitStrings as seen on p.109 in Paper:
    // "Compressed representation of dynamic binary relations with applications"

    static BitInterface statT;
    static BitInterface statL;
    static DynamicBitVector dynT;
    static DynamicBitVector dynL;

    @BeforeAll
    static void init() {
        List<Boolean> tBits = new ArrayList<>(
                List.of(
                true, true, true, false,
                true, true, false, true,
                true, false, true, false,
                false, true, false, false,
                false, true, true, false,
                true, false, false, true,
                false, true, false, true,
                false, false, true, false,
                true, false, true, false,
                true, true, false, false
            )
        );
        statT = new SuxBitString(tBits);
        dynT = DynamicBitVectorBuilder.build(statT, 8, 1, 2);


        List<Boolean> lBits = new ArrayList<>(
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
        statL = new SuxBitString(lBits);
        dynL = DynamicBitVectorBuilder.build(statL, 8, 1, 3);
    }

    @Test
    void t_rootEntriesTest() {
        Entry e1 = ((InternalNode) dynT.root()).entries().get(0);
        Entry e2 = ((InternalNode) dynT.root()).entries().get(1);

        assertEquals(23, e1.b());
        assertEquals(16, e1.o());
        assertEquals(8, e2.b());
        assertEquals(4, e2.o());
    }

    @Test
    void l_rootEntriesTest() {
        Entry e1 = ((InternalNode) dynL.root()).entries().get(0);
        Entry e2 = ((InternalNode) dynL.root()).entries().get(1);

        assertEquals(24, e1.b());
        assertEquals(8, e1.o());
        assertEquals(20, e2.b());
        assertEquals(6, e2.o());
    }

    @Test
    void t_bitSumTest() {
        Entry e1 = ((InternalNode) dynT.root()).entries().get(0);
        Entry e2 = ((InternalNode) dynT.root()).entries().get(1);

        assertEquals(statT.size(), e1.b() + e2.b());
        assertEquals(statT.countOnes(), e1.o() + e2.o());
    }

    @Test
    void l_bitSumTest() {
        Entry e1 = ((InternalNode) dynL.root()).entries().get(0);
        Entry e2 = ((InternalNode) dynL.root()).entries().get(1);

        assertEquals(statL.size(), e1.b() + e2.b());
        assertEquals(statL.countOnes(), e1.o() + e2.o());
    }
}
