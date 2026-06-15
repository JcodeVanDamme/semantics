package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import java.util.ArrayList;
import java.util.List;

public class InternalNode extends Node {

    // Internal Nodes store Entries of the Form (b, o, p)
    private final List<Entry> entries;
    public InternalNode(int minCapacity, int maxCapacity) {
        super(minCapacity, maxCapacity);
        entries = new ArrayList<>();
    }
    @Override
    public int size() {
        return entries.size();
    }
    public List<Entry> entries() { return entries; }
    public void add(Node node) {
        assert size() < maxCapacity();
        entries.add(new Entry(node));
    }
}
