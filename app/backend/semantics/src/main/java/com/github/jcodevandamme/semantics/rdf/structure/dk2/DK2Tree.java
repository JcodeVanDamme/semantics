package com.github.jcodevandamme.semantics.rdf.structure.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.DynamicBitVector;

public class DK2Tree {

    private final DynamicBitVector tTree;
    private final DynamicBitVector lTree;

    public DK2Tree(DynamicBitVector tTree, DynamicBitVector lTree) {
        this.tTree = tTree;
        this.lTree = lTree;
    }
}
