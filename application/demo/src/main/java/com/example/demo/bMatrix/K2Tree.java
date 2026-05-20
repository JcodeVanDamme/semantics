package com.example.demo.bMatrix;

import java.util.*;

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
        return "T:" +
                t.toString() +
                "\nL:" +
                l.toString();
    }
}
