package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.structure.index.StaticPredicateIndex;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;

import java.util.*;

public class StaticBMatrix implements BMatrix{

    private final List<Triple> triples;
    private final StaticPredicateIndex bp;
    private final int t;

    private K2 st;
    private K2 ot;

    public StaticBMatrix(List<Triple> triples, K2 st, K2 ot, StaticPredicateIndex bp, int t) {
        this.triples = triples;
        this.st = st;
        this.ot = ot;
        this.bp = bp;
        this.t = t;
    }

    public boolean add(int s, int p, int o) throws IllegalArgumentException {
        if (spoQuery(s, p, o)) {
            throw new IllegalArgumentException("Triple to be created already exists");
        }

        return true;
    }
    public boolean delete(int s, int p, int o) throws IllegalArgumentException {
        if (!spoQuery(s, p, o)) {
            throw new IllegalArgumentException("Triple to be deleted does not exist");
        }

        return true;
    }
    public boolean update(int oldS, int oldP, int oldO, int newS, int newP, int newO) throws IllegalArgumentException {
        if (!spoQuery(oldS, oldP, oldO)) {
            throw new IllegalArgumentException("Triple to be updated does not exist");
        } else if (spoQuery(newS, newP, newO)) {
            throw new IllegalArgumentException("Triple to be deleted does not exist");
        }

        delete(oldS, oldP, oldO);
        add(newS, newP, newO);

        return true;
    }

    public boolean spoQuery(int s, int p, int o) {
        int lPredicateBound = bp.select1(p);
        int uPredicateBound = bp.select1(p + 1) - 1;

        List<Integer> triples = st.boundedRowQuery(s, lPredicateBound, uPredicateBound);

        for (int t : triples) {
            if (ot.checkCell(o, t)) {
                return true;
            }
        }
        return false;
    }
    public List<Triple> sp_Query(int s, int p) {
        List<Triple> results = new ArrayList<>();

        int lPredicateBound = bp.select1(p);
        int uPredicateBound = bp.select1(p + 1) - 1;

        List<Integer> triples = st.boundedRowQuery(s, lPredicateBound, uPredicateBound);

        for (int t : triples) {
            Integer res = ot.columnQuery(t);
            if (res != null) {
                results.add(this.triples.get(t));
            }
        }
        return results;
    }

    public List<Triple> _poQuery(int p, int o) {
        List<Triple> results = new ArrayList<>();

        int lPredicateBound = bp.select1(p);
        int uPredicateBound = bp.select1(p + 1) - 1;

        List<Integer> triples = ot.boundedRowQuery(o, lPredicateBound, uPredicateBound);

        for (int t : triples) {
            Integer res = st.columnQuery(t);
            if (res != null) {
                results.add(this.triples.get(t));
            }
        }
        return results;
    }

    public List<Triple> s_oQuery(int s, int o) {
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

    public List<Triple> s__Query(int s) {
        List<Triple> results = new ArrayList<>();

        List<Integer> subjectMatches = st.rowQuery(s);

        for (int t : subjectMatches) {
            Integer o = ot.columnQuery(t);
            if (o != null) {
                int p = bp.rank1(t);
                results.add(new Triple(s, p, o));
            }
        }
        return results;
    }

    public List<Triple> __oQuery(int o) {
        List<Triple> results = new ArrayList<>();

        List<Integer> objectMatches = ot.rowQuery(o);

        for (int t : objectMatches) {
            Integer s = st.columnQuery(t);
            if (s != null) {
                int p = bp.rank1(t);
                results.add(new Triple(s, p, o));
            }
        }
        return results;
    }

    public List<Triple> _p_Query(int p) {
        List<Triple> results = new ArrayList<>();

        int lPredicateBound = bp.select1( p);
        int uPredicateBound = bp.select1( p + 1) - 1;

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

    public List<Triple> ___Query() {
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
