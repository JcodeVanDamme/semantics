package com.semantics.k2.dynamicK2;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Builder;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2TreeBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DK2CreateDeleteTests {

    static DK2Tree tree;

    @BeforeAll
    public static void setup() {
        List<Cell> testST = new ArrayList<>();
        testST.add(new Cell(1, 1));
        testST.add(new Cell(1, 3));
        testST.add(new Cell(2, 5));
        testST.add(new Cell(4, 7));
        testST.add(new Cell(5, 6));
        testST.add(new Cell(6, 2));


        K2TreeBuilder k2Builder = new K2TreeBuilder();
        K2Tree staticTree = k2Builder.constructK2(2, testST, 8);
        tree = DK2Builder.build(staticTree, new DynamicBitVectorConfiguration(4, 1, 1, 3), testST.size());
    }

    @Test
    public void test() {

    }
}
