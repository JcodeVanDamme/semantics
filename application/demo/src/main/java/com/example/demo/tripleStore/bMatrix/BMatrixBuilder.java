package com.example.demo.tripleStore.bMatrix;

import com.example.demo.tripleStore.bitString.BitStringPredicate;
import com.example.demo.tripleStore.k2Tree.Cell;
import com.example.demo.tripleStore.k2Tree.K2Tree;
import com.example.demo.tripleStore.k2Tree.K2TreeBuilder;
import com.example.demo.tripleStore.triple.Triple;
import com.example.demo.tripleStore.triple.TripleProvider;

import java.util.*;

public class BMatrixBuilder {

    private int k;
    private List<Triple> triples;

    private int sCount = 0;
    private int pCount = 0;
    private int oCount = 0;

    private K2Tree st;
    private K2Tree ot;

    public BMatrix build(int k, int d, TripleDictionary dict, TripleProvider provider) {
        this.k = k;

        triples = TripleEncoder.encode(provider, dict);

        countValues();
        assembleBinaryMatrices();
        BitStringPredicate bp = new BitStringPredicate(pCount, this.triples, d);

        return new BMatrix(triples, st, ot, bp);
    }

    public void countValues() {
        Set<Integer> subjects = new HashSet<>();
        Set<Integer> objects = new HashSet<>();
        Set<Integer> predicates = new HashSet<>();

        // Collect unique Strings according to Triple-Type
        for (Triple t : triples) {
            subjects.add((Integer) t.s());
            objects.add((Integer) t.o());
            predicates.add((Integer) t.p());
        }

        sCount = subjects.size();
        oCount = objects.size();
        pCount = predicates.size();
    }

    private void assembleBinaryMatrices() {
        // Determine initial Matrix Size
        // -> Max of given Column / Rows increased to next Power of K
        int max = Math.max(
                Math.max(
                        sCount,
                        oCount
                )
                , triples.size()
        );
        int size = 1;
        while (size < max) {
            size *= k;
        }
        // Assemble Lists of filled Matrix Cells
        // -> Traverse Matrices sparsely over these instead of looping over all Cells
        List<Cell> stCells = new ArrayList<>();
        List<Cell> otCells = new ArrayList<>();
        for (int i = 0; i < triples.size(); i++) {
            Triple t = triples.get(i);
            stCells.add(new Cell(
                    (int) t.s(),
                    i
            ));
            otCells.add(new Cell(
                    (int) t.o(),
                    i
            ));
        }

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        st = k2Builder.constructK2(k, stCells, size);
        ot = k2Builder.constructK2(k, otCells, size);
    }
}
