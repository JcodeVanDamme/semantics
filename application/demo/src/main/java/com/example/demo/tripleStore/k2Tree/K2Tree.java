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
        String COLOR = "\u001B[95m";
        String RESET = "\u001B[0m";
        return
                COLOR + "T" + RESET + ": "
                + t.toString() + "\n"
                + COLOR + "L" + RESET + ": "
                + l.toString();
    }
}
