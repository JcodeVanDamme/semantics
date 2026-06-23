package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

public abstract class Node {

    // Internal -> Current Number of Entries
    // Leave -> Number of stored Bits
    private int size;

    // Range of supported Number of Entries in this Node
    private int minCapacity;
    private int maxCapacity;

    private InternalNode parent;


    private int indexInParent;

    public Node(int minCapacity, int maxCapacity) {
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        size = 0;
    }
    public abstract int size();
    public int minCapacity() { return minCapacity; }
    public int maxCapacity() { return maxCapacity; }

    public void setParent(InternalNode parent, int indexInParent) {
        this.parent = parent;
        this.indexInParent = indexInParent;
    }
    public InternalNode parent() { return parent; }
    public int indexInParent() { return indexInParent; }
}
