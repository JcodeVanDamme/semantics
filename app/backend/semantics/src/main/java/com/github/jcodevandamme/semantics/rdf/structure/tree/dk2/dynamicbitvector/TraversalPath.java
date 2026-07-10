package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import java.util.ArrayDeque;
import java.util.Queue;

public class TraversalPath {

    public final Queue<Node> nodes;

    public TraversalPath() {
        nodes = new ArrayDeque<>();
    }
}
