package com.semantics.staticK2;
import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2TreeBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class K2CheckCellTests {

    static K2Tree tree;

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
        tree = k2Builder.constructK2(2, testST, 8);
    }

    @Test
    public void existsTest() {
        // Row 0
        assertFalse(tree.checkCell(0, 0));
        assertFalse(tree.checkCell(0, 1));
        assertFalse(tree.checkCell(0, 2));
        assertFalse(tree.checkCell(0, 3));
        assertFalse(tree.checkCell(0, 4));
        assertFalse(tree.checkCell(0, 5));
        assertFalse(tree.checkCell(0, 6));
        assertFalse(tree.checkCell(0, 7));

        // Row 1
        assertFalse(tree.checkCell(1, 0));
        assertTrue(tree.checkCell(1, 1));
        assertFalse(tree.checkCell(1, 2));
        assertTrue(tree.checkCell(1, 3));
        assertFalse(tree.checkCell(1, 4));
        assertFalse(tree.checkCell(1, 5));
        assertFalse(tree.checkCell(1, 6));
        assertFalse(tree.checkCell(1, 7));

        // Row 2
        assertFalse(tree.checkCell(2, 0));
        assertFalse(tree.checkCell(2, 1));
        assertFalse(tree.checkCell(2, 2));
        assertFalse(tree.checkCell(2, 3));
        assertFalse(tree.checkCell(2, 4));
        assertTrue(tree.checkCell(2, 5));
        assertFalse(tree.checkCell(2, 6));
        assertFalse(tree.checkCell(2, 7));

        // Row 3
        assertFalse(tree.checkCell(3, 0));
        assertFalse(tree.checkCell(3, 1));
        assertFalse(tree.checkCell(3, 2));
        assertFalse(tree.checkCell(3, 3));
        assertFalse(tree.checkCell(3, 4));
        assertFalse(tree.checkCell(3, 5));
        assertFalse(tree.checkCell(3, 6));
        assertFalse(tree.checkCell(3, 7));

        // Row 4
        assertFalse(tree.checkCell(4, 0));
        assertFalse(tree.checkCell(4, 1));
        assertFalse(tree.checkCell(4, 2));
        assertFalse(tree.checkCell(4, 3));
        assertFalse(tree.checkCell(4, 4));
        assertFalse(tree.checkCell(4, 5));
        assertFalse(tree.checkCell(4, 6));
        assertTrue(tree.checkCell(4, 7));

        // Row 5
        assertFalse(tree.checkCell(5, 0));
        assertFalse(tree.checkCell(5, 1));
        assertFalse(tree.checkCell(5, 2));
        assertFalse(tree.checkCell(5, 3));
        assertFalse(tree.checkCell(5, 4));
        assertFalse(tree.checkCell(5, 5));
        assertTrue(tree.checkCell(5, 6));
        assertFalse(tree.checkCell(5, 7));

        // Row 6
        assertFalse(tree.checkCell(6, 0));
        assertFalse(tree.checkCell(6, 1));
        assertTrue(tree.checkCell(6, 2));
        assertFalse(tree.checkCell(6, 3));
        assertFalse(tree.checkCell(6, 4));
        assertFalse(tree.checkCell(6, 5));
        assertFalse(tree.checkCell(6, 6));
        assertFalse(tree.checkCell(6, 7));

        // Row 7
        assertFalse(tree.checkCell(7, 0));
        assertFalse(tree.checkCell(7, 1));
        assertFalse(tree.checkCell(7, 2));
        assertFalse(tree.checkCell(7, 3));
        assertFalse(tree.checkCell(7, 4));
        assertFalse(tree.checkCell(7, 5));
        assertFalse(tree.checkCell(7, 6));
        assertFalse(tree.checkCell(7, 7));
    }

    @Test
    public void boundedRowQueryTest() {
        List<Integer> results1 = new ArrayList<>();
        results1.add(1);
        results1.add(3);
        assertEquals(results1, tree.boundedRowQuery(1, 0, 7));

        List<Integer> results2 = new ArrayList<>();
        assertEquals(results2, tree.boundedRowQuery(7, 0, 7));
    }
}
