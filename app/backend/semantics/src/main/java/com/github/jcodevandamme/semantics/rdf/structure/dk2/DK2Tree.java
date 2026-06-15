package com.github.jcodevandamme.semantics.rdf.structure.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.*;

public class DK2Tree {

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
    
    public LeafNode findTLeaf(int i) {
        return checkNode(tTree.root(), i, 0, 0);
    }

    private LeafNode checkNode(Node node, int i, int bBefore, int oBefore) {
        if (node instanceof LeafNode) {
            return (LeafNode) node;
        }
        for (Entry e : ((InternalNode) node).entries()) {
            if (i < e.b() + bBefore) {
                return checkNode(e.p(), i, bBefore, oBefore);
            }
            bBefore += e.b();
            oBefore += e.o();
        }
        return null;
    }
}
