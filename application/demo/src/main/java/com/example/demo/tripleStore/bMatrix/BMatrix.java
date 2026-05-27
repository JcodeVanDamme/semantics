package com.example.demo.tripleStore.bMatrix;

import com.example.demo.tripleStore.bitString.BitStringPredicate;
import com.example.demo.tripleStore.k2Tree.K2Tree;
import com.example.demo.tripleStore.triple.Triple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BMatrix {

    private final List<Triple> triples;
    private final BitStringPredicate bp;
    private final int t;

    private K2Tree st;
    private K2Tree ot;

    public BMatrix(List<Triple> triples, K2Tree st, K2Tree ot, BitStringPredicate bp, int t) {
        this.triples = triples;
        this.st = st;
        this.ot = ot;
        this.bp = bp;
        this.t = t;
    }

    public boolean spo(int s, int p, int o) {
        int lPredicateBound = bp.select(true, p);
        int uPredicateBound = bp.select(true, p + 1) - 1;
        List<Integer> triples = st.boundedRowQuery(s, lPredicateBound, uPredicateBound);
        for (int t : triples) {
            if (ot.checkCell(o, t)) {
                return true;
            }
        }
        return false;
    }
    public List<Triple> sp_(int s, int p) {
        List<Triple> results = new ArrayList<>();
        int lPredicateBound = bp.select(true, p);
        int uPredicateBound = bp.select(true, p + 1) - 1;
        List<Integer> triples = st.boundedRowQuery(s, lPredicateBound, uPredicateBound);
        for (int t : triples) {
            Integer res = ot.columnQuery(t);
            if (res != null) {
                results.add(this.triples.get(t));
            }
        }
        return results;
    }

    public List<Triple> _po(int p, int o) {
        List<Triple> results = new ArrayList<>();
        int lPredicateBound = bp.select(true, p);
        int uPredicateBound = bp.select(true, p + 1) - 1;
        List<Integer> triples = ot.boundedRowQuery(o, lPredicateBound, uPredicateBound);
        for (int t : triples) {
            Integer res = st.columnQuery(t);
            if (res != null) {
                results.add(this.triples.get(t));
            }
        }
        return results;
    }

    public List<Triple> s_o(int s, int o) {
        List<Triple> results = new ArrayList<>();
        List<Integer> objectMatches = ot.rowQuery(o);
        if (objectMatches.size() <= t) {
            for (int t : objectMatches) {
                if (st.checkCell(s, t)) {
                    results.add(triples.get(t));
                }
            }
        } else {
            List<Integer> subjectMatches = st.rowQuery(s);
            Set<Integer> intersection = new HashSet<>(objectMatches);
            intersection.addAll(subjectMatches);
            for (int t : intersection) {
                results.add(triples.get(t));
            }
        }
        return results;
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
        return triples;
    }git

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append("------------------------- ")
            .append("Triples")
            .append(" -------------------------\n");

        for (Triple t : triples) {
            int s = (int) t.s();
            int p = (int) t.p();
            int o = (int) t.o();

            strb.append('(')
                .append(s).append(", ")
                .append(p).append(", ")
                .append(o).append(")")
                .append('\n');
        }

        strb.append("---------------------------- ")
            .append("ST")
            .append(" ---------------------------\n")
            .append(st).append("\n");

        strb.append("---------------------------- ")
            .append("OT")
            .append(" ---------------------------\n")
            .append(ot).append("\n");

        strb.append("---------------------------- ")
            .append("BP")
            .append(" ---------------------------\n")
            .append(bp).append("\n");

        return strb.toString();
    }
}
