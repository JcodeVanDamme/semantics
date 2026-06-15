package com.github.jcodevandamme.semantics.rdf.structure.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.dk2.dynamicbitvector.*;
import com.github.jcodevandamme.semantics.rdf.structure.k2.K2Tree;

public class DK2Builder {

    /**
     * Generates a dynamic DK2-tree from a static K2-tree.
     *
     * <p> This process converts the static bitmap representation (T and L)
     * into a dynamic structure with explicit nodes open for modifications. </p>
     *
     * @param staticTree static K2-tree to generate the DK2-tree from
     * @param chunkSize size of bit chunks used to partition the original
     *                  T and L bitstrings; determines the size of leaf nodes
     *                  in the resulting DynamicBitVector trees
     * @param minCapacity minimum number of entries allowed in internal nodes
     * @param maxCapacity maximum number of entries allowed in internal nodes
     * @return the generated DK2-tree
     */
    public static DK2Tree build(K2Tree staticTree, int chunkSize, int minCapacity, int maxCapacity) {
        DynamicBitVector tTree = DynamicBitVectorBuilder.build(staticTree.t(), chunkSize, minCapacity, maxCapacity);
        DynamicBitVector lTree = DynamicBitVectorBuilder.build(staticTree.l(), chunkSize, minCapacity, maxCapacity);
        return new DK2Tree(tTree, lTree, staticTree.k(), staticTree.matrixSize());
    }
}
