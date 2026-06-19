package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.*;

public class DK2Tree implements K2 {

    private final int k;
    private final int matrixSize;
    private final DynamicBitVector tTree;
    private final DynamicBitVector lTree;

    public DK2Tree(DynamicBitVector tTree, DynamicBitVector lTree, int k, int matrixSize) {
        this.k = k;
        this.matrixSize = matrixSize;
        this.tTree = tTree;
        this.lTree = lTree;
    }

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
                // Call rank1 with + 1 to counter its exclusive Upper Bound
                base = rank1(tTree, currentBitIndex + 1) * (k * k);

            } else {
                // Base Offset needs to be 0 for first Cycle
                base = 0;
            }

            // Leave reached
            if (subSize == 1) {
                // Obtain L-Index by treating T and L as continuous and shifting the Index by T`s Length
                int lIdx = (base + child) - tTree.size();
                return access(lTree, lIdx) == 1;

            } else {
                // Skip Traversal when encountering a Node without Children
                if (access(tTree, base + child) == 0) {
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

    public FindLeafResult findTLeaf(DynamicBitVector tree, int p) {
        return checkNode(
                p,
                new FindLeafResult(tree.root(), 0, 0));
    }

    private FindLeafResult checkNode(int p, FindLeafResult res) {
        int bBefore = res.bBefore();
        int oBefore  = res.oBefore();
        if (res.node() instanceof LeafNode) {
            return res;
        }
        for (Entry e : ((InternalNode) res.node()).entries()) {
            if (p < e.b() + bBefore) {
                return checkNode(
                        p,
                        new FindLeafResult(e.p(), bBefore, oBefore)
                );
            }
            bBefore += e.b();
            oBefore += e.o();
        }
        return null;
    }

    private int rank1(DynamicBitVector b, int i) {
        FindLeafResult res = findTLeaf(b, i);
        LeafNode leaf = (LeafNode) res.node();
        return res.oBefore() + leaf.bits().rank1(i - res.bBefore());
    }

    private int access(DynamicBitVector b, int i) {
        FindLeafResult res = findTLeaf(b, i);
        LeafNode leaf = (LeafNode) res.node();
        return leaf.bits().access(i - res.bBefore());
    }
}
