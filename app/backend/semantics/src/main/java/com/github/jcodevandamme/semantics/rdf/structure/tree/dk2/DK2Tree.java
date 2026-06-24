package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.*;

import java.util.ArrayList;
import java.util.List;

public class DK2Tree implements K2 {

    private final int k;
    private final int matrixSize;
    private final DynamicBitVector tTree;
    private final DynamicBitVector lTree;

    private int currentColumnIndex;
    private List<Integer> freedColumns;

    public DK2Tree(DynamicBitVector tTree, DynamicBitVector lTree, int k, int matrixSize, int numberOfSetColumns) {
        this.k = k;
        this.matrixSize = matrixSize;
        this.tTree = tTree;
        this.lTree = lTree;


        currentColumnIndex = numberOfSetColumns;
        freedColumns = new ArrayList<>();
    }

    @Override
    public boolean addEntry(int row, int col) {
        return true;
    }

    @Override
    public boolean removeEntry(int row, int col) {
        return true;
    }

    @Override
    public boolean update(int removeRow, int removeCol, int addRow, int addCol) {
        return true;
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
        if (!res.leafIsInL()) {
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
            if (!res.leafIsInL()) {
                expandTree(res, row, col);
            }
        } else {
            // Target Cell was found in L
            // -> No need to
            if (res.leafIsInL()) {
                reduceTree(res, row, col);
            }
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

            if (match.leafIsInL()) {
                found = true;

            } else {
                childIdx = rank1(tTree, match.localTargetIndex() * (k * k));
                tTree.addK2Bits(match.leafNode(), k, childIdx);
            }
        }
    }
    private void reduceTree(TraversalResult match, int row, int col) {
        LeafNode lLeaf = match.leafNode();
        int lIdx = match.localTargetIndex();
        LeafNode tLeaf = match.parentTLeafNode();
        int tIdx = match.parentLLeafIndex();

        if (checkK2SiblingBits(lLeaf.bits(), lIdx)) {
            return;
        }

        /*
       lTree.removeK2Bits(lLeaf, k, lIdx);

        /*
        if (lLeaf.bits().size() > 0) {
            return;
        }
        // Remove Leaf Node from Parent Nodes Entries
        lLeaf.parent().entries().remove(lLeaf.indexInParent());

        /*

        // Unset Bit in T which pointed to LLeaf
        DynamicBitVector.set(false, tLeaf, tIdx);

        if (!checkK2SiblingBits(tLeaf.bits(), tIdx)) {
            tLeaf.parent().entries().remove(tLeaf.indexInParent());
        }*/
    }

    private boolean checkK2SiblingBits(BitInterface bits, int blockStartIdx) {
        int end = Math.min(bits.size(), k*k);
        for (int i = 0; i < end; i++) {
            if (bits.access(blockStartIdx + i) == 1) {
                return true;
            }
        }
        return false;
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

                // Retrieve the Leaf in T corresponding to the following Leaf in L
                FindLeafResult tRes = findLeaf(tTree, currentBitIndex);

                // Obtain L-Index by treating T and L as continuous and shifting the Index by T`s Length
                int lIdx = idx - tTree.size();
                FindLeafResult lRes = findLeaf(lTree, lIdx);

                return new TraversalResult(
                        (LeafNode) lRes.node(),
                        // idx needs to be offsetted by bBefore to correctly align (Dont ask me why)
                        lIdx - lRes.bBefore(),
                        true,
                        (LeafNode) tRes.node(),
                        // Index of Bit in Parent Node correlating to Leaf L
                        currentBitIndex - tRes.bBefore()
                );

            } else {

                // Skip Traversal when encountering a Node without Children
                if (access(tTree, idx) == 0) {

                    FindLeafResult res = findLeaf(tTree, idx);
                    return new TraversalResult(
                            (LeafNode) res.node(),
                            // idx needs to be offsetted by bBefore to correctly align (Dont ask me why)
                            idx - res.bBefore(),
                            false,
                            null,
                            null
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
