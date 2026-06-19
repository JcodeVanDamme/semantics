package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;

public class LeafNode extends Node{

    // Leaf Nodes store original Bit(-chunks)
    private BitInterface bits;
    public LeafNode(int minCapacity, int maxCapacity, BitInterface bits) {
        super(minCapacity, maxCapacity);
        this.bits = bits;
    }
    @Override
    public int size() {
        return bits.size();
    }
    public BitInterface bits() { return bits; }
}
