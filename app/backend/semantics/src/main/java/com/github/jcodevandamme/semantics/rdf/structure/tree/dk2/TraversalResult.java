package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.LeafNode;

public record TraversalResult(LeafNode leafNode, int localTargetIndex, boolean leafIsInL, LeafNode parentTLeafNode, Integer parentLLeafIndex) {
}
