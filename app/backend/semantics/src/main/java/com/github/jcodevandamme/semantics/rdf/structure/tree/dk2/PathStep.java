package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.LeafNode;

public class PathStep {
    public final DynamicBitVector tree;
    public final LeafNode leafNode;
    public final int localIndex;
    public final int globalIndex;
    public final boolean isLTree;
    public final boolean bitValue;

    public PathStep(DynamicBitVector tree, LeafNode leafNode, int localIndex, int globalIndex, boolean isLTree, boolean bitValue) {
        this.tree = tree;
        this.leafNode = leafNode;
        this.localIndex = localIndex;
        this.globalIndex = globalIndex;
        this.isLTree = isLTree;
        this.bitValue = bitValue;
    }
}