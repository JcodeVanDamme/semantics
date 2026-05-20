package com.example.demo.bMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class K2TreeBuilder {

    // Temporary List-Structures used for Assembly
    private final List<Boolean> tTemp;
    private final List<Boolean> lTemp;

    // Necessary to achieve Level-ordered Bits
    // -> Recursive Assembly-Flow would naturally append in a depth-first Order
    private final Map<Integer, List<Boolean>> tLevels;
    private int maxTLevel;

    private int k;
    private K2TreeBuilder() {
        tTemp = new ArrayList<>();
        lTemp = new ArrayList<>();
        tLevels = new HashMap<>();
    }
    public K2Tree constructK2(int k, List<Cell> cells, int initialMatrixSize) {

        K2Tree tree = new K2Tree(k);

        assembleK2(tree, cells, 0, 0, initialMatrixSize, true);
        assembleBitStrings(tree);

        return tree;
    }
    private void assembleK2(K2Tree tree, List<Cell> currentCells,  int rowStart, int colStart, int matrixSize, boolean skipBit) {
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

    public void setT(int matrixSize, boolean val) {
        if (matrixSize > maxTLevel) { maxTLevel = matrixSize; }
        tLevels.computeIfAbsent(matrixSize, x -> new ArrayList<>()).add(val);
    }
    public void setL(boolean val) {
        // L-Bits as opposed to T-Bits need to be directly appended when encountered to keep the correct Order
        lTemp.add(val);
    }
    public void assembleBitStrings(K2Tree tree) {
        // First assemble final TTemp Bits in the correct Order
        for (int lvl = maxTLevel; lvl > 0; lvl = lvl / k) {
            List<Boolean> bits = tLevels.get(lvl);
            if (bits != null) {
                tTemp.addAll(bits);
            }
        }
        tree.t() = new BitString(tTemp);
        tree.l() = new BitString(lTemp);
    }
}
