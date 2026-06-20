package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.structure.index.DynamicPredicateIndex;
import com.github.jcodevandamme.semantics.rdf.structure.index.StaticPredicateIndex;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Tree;

import java.util.*;

public class DynamicBMatrix implements BMatrix {

    private final List<Triple> triples;
    private final DynamicPredicateIndex bp;
    private final int t;

    private K2 st;
    private K2 ot;

    private int currentColumn;
    private final ArrayList<Integer> freedColumns;

    public DynamicBMatrix(List<Triple> triples, DK2Tree st, DK2Tree ot, DynamicPredicateIndex bp, int t) {
        this.triples = triples;
        this.st = st;
        this.ot = ot;
        this.bp = bp;
        this.t = t;

        currentColumn = triples.size();
        freedColumns = new ArrayList<>();
    }

    public boolean addTriple(int s, int p, int o) throws IllegalArgumentException {
        if (spo(s, p, o)) {
            throw new IllegalArgumentException("Triple to be created already exists");
        }

        if (currentColumn < st.matrixSize() && currentColumn < ot.matrixSize()) {
            // Insert
        } else {
            // Expand
            // Insert
        }

        return true;
    }
    public boolean deleteTriple(int s, int p, int o) throws IllegalArgumentException {
        if (!spo(s, p, o)) {
            throw new IllegalArgumentException("Triple to be deleted does not exist");
        }

        return true;
    }
    public boolean updateTriple(int oldS, int oldP, int oldO, int newS, int newP, int newO) throws IllegalArgumentException {
        if (!spo(oldS, oldP, oldO)) {
            throw new IllegalArgumentException("Triple to be updated does not exist");
        } else if (spo(newS, newP, newO)) {
            throw new IllegalArgumentException("Triple to be deleted does not exist");
        }

        deleteTriple(oldS, oldP, oldO);
        addTriple(newS, newP, newO);

        return true;
    }

    public boolean spo(int s, int p, int o) {
        List<Integer> cols = bp.select1(p);

        List<Integer> triples = new ArrayList<>();
        for (int col : cols) {
            if (st.checkCell(s, col)) {
                triples.add(col);
            }
        }
        for (int t : triples) {
            if (ot.checkCell(o, t)) {
                return true;
            }
        }
        return false;
    }

    public List<Triple> sp_(int s, int p) {
        List<Triple> results = new ArrayList<>();

        List<Integer> cols = bp.select1(p);
        List<Integer> triples = new ArrayList<>();
        for (int col : cols) {
            if (st.checkCell(s, col)) {
                triples.add(col);
            }
        }

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

        List<Integer> cols = bp.select1(p);
        List<Integer> triples = new ArrayList<>();
        for (int col : cols) {
            if (ot.checkCell(o, col)) {
                triples.add(col);
            }
        }

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
                int p = bp.rank1(t);
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
                int p = bp.rank1(t);
                results.add(new Triple(s, p, o));
            }
        }
        return results;
    }
    public List<Triple> _p_(int p) {
        List<Triple> results = new ArrayList<>();
        List<Integer> cols = bp.select1(p);

        List<Cell> subjects = new ArrayList<>();
        for (int col : cols) {
            subjects.addAll(st.wholeRowQuery(col));
        }

        if (subjects.size() <= t) {

            for (Cell c : subjects) {
                Integer res = ot.columnQuery(c.col());
                if (res != null) {
                    results.add(this.triples.get(c.col()));
                }
            }
        } else {
            List<Cell> objects = new ArrayList<>();
            for (int col : cols) {
                objects.addAll(ot.wholeRowQuery(col));
            }


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
        results.sort(Comparator.comparingInt(t -> (int) t.s()));
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
