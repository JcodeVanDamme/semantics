package com.example.demo.tripleStore.k2Tree;

import com.example.demo.tripleStore.bitString.BitString;

public class K2Tree {

    private final BitString t;
    private final BitString l;
    public K2Tree(BitString t, BitString l) {
        this.t = t;
        this.l = l;
    }
    public BitString getT() { return t; }
    public BitString getL() { return l; }

    @Override
    public String toString() {
        return
                "T: "
                + t.toString() + "\n"
                + "L: "
                + l.toString();
    }
}
