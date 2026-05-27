package com.example.demo.tripleStore.k2Tree;

import com.example.demo.tripleStore.bitString.BitString;

public class K2Tree {

    private final int k;
    private final int matrixSize;
    private final BitString t;
    private final BitString l;

    public int matrixSize() { return matrixSize; }

    public K2Tree(int k,int matrixSize, BitString t, BitString l) {
        this.k = k;
        this.matrixSize = matrixSize;
        this.t = t;
        this.l = l;
    }
    public boolean checkCell(int row, int col) {
        int currentBitIndex = 0;
        int matrixSize = this.matrixSize;

        while (true) {
            // Determine Quadrant containing (row, col) in Relation to the current Submatrix
            int subSize = matrixSize / k;
            int childRow = row / subSize;
            int childCol = col / subSize;
            // Obtain Child Index and map 2D-Coords into BitMap Index using Row-Major-Transformation
            // (Black-Magic-Fuckery)
            int child = childRow * k + childCol;

            // Obtain Base Offset for the continuous Block of currentBitIndex`s Children
            int base;
            if (matrixSize < this.matrixSize) {
                // Call rank with + 1 to counter its exclusive Upper Bound
                base = t.rank(true, currentBitIndex + 1) * (k * k);
            } else {
                // Base Offset needs to be 0 for first Cycle
                base = 0;
            }

            // Leave reached
            if (subSize == 1) {
                // Obtain L-Index by treating T and L as continuous and shifting the Index by T`s Length
                int lIdx = (base + child) - t.size();
                return l.access(lIdx) == 1;

            } else {
                // Skip Traversal when encountering a Node without Children
                if (t.access(base + child) == 0) {
                    return false;
                }
            }
            // Update target row / col to account for the now smaller Scope
            row = row % subSize;
            col = col % subSize;
            currentBitIndex = base + child;
            matrixSize = subSize;
        }
    }

    @Override
    public String toString() {
        return
                "T: "
                + t.toString() + "\n"
                + "L: "
                + l.toString();
    }
}
