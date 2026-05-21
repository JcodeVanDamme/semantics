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
    }
    // Implement Queries here

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb
            .append("------------------------- ")
            .append("Triples")
            .append(" -------------------------\n");

        for (Triple t : triples) {
            int s = (int) t.s();
            int p = (int) t.p();
            int o = (int) t.o();

            strb
                .append('(')
                .append(s).append(", ")
                .append(p).append(", ")
                .append(o).append(") - ")
                .append(dict.decodeSO(s)).append(" | ")
                .append(dict.decodeP(p)).append(" | ")
                .append(dict.decodeSO(o))
                .append('\n');
        }

        strb
            .append("---------------------------- ")
            .append("ST")
            .append(" ---------------------------\n")
            .append(st).append("\n");

        strb
            .append("---------------------------- ")
            .append("OT")
            .append(" ---------------------------\n")
            .append(ot).append("\n");

        strb
            .append("---------------------------- ")
            .append("BP")
            .append(" ---------------------------\n")
            .append(bp).append("\n");

        strb.append("-----------------------------------------------------------");

        return strb.toString();
    }
}
