package com.github.jcodevandamme.semantics.rdf.structure.k2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.NaiveBitString;
import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.SuxBitString;

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

    public K2TreeBuilder() {
        tTemp = new ArrayList<>();
        lTemp = new ArrayList<>();
        tLevels = new HashMap<>();
    }
    public K2Tree constructK2(int k, List<Cell> cells, int initialMatrixSize) {
        tTemp.clear();
        lTemp.clear();
        tLevels.clear();
        maxTLevel = 0;

        this.k = k;
        assembleK2(cells, 0, 0, initialMatrixSize, true);
        return assembleBitStrings(initialMatrixSize);
    }
    private void assembleK2(List<Cell> currentCells,  int rowStart, int colStart, int matrixSize, boolean skipBit) {
        // Leave Reached
        if (matrixSize == 1) {
            if (!currentCells.isEmpty()) {
                Cell c = currentCells.get(0);
                if (c.row() == rowStart && c.col() == colStart) {
                    // Set next Bit in L to 1
                    setL(true);
                    return;
                }
            }
            // Set next Bit in L to 0
            setL(false);
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
            setT(matrixSize, match);
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

                assembleK2(childCells, newRowStart, newColStart, childSize, false);
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
    private K2Tree assembleBitStrings(int matrixSize) {
        // First assemble final TTemp Bits in the correct Order
        for (int lvl = maxTLevel; lvl > 0; lvl = lvl / k) {
            List<Boolean> bits = tLevels.get(lvl);
            if (bits != null) {
                tTemp.addAll(bits);
            }
        }
        SuxBitString t = new SuxBitString(tTemp);
        SuxBitString l = new SuxBitString(lTemp);
        return new K2Tree(k,matrixSize, t, l);
    }

    public String tempToString() {
        StringBuilder strb = new StringBuilder();
        strb.append("T: ");
        generateString(strb, tTemp);
        strb.append("\nL: ");
        generateString(strb, lTemp);
        return strb.toString();
    }
    private void generateString(StringBuilder strb, List<Boolean> b) {
        for (int i = 0; i < b.size(); i ++) {
            String bit = b.get(i) ? "1" : "0";
            strb.append(bit);
            if ((i + 1) % (k*k) == 0) {
                strb.append(' ');
            }
        }
    }
}