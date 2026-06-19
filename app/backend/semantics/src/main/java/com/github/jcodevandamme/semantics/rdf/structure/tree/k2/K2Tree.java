package com.github.jcodevandamme.semantics.rdf.structure.tree.k2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;

public class K2Tree implements K2 {

    private final int k;
    private final int matrixSize;
    private final BitInterface t;
    private final BitInterface l;

    public K2Tree(int k,int matrixSize, BitInterface t, BitInterface l) {
        this.k = k;
        this.matrixSize = matrixSize;
        this.t = t;
        this.l = l;
    }
    public int k() { return k; }
    public BitInterface t() { return t; }
    public BitInterface l() { return l; }
    @Override
    public int matrixSize() {
        return matrixSize;
    }
    @Override
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
                base = t.rank1(currentBitIndex + 1) * (k * k);
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
