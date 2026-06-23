package com.semantics.k2.dynamicK2;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Builder;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Configuration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2TreeBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DK2UpdateTests {

    static DK2Tree tree;

    @BeforeAll
    public static void setup() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(0, 0));
        cells.add(new Cell(1, 1));
        cells.add(new Cell(2, 2));
        cells.add(new Cell(3, 3));
        cells.add(new Cell(4, 4));

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        K2Tree staticTree = k2Builder.constructK2(2, cells, 8);
        tree = DK2Builder.build(staticTree, new DK2Configuration(4, 1, 1, 3));
    }

    @Test
    public void update_cellNotSet_Test() {
        assertFalse(tree.checkCell(5, 0));
        tree.updateCell(5,0, true);
        assertTrue(tree.checkCell(5, 0));
    }
}
