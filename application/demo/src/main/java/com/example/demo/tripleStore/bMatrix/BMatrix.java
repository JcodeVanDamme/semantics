package com.example.demo.tripleStore.bMatrix;

import com.example.demo.tripleStore.bitString.BitStringPredicate;
import com.example.demo.tripleStore.k2Tree.Cell;
import com.example.demo.tripleStore.k2Tree.K2Tree;
import com.example.demo.tripleStore.triple.Triple;

import java.util.*;

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

        List<Integer> objects = ot.rowQuery(o);

        if (objects.size() <= t) {

            for (int t : objects) {
                if (st.checkCell(s, t)) {
                    results.add(triples.get(t));
                }
            }
        } else {
            List<Integer> subjects = st.rowQuery(s);
            Set<Integer> intersection = new HashSet<>(objects);
            intersection.retainAll(subjects);

            for (int t : intersection) {
                results.add(triples.get(t));
            }
        }
        return results;
    }

    public List<Triple> s__(int s) {
        List<Triple> results = new ArrayList<>();

        List<Integer> subjectMatches = st.rowQuery(s);

        for (int t : subjectMatches) {
            Integer o = ot.columnQuery(t);
            if (o != null) {
                int p = bp.rank(true, t);
                results.add(new Triple(s, p, o));
            }
        }
        return results;
    }

    public List<Triple> __o(int o) {
        List<Triple> results = new ArrayList<>();

        List<Integer> objectMatches = ot.rowQuery(o);

        for (int t : objectMatches) {
            Integer s = st.columnQuery(t);
            if (s != null) {
                int p = bp.rank(true, t);
                results.add(new Triple(s, p, o));
            }
        }
        return results;
    }

    public List<Triple> _p_(int p) {
        List<Triple> results = new ArrayList<>();

        int lPredicateBound = bp.select(true, p);
        int uPredicateBound = bp.select(true, p + 1) - 1;

        List<Cell> subjects = st.boundedRangeQuery(lPredicateBound, uPredicateBound);

        if (subjects.size() <= t) {

            for (Cell c : subjects) {
                Integer res = ot.columnQuery(c.col());
                if (res != null) {
                    results.add(this.triples.get(c.col()));
                }
            }
        } else {
            List<Cell> objects = ot.boundedRangeQuery(lPredicateBound, uPredicateBound);
            subjects.sort(Comparator.comparingInt(Cell::col));
            objects.sort(Comparator.comparingInt(Cell::col));

            int si = 0;
            int oi = 0;
            while (si < subjects.size() && oi < objects.size()) {
                Cell sCell = subjects.get(si);
                Cell oCell = objects.get(oi);

                if (sCell.col() == oCell.col()) {
                    results.add(new Triple(sCell.row(), p, oCell.row()));
                    si++;
                    oi++;

                } else if (sCell.col() < oCell.col()) {
                    si++;

                } else {
                    oi++;
                }
            }
        }
        return results;
    }

    public List<Triple> ___() {
        return triples;
    }

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
