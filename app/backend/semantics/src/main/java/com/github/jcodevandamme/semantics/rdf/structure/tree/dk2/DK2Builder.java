package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;
import com.github.jcodevandamme.semantics.rdf.structure.tree.k2.K2Tree;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVector;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorBuilder;

public class DK2Builder {

    /**
     * Generates a dynamic DK2-tree from a static K2-tree.
     *
     * <p> This process converts the static bitmap representation (T and L)
     * into a dynamic structure with explicit nodes open for modifications. </p>
     *
     * @param staticTree static K2-tree to generate the DK2-tree from
     * @param config Configuration Parameters for the Tree
     * @return the generated DK2-tree
     */
    public static DK2Tree build(K2Tree staticTree, DynamicBitVectorConfiguration config, int numberOfSetColumns) {
        DynamicBitVector tTree = DynamicBitVectorBuilder.build(
                staticTree.t(),
                config
        );
        DynamicBitVector lTree = DynamicBitVectorBuilder.build(
                staticTree.l(),
                config
        );
        return new DK2Tree(tTree, lTree, staticTree.k(), staticTree.matrixSize(), numberOfSetColumns);
    }

    /**
     * Initializes an empty dynamic DK2-tree.
     *
     * @param config Configuration Parameters for the Tree
     * @param k Subdivision Factor of the Conceptual Matrix
     * @return the generated DK2-tree
     */
    public static DK2Tree build(DynamicBitVectorConfiguration config, int k) {
        return new DK2Tree(
                DynamicBitVectorBuilder.build(config),
                DynamicBitVectorBuilder.build(config),
                k,
                k*k,
                0);
    }
}
