package com.example.demo.bMatrix;

import java.util.*;

public class bMatrix {
    private final int k;

    TripleProvider provider;
    List<Triple> encodedTriples;

    int soID;
    HashMap<Integer, String> soStringFromID;
    HashMap<String, Integer> soIDFromString;

    int pID;
    HashMap<Integer, String> pStringFromID;
    HashMap<String, Integer> pIDFromString;

    int numSubjects;
    int numObjects;

    public K2Tree st;
    K2Tree ot;



    public bMatrix(int k, TripleProvider provider) {
        this.k = k;

        this.provider = provider;
        encodedTriples = new ArrayList<>();

        soID = 0;
        soStringFromID = new HashMap<>();
        soIDFromString = new HashMap<>();

        pID = 0;
        pStringFromID = new HashMap<>();
        pIDFromString = new HashMap<>();

        numSubjects = 0;
        numObjects = 0;

        st = new K2Tree(k);
        ot = new K2Tree(k);
    }

    public void init() {
        List<Triple> triples = provider.getTriples();
        assembleIdentifiers(triples);
        decodeTriples(triples);
        assembleBinaryMatrices();
    }

    private void assembleIdentifiers(List<Triple> triples) {
        Set<String> subjects = new HashSet<>();
        Set<String> objects = new HashSet<>();
        Set<String> predicates = new HashSet<>();

        // Collect unique Strings according to Triple-Type
        for (Triple t : triples) {
            subjects.add((String) t.s());
            objects.add((String) t.o());
            predicates.add((String) t.p());
        }
        numSubjects = subjects.size();
        numObjects = objects.size();

        // Subject-Objects need to be remembered to prevent multiple Inclusions
        Set<String> added = new HashSet<>();

        // SO
        for (String s : subjects) {
            if (objects.contains(s)) {
                added.add(s);
                int id = soID++;
                soStringFromID.put(id, s);
                soIDFromString.put(s, id);
            }
        }
        // S
        for (String s : subjects) {
            if (!added.contains(s)) {
                int id = soID++;
                soStringFromID.put(id, s);
                soIDFromString.put(s, id);
            }
        }
        // O
        for (String o : objects) {
            if (!added.contains(o)) {
                int id = soID++;
                soStringFromID.put(id, o);
                soIDFromString.put(o, id);
            }
        }
        // P
        for (String p : predicates) {
            int id = pID++;
            pStringFromID.put(id, p);
            pIDFromString.put(p, id);
        }
    }
    private void decodeTriples(List<Triple> triples) {
        for (Triple t : triples) {
            encodedTriples.add(new Triple(
                    soIDFromString.get(t.s()),
                    pIDFromString.get(t.p()),
                    soIDFromString.get(t.o())
            ));
        }
        encodedTriples.sort(
                Comparator.comparingInt(t -> (Integer) t.p())
        );
    }

    private void assembleBinaryMatrices() {
        // Determine initial Matrix Size
        // -> Max of given Column / Rows increased to next Power of K
        int max = Math.max(
                Math.max(
                        numSubjects,
                        numObjects
                )
                ,encodedTriples.size()
        );
        int size = 1;
        while (size < max) {
            size *= k;
        }
        // Assemble Lists of filled Matrix Cells
        // -> Traverse Matrices sparsely over these instead of looping over all Cells
        List<Cell> stCells = new ArrayList<>();
        List<Cell> otCells = new ArrayList<>();
        for (int i = 0; i < encodedTriples.size(); i++) {
            Triple t = encodedTriples.get(i);
            stCells.add(new Cell(
                    (int) t.s(),
                    i
            ));
            otCells.add(new Cell(
                    (int) t.o(),
                    i
            ));
        }

        /* ****************************************************************************

        // PREFAB. DEBUG CELLS REPRESENTING LAYOUT OF ST-MATRIX AS SEEN ON P.5 IN PAPER

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
        assembleK2(st, testST, 0, 0, size, true);

        // PREFAB. DEBUG CELLS REPRESENTING LAYOUT OF OT-MATRIX AS SEEN ON P.5 IN PAPER

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
        assembleK2(st, testOT, 0, 0, size, true);

        **************************************************************************** */

        assembleK2(st, stCells, 0, 0, size, true);
        st.assembleBitStrings();
        assembleK2(ot, otCells, 0, 0, size, true);
        ot.assembleBitStrings();
    }
    public void assembleK2(K2Tree tree, List<Cell> currentCells,  int rowStart, int colStart, int matrixSize, boolean skipBit) {
        // Leave Reached
        if (matrixSize == 1) {
            if (!currentCells.isEmpty()) {
                Cell c = currentCells.get(0);
                if (c.row() == rowStart && c.col() == colStart) {
                    // Set next Bit in L to 1
                    tree.setL(true);
                    return;
                }
            }
            // Set next Bit in L to 0
            tree.setL(false);
            return;
        }

        boolean match = false;
        for (Cell c : currentCells) {
            if (matchCell(c, rowStart, colStart, matrixSize)) {
                match = true;
                break;
            }
        }
        // Skip Addition for the Root Node
        if (!skipBit) {
            // Set next Bit in T to 1 if a matching Cell was found; else set Bit to 0
            tree.setT(matrixSize, match);
        }
        // Break Subdivision Process for Empty Matrices
        if (!match) {
            return;
        }

        // Continue processing Sub-Matrices
        int childSize = matrixSize / k;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {

                int newRowStart = rowStart + i * childSize;
                int newColStart = colStart + j * childSize;

                // Reduce Cells for each Sub-Matrix
                List<Cell> childCells = new ArrayList<>();
                for (Cell c : currentCells) {
                    if (matchCell(c, newRowStart, newColStart, childSize)) {
                        childCells.add(c);
                    }
                }

                assembleK2(tree, childCells, newRowStart, newColStart, childSize, false);
            }
        }
    }
    private boolean matchCell(Cell c, int rowStart, int colStart, int matrixSize) {
        if (c.row() >= rowStart && c.row() < rowStart + matrixSize) {
            return c.col() >= colStart && c.col() < colStart + matrixSize;
        }
        return false;
    }
}
