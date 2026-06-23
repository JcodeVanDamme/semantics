package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

public class Entry {

    public Entry(Node child) {
        if (child instanceof LeafNode) {
            b = child.size();
            o = ((LeafNode) child).bits().countSetBits();
        } else {
            b = 0;
            o = 0;
            for (Entry e : ((InternalNode) child).entries()) {
                b += e.b();
                o += e.o();
            }
        }
        p = child;
    }

    // Leave Node -> Number of Bits stored in this Leave
    // Internal Node -> Sum of b-Counters of Children
    private int b;

    // Leave Node -> Number of 1-Bits stored in this Leave
    // Internal Node -> Sum of o-Counters of Children
    private int o;

    // Pointer to Child Node of this Entry
    private Node p;

    public int b() { return b; }
    public int o() { return o; }
    public Node p() { return p; }

    public void updateB(int delta) {
        b += delta;
    }
    public void updateO(int delta) {
        o += delta;
    }

}
