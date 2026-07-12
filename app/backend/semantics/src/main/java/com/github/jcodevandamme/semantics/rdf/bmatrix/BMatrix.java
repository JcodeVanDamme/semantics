package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Cell;
import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.structure.index.DynamicPredicateIndex;
import com.github.jcodevandamme.semantics.rdf.structure.tree.K2;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Builder;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;

import java.util.*;

public class BMatrix {

    private final List<EncodedTriple> triples;

    private final K2 st;
    private final K2 ot;
    private final DynamicPredicateIndex bp;
    private final int t;

    public BMatrix(int k, int t, DynamicBitVectorConfiguration config) {
        this.triples = new ArrayList<>();
        this.t = t;

        bp = new DynamicPredicateIndex();
        st = DK2Builder.build(config, k);
        ot = DK2Builder.build(config, k);
    }

    public boolean add(int s, int p, int o) {
        if (spoQuery(s, p, o)) {
            return false;
        }

        int tripleIdx = getNewTripleIdx();
        st.addEntry(s, tripleIdx);
        ot.addEntry(o, tripleIdx);
        bp.registerTriple(tripleIdx, p);
        triples.add(new EncodedTriple(s, p, o));
        return true;
    }

    private int getNewTripleIdx() {
        int nextStCol = st.getNextAvailableColumnIndex();
        int nextOtCol = ot.getNextAvailableColumnIndex();

        if (nextStCol != nextOtCol) {
            throw new RuntimeException("BMatrix Error: ST and OT Column Indexes have diverged.");
        }
        return nextStCol;
    }

    public boolean delete(int s, int p, int o) {
        if (!spoQuery(s, p, o)) {
            return false;
        }
        int tripleIdx = getIndexOfTriple(s, p, o);
        st.removeEntry(s, tripleIdx);
        ot.removeEntry(o, tripleIdx);
        bp.deregisterTriple(tripleIdx, p);
        triples.remove(new EncodedTriple(s, p, o));
        return true;
    }

    private int getIndexOfTriple(int s, int p, int o) {
        List<Integer> triples = bp.select1(p);
        for (int col : triples) {
            if (st.checkCell(s, col) && ot.checkCell(o, col)) {
                return col;
            }
        }
        throw new RuntimeException("BMatrix Error: Index of Triple not found.");
    }

    public boolean update(int oldS, int oldP, int oldO, int newS, int newP, int newO) throws TripleNotFoundException, TripleAlreadyExistsException {
        if (!spoQuery(oldS, oldP, oldO)) {
            throw new TripleNotFoundException();
        } else if (spoQuery(newS, newP, newO)) {
            throw new TripleAlreadyExistsException();
        }

        delete(oldS, oldP, oldO);
        add(newS, newP, newO);
        return true;
    }

    public boolean spoQuery(int s, int p, int o) {
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
    public List<EncodedTriple> sp_Query(int s, int p) {
        List<EncodedTriple> results = new ArrayList<>();

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
    public List<EncodedTriple> _poQuery(int p, int o) {
        List<EncodedTriple> results = new ArrayList<>();

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
    public List<EncodedTriple> s_oQuery(int s, int o) {
        List<EncodedTriple> results = new ArrayList<>();

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
    public List<EncodedTriple> s__Query(int s) {
        List<EncodedTriple> results = new ArrayList<>();

        List<Integer> subjectMatches = st.rowQuery(s);

        for (int t : subjectMatches) {
            Integer o = ot.columnQuery(t);
            if (o != null) {
                int p = bp.rank1(t);
                results.add(new EncodedTriple(s, p, o));
            }
        }
        return results;
    }
    public List<EncodedTriple> __oQuery(int o) {
        List<EncodedTriple> results = new ArrayList<>();

        List<Integer> objectMatches = ot.rowQuery(o);

        for (int t : objectMatches) {
            Integer s = st.columnQuery(t);
            if (s != null) {
                int p = bp.rank1(t);
                results.add(new EncodedTriple(s, p, o));
            }
        }
        return results;
    }
    public List<EncodedTriple> _p_Query(int p) {
        List<EncodedTriple> results = new ArrayList<>();
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
                    results.add(new EncodedTriple(sCell.row(), p, oCell.row()));
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
    public List<EncodedTriple> ___Query() {
        return triples;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append("------------------------- ")
            .append("Triples")
            .append(" -------------------------\n");

        for (EncodedTriple t : triples) {
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

        strb.append(bp).append("\n");

        return strb.toString();
    }
}
