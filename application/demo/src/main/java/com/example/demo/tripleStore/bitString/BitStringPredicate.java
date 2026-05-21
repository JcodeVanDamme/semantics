package com.example.demo.tripleStore.bitString;

import com.example.demo.tripleStore.triple.Triple;

import java.util.Arrays;
import java.util.List;

public class BitStringPredicate implements B {

    // Stores initial Position of each Predicate in Triples
    private final int[] ap;

    // Stores Predicates in Triples sampled against d
    private final int[] rankP;

    private final int d;

    public BitStringPredicate(int pCount, List<Triple> triples, int d) {
        this.d = d;
        ap = initializeAP(pCount, triples);
        rankP = initializeRankP(triples);
    }

    private int[] initializeAP(int pCount, List<Triple> triples) {
        int[] ap = new int[pCount];
        int idx = 0;
        int currentPredicateID = -1;
        for (int i = 0; i < triples.size(); i++) {
            int pID = (int) triples.get(i).p();
            if (pID != currentPredicateID) {
                ap[idx] = i;
                currentPredicateID = pID;
                idx++;
            }
        }
        return ap;
    }
    private int[] initializeRankP(List<Triple> triples) {
        // Size of rankP is number of Triples / sampling Factor, rounded Up
        int[] rankP = new int[(triples.size() + d - 1) / d];
        int sample = 0;
        for (int i = 0; i < triples.size(); i += d) {
            rankP[sample++] = (int) triples.get(i).p();
        }
        return rankP;
    }

    @Override
    public int rank(boolean c, int i) {
        // -> Given Triple with Index i; what Predicate belongs to it ?
        // Approximate the Predicate Range using sampled rankP
        int lBound = i / d;
        int predicate = rankP[lBound];
        while (predicate + 1 < ap.length && ap[predicate + 1] <= i) {
            predicate++;
        }
        return predicate;
    }

    @Override
    public int select(boolean c, int j) {
        // -> Where does Predicate with ID j  begin in the Triple List ?
        // -> Which Columns in ST/OT / Triples belong to that predicate
        return ap[j];
    }

    @Override
    public int access(int i) {
        throw new UnsupportedOperationException("access not implemented");
    }

    @Override
    public String toString() {
        return "AP: " + Arrays.toString(ap) + "\nrankP: " + Arrays.toString(rankP);
    }
}
