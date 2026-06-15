package com.semantics.staticK2;
import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2TreeBuilder;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class K2TreeTests {
    // Tests done using prefabricated Cells as seen on p.5
    // in Paper: "Revisiting compact RDF stores based on k2-trees"
    @Test
    void K2TestST() {
        List<Cell> testST = new ArrayList<>();
        testST.add(new Cell(0, 7));
        testST.add(new Cell(0, 8));
        testST.add(new Cell(0, 9));
        testST.add(new Cell(1, 4));
        testST.add(new Cell(2, 0));
        testST.add(new Cell(2, 12));
        testST.add(new Cell(3, 1));
        testST.add(new Cell(3, 5));
        testST.add(new Cell(3, 11));
        testST.add(new Cell(4, 2));
        testST.add(new Cell(4, 13));
        testST.add(new Cell(5, 3));
        testST.add(new Cell(5, 6));
        testST.add(new Cell(5, 10));

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        K2Tree st = k2Builder.constructK2(2, testST, 16);

        assertEquals(
          "T: {0, 1, 4, 5, 6, 7, 8, 9, 10, 11, 14, 16, 17, 18, 21, 25, 28, 31, 34, 37, 40}\n"
                + "L: {0, 3, 6, 9, 15, 16, 19, 22, 24, 25, 31, 32, 38, 41}",
          st.toString()
        );
    }
    @Test
    void K2TestOT() {
        List<Cell> testOT = new ArrayList<>();
        testOT.add(new Cell(1, 0));
        testOT.add(new Cell(1, 1));
        testOT.add(new Cell(1, 2));
        testOT.add(new Cell(1, 3));
        testOT.add(new Cell(1, 9));
        testOT.add(new Cell(2, 10));
        testOT.add(new Cell(3, 11));
        testOT.add(new Cell(4, 5));
        testOT.add(new Cell(4, 6));
        testOT.add(new Cell(4, 7));
        testOT.add(new Cell(5, 12));
        testOT.add(new Cell(5, 13));
        testOT.add(new Cell(6, 4));
        testOT.add(new Cell(7, 8));

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        K2Tree ot = k2Builder.constructK2(2, testOT, 16);

        assertEquals(
                "T: {0, 1, 4, 7, 8, 10, 11, 12, 13, 16, 17, 18, 20, 23, 26, 28}\n"
                + "L: {2, 3, 6, 7, 9, 12, 13, 16, 23, 24, 27, 30, 34, 35}",
                ot.toString()
        );
    }
}
