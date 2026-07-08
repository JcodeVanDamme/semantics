package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.*;

import java.util.ArrayList;
import java.util.List;

public class DK2Tree implements K2 {

    private final int k;
    private final DynamicBitVector tTree;
    private final DynamicBitVector lTree;
    private int matrixSize;

    private int currentColumnIndex;
    private final List<Integer> freedColumns;

    public DK2Tree(DynamicBitVector tTree, DynamicBitVector lTree, int k, int matrixSize, int numberOfSetColumns) {
        this.k = k;
        this.matrixSize = matrixSize;
        this.tTree = tTree;
        this.lTree = lTree;

        currentColumnIndex = numberOfSetColumns;
        freedColumns = new ArrayList<>();
    }

    @Override
    public int matrixSize() {
        return matrixSize;
    }

    @Override
    public boolean addEntry(int row, int col) {
        updateCell(row, col, true);
        return true;
    }

    @Override
    public boolean removeEntry(int row, int col) {
        updateCell(row, col, false);
        return true;
    }

    @Override
    public int getNextAvailableColumnIndex() {
        if (!freedColumns.isEmpty()) {
            return freedColumns.removeFirst();
        } else {
            if (currentColumnIndex == matrixSize - 1) {
                matrixSize *= k;
                tTree.expandRoot(k);
            }
            int index = currentColumnIndex;
            currentColumnIndex += 1;
            return index;
        }
    }

    @Override
    public boolean checkCell(int row, int col) {
        if (row >= this.matrixSize || col >= this.matrixSize) {
            return false;
        }

        List<PathStep> path = tracePath(row, col);
        PathStep lastStep = path.getLast();

        // Path ended in upper Levels of the Tree
        if (!lastStep.isLTree) {
            return false;
        }

        // Check Bit Value of Cell using local Index
        return lastStep.bitValue;
    }


    public List<PathStep> tracePath(int row, int col) {
        List<PathStep> path = new ArrayList<>();
        int currentBitIndex = 0;
        int currentMatrixSize = this.matrixSize;

        while (true) {
            int subSize = currentMatrixSize / k;
            int childRow = row / subSize;
            int childCol = col / subSize;
            int child = childRow * k + childCol;

            int base;
            if (currentMatrixSize < this.matrixSize) {
                // rank1 is exclusive upper bound
                base = rank1(tTree, currentBitIndex + 1) * (k * k);
            } else {
                base = 0;
            }

            int idx = base + child;

            if (subSize == 1) {
                // We have reached the L Tree
                int lIdx = idx - tTree.size();
                FindLeafResult lRes = findLeaf(lTree, lIdx);
                LeafNode lLeaf = (LeafNode) lRes.node();
                int localIdx = lIdx - lRes.bBefore();
                boolean bitValue = lLeaf.bits().access(localIdx) == 1;

                path.add(new PathStep(lTree, lLeaf, localIdx, lIdx, true, bitValue));
                return path; // Reached the absolute bottom

            } else {
                // We are still in the T Tree
                FindLeafResult tRes = findLeaf(tTree, idx);
                LeafNode tLeaf = (LeafNode) tRes.node();
                int localIdx = idx - tRes.bBefore();
                boolean bitValue = tLeaf.bits().access(localIdx) == 1;

                path.add(new PathStep(tTree, tLeaf, localIdx, idx, false, bitValue));

                if (!bitValue) {
                    // We hit a 0. The branch ends here.
                    // The conceptual tree has no physical representation below this point.
                    return path;
                }
            }

            // Update for the next level down
            row = row % subSize;
            col = col % subSize;
            currentBitIndex = idx; // Update to the specific bit we just evaluated
            currentMatrixSize = subSize;
        }
    }

    public void updateCell(int row, int col, boolean value) {
        // -> Path ends at the first 0 Bit encountered
        List<PathStep> path = tracePath(row, col);
        PathStep lastStep = path.getLast();

        if (value) {
            if (lastStep.bitValue) {
                // -> Cell is already 1, nothing to do
                return;
            }
            // The 0 is replaced with a 1
            // -> Automatically updates o-Counters
            DynamicBitVector.set(true, lastStep.leafNode, lastStep.localIndex);

            if (!lastStep.isLTree) {
                // New path must be created
                // -> pass the Depth so we know how big the current Submatrix is
                expandTree(lastStep.globalIndex, row, col, path.size());
            }

        } else {

            if (!lastStep.bitValue) {
                return; // Cell is already 0, nothing to do
            }
            // The 1 is replaced with a 0
            // -> Automatically updates the o-Counters
            DynamicBitVector.set(false, lastStep.leafNode, lastStep.localIndex);
            freedColumns.add(col);

            // Check the k 2 −1 bits corresponding to the siblings of that node.
            if (bitSetInK2Siblings(lastStep.leafNode, lastStep.localIndex)) {
                return;
            }
            // Siblings empty; Tree needs pruning
            reduceTree(path);
        }
    }

    private void expandTree(int splitGlobalIdx, int targetRow, int targetCol, int currentDepth) {
        // Calculate the sub-matrix size at the level of the flipped bit
        int currentSubSize = this.matrixSize;
        for (int i = 0; i < currentDepth; i++) {
            currentSubSize /= k;
        }

        int parentGlobalIdx = splitGlobalIdx;

        int localRow = targetRow % currentSubSize;
        int localCol = targetCol % currentSubSize;

        while (currentSubSize > 1) {
            boolean isNextLevelL = (currentSubSize == k);
            DynamicBitVector targetTree = isNextLevelL ? lTree : tTree;

            // "A rank operation is performed to compute the position where its children should be located"
            int childBlockStartIdx = rank1(tTree, parentGlobalIdx + 1) * (k * k);

            // 3. FIX: Map global index space to local lTree index space if targeting L
            if (isNextLevelL) {
                childBlockStartIdx -= tTree.size();
            }

            // "Then k^2 0 bits are added as children"
            FindLeafResult insertLeafRes = findLeaf(targetTree, childBlockStartIdx);
            targetTree.addK2Bits((LeafNode) insertLeafRes.node(), k, childBlockStartIdx - insertLeafRes.bBefore());

            int parentBlockSize = currentSubSize;
            int childBlockSize = currentSubSize / k;

            // 4. Cleaned up coordinate tracking using the outer scope variables
            int childRow = localRow / childBlockSize;
            int childCol = localCol / childBlockSize;
            int targetChildOffset = childRow * k + childCol;

            int targetBitGlobalIdx = childBlockStartIdx + targetChildOffset;

            // Flip that specific covering child bit to 1
            FindLeafResult bitLeafRes = findLeaf(targetTree, targetBitGlobalIdx);
            DynamicBitVector.set(true, (LeafNode) bitLeafRes.node(), targetBitGlobalIdx - bitLeafRes.bBefore());
            DynamicBitVector.set(true, (LeafNode) bitLeafRes.node(), targetBitGlobalIdx - bitLeafRes.bBefore());

            // "The procedure continues recursively until it reaches the last level"
            if (isNextLevelL) {
                break; // Reached L tree, expansion complete!
            }

            // Prepare for the next level down safely
            parentGlobalIdx = targetBitGlobalIdx;
            localRow = localRow % childBlockSize;
            localCol = localCol % childBlockSize;
            currentSubSize /= k;
        }
    }

    private boolean bitSetInK2Siblings(LeafNode leaf, int localIndex) {
        int blockSize = k*k;
        int blockStartIdx = (localIndex / blockSize) * blockSize;
        int blockEndIdx = blockStartIdx + blockSize;

        for (int i = blockStartIdx; i < blockEndIdx; i++) {
            if (leaf.bits().access(i) == 1) {
                return true;
            }
        }
        return false;
    }

    private void reduceTree(List<PathStep> path) {
        int blockSize = k * k;

        PathStep lastStep = path.getLast();
        DynamicBitVector targetTree = lastStep.isLTree ? lTree : tTree;

        int blockStartIdx = (lastStep.localIndex / blockSize) * blockSize;
        targetTree.removeK2Bits(lastStep.leafNode, k, blockStartIdx);

        if (lastStep.leafNode.size() == 0) {
            pruneEmptyNodesUpwards(lastStep.leafNode);
        }

        for (int depth = path.size() - 2; depth >= 0; depth--) {
            PathStep parentStep = path.get(depth);

            // Replace the Parents 1 bit with a 0
            DynamicBitVector.set(false, parentStep.leafNode, parentStep.localIndex);

            // Check if the parent's siblings are also all 0 now
            if (bitSetInK2Siblings(parentStep.leafNode, parentStep.localIndex)) {
                break;
            }

            // All Siblings 0, this parent block dead
            // --> Prune it
            int parentBlockStartIdx = (parentStep.localIndex / blockSize) * blockSize;
            tTree.removeK2Bits(parentStep.leafNode, k, parentBlockStartIdx);

            if (parentStep.leafNode.size() == 0) {
                pruneEmptyNodesUpwards(parentStep.leafNode);
            }
        }
    }

    private void pruneEmptyNodesUpwards(Node node) {
        Node current = node;
        InternalNode parent = node.parent();

        while (parent != null) {
            parent.entries().remove(current.indexInParent());
            DynamicBitVector.reindexChildren(parent);
            if (parent.size() > 0) {
                break;
            }
            current = parent;
            parent = current.parent();
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

        List<Entry> entries = ((InternalNode) node).entries();
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);

            // Follow the child if 'p' falls inside it, OR if 'p' is exactly at the
            // boundary of the very last entry (meaning we are appending to the end of the tree)
            if (p < e.b() + bBefore || (i == entries.size() - 1)) {
                return checkNode(
                        p,
                        new FindLeafResult(e.p(), bBefore, oBefore)
                );
            }
            bBefore += e.b();
            oBefore += e.o();
        }

        return null; // Should only hit this if p > total size of the tree
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
