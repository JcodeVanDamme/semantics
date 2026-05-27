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

    public boolean spo(int s, int p, int o) {
        int lBound = bp.select(true, p);
        int uBound = bp.select(true, p + 1) - 1;
        return true;
    }
    public List<Triple> sp_(int s, int p) {

        return null;
    }

    public List<Triple> _po(int p, int o) {

        return null;
    }
    public List<Triple> s_o(int s, int o) {

        return null;
    }
    public List<Triple> s__(int s) {

        return null;
    }
    public List<Triple> __o(int o) {

        return null;
    }
    public List<Triple> _p_(int p) {

        return null;
    }
    public List<Triple> ___() {

        return null;
    }

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
