package com.semantics.dynamicK2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.SuxBitString;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.DK2Builder;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.DK2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.DynamicBitVectorBuilder;
import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.LeafNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DK2FindLeafTests {

    // Tests done using prefabricated BitStrings as seen on p.109 in Paper:
    // "Compressed representation of dynamic binary relations with applications"

    static DK2Tree tree;

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
        BitInterface statT = new SuxBitString(tBits);
        DynamicBitVector dynT = DynamicBitVectorBuilder.build(statT, 8, 1, 2);

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
        BitInterface statL = new SuxBitString(lBits);
        DynamicBitVector dynL = DynamicBitVectorBuilder.build(statL, 8, 1, 3);

        tree = new DK2Tree(dynT, dynL);
    }
    @Test
    void findLeaf() {
        LeafNode target1 = tree.findTLeaf(0);
        LeafNode target2 = tree.findTLeaf(7);

        assertEquals(target1.bits().toString(), target2.bits().toString());

        LeafNode target3 = tree.findTLeaf(8);
        LeafNode target4 = tree.findTLeaf(15);

        assertEquals(target3.bits().toString(), target4.bits().toString());
    }
}
