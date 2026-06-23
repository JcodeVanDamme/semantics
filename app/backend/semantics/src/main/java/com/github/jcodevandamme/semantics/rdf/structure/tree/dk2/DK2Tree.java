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
        // res contains the last leafNode encountered while traversing the tree to target (row, col)
        TraversalResult res = findNode(row, col);

        // Path ended in upper Levels of the Tree
        if (!res.leafInL()) {
            return false;
        }

        // Check Bit Value of Cell using local Index
        return res.leafNode().bits().access(res.localTargetIndex()) == 1;
    }

    public void updateCell(int row, int col, boolean value) {
        TraversalResult res = findNode(row, col);
        // Update found Cell
        DynamicBitVector.set(value, res.leafNode(), res.localTargetIndex());

        if (value) {
            // Target Cell was found in T
            // -> Expand Tree to include target Cell
            if (!res.leafInL()) {
                expandTree(res, row, col);
            }
        } else {
            // Target Cell was found in L
            // -> No need to
            if (res.leafInL()) {
                reduceTree(res, row, col);
            }
        }
    }
    private void reduceTree(TraversalResult match, int targetRow, int targetCol) {
        // Leaf has become irrelevant after update
        if (match.leafNode().bits().countSetBits() == 0) {

            InternalNode parent = match.leafNode().parent();
            parent.remove(match.leafNode());
        }

    }
    private void expandTree(TraversalResult match, int targetRow, int targetCol) {
        // Path to Cell ended in upper Tree
        // -> set relevant Bit in T Leaf and expand Tree
        int childIdx = rank1(tTree, match.localTargetIndex()) * (k * k);
        tTree.addK2Bits(match.leafNode(), k, childIdx);

        // Continue Traversal to target Cell and keep expanding the Tree
        // until Leaf in L was reached
        // -> then update
        boolean found = false;
        while (!found) {

            match = findNode(targetRow, targetCol);
            DynamicBitVector.set(true, match.leafNode(), match.localTargetIndex());

            if (match.leafInL()) {
                found = true;

            } else {
                childIdx = rank1(tTree, match.localTargetIndex() * (k * k));
                tTree.addK2Bits(match.leafNode(), k, childIdx);
            }
        }
    }

    public TraversalResult findNode(int row, int col) {
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

            int idx = base + child;

            // Leaf reached
            if (subSize == 1) {

                // Obtain L-Index by treating T and L as continuous and shifting the Index by T`s Length
                idx -= tTree.size();

                FindLeafResult res = findLeaf(lTree, idx);
                return new TraversalResult(
                        (LeafNode) res.node(),
                        // idx needs to be offsetted by bBefore to correctly align (Dont ask me why)
                        idx - res.bBefore(),
                        true);

            } else {

                // Skip Traversal when encountering a Node without Children
                if (access(tTree, idx) == 0) {

                    FindLeafResult res = findLeaf(tTree, idx);
                    return new TraversalResult(
                            (LeafNode) res.node(),
                            // idx needs to be offsetted by bBefore to correctly align (Dont ask me why)
                            idx - res.bBefore(),
                            false
                    );
                }
            }

            // Update target row / col to account for the now smaller Scope
            row = row % subSize;
            col = col % subSize;
            currentBitIndex = base + child;
            matrixSize = subSize;
        }
    }

    // Returns the Leaf Node corresponding to the Bit Index p
    public FindLeafResult findLeaf(DynamicBitVector tree, int p) {
        return checkNode(
                p,
                new FindLeafResult(tree.root(), 0, 0));
    }

    // Continuous traversing the tree according to Index p until a Leaf is found
    private FindLeafResult checkNode(int p, FindLeafResult res) {
        int bBefore = res.bBefore();
        int oBefore  = res.oBefore();
        Node node = res.node();

        if (node instanceof LeafNode) {
            return res;
        }
        for (Entry e : ((InternalNode) node).entries()) {
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
        FindLeafResult res = findLeaf(b, i);
        LeafNode leaf = (LeafNode) res.node();
        return res.oBefore() + leaf.bits().rank1(i - res.bBefore());
    }

    private int access(DynamicBitVector b, int i) {
        FindLeafResult res = findLeaf(b, i);
        LeafNode leaf = (LeafNode) res.node();
        return leaf.bits().access(i - res.bBefore());
    }

    @Override
    public String toString() {
        return
            "T:\n" + tTree + "\n" +
            "L:\n" + lTree;
    }
}
