package com.example.demo.tripleStore.bMatrix;

import com.example.demo.tripleStore.bitString.BitStringPredicate;
import com.example.demo.tripleStore.k2Tree.K2Tree;
import com.example.demo.tripleStore.triple.Triple;

import java.util.List;

public class BMatrix {

    private final List<Triple> triples;
    private final TripleDictionary dict;
    private K2Tree st;
    private K2Tree ot;
    private final BitStringPredicate bp;

    public BMatrix(List<Triple> triples, TripleDictionary dict, K2Tree st, K2Tree ot, BitStringPredicate bp) {
        this.triples = triples;
        this.dict = dict;
        this.st = st;
        this.ot = ot;
        this.bp = bp;
        printTriples();
    }
    // Implement Queries here

    public void printTriples() {
        System.out.println("Triples:");
        StringBuilder strb = new StringBuilder();
        for (Triple t : triples) {
            strb.delete(0, strb.length());
            int s = (int) t.s();
            int p = (int) t.p();
            int o = (int) t.o();
            strb.append('(')
                    .append(t.s()).append(',')
                    .append(t.p()).append(',')
                    .append(t.o())
                    .append(") - ")
                    .append(dict.decodeSO(s)).append(',')
                    .append(dict.decodeP(p)).append(',')
                    .append(dict.decodeSO(o))
                    .append('\n');

            System.out.print(strb);
        }
        System.out.println("");
        System.out.println(bp);
    }
}
