package com.semantics.k2;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Builder;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2TreeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DK2UpdateTests {

    DK2Tree tree;

    @BeforeEach
    public void setup() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(0, 0));
        cells.add(new Cell(0, 2));
        cells.add(new Cell(3, 0));
        cells.add(new Cell(5, 2));
        cells.add(new Cell(5, 3));

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        K2Tree staticTree = k2Builder.constructK2(2, cells, 8);
        tree = DK2Builder.build(staticTree, new DynamicBitVectorConfiguration(4, 1, 1, 3), cells.size());
    }

    @Test
    public void setCell_cellNotSet_Test() {
        System.out.println(tree);
        assertFalse(tree.checkCell(2, 7));
        tree.updateCell(2, 7, true);
        assertTrue(tree.checkCell(2, 7));
        System.out.println(tree);
    }

    @Test
    public void setCell_cellSet_Test() {
        System.out.println(tree);
        assertTrue(tree.checkCell(0, 0));
        tree.updateCell(0, 0, true);
        assertTrue(tree.checkCell(0, 0));
        System.out.println(tree);
    }

    @Test
    public void unsetCell_cellSet_noPruning_Test() {
        System.out.println(tree);
        assertTrue(tree.checkCell(5, 3));
        tree.updateCell(5, 3, false);
        System.out.println(tree);
        assertFalse(tree.checkCell(5, 3));
    }

    @Test
    public void unsetCell_cellSet_pruning_Test() {
        System.out.println(tree);
        assertTrue(tree.checkCell(0, 0));
        tree.updateCell(0, 0, false);
        System.out.println(tree);
        assertFalse(tree.checkCell(0, 0));
    }

    @Test
    void gabba() {


    }
}
