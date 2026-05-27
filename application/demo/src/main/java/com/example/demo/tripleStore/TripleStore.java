package com.example.demo.tripleStore;

import com.example.demo.tripleStore.bMatrix.BMatrix;
import com.example.demo.tripleStore.bMatrix.BMatrixBuilder;
import com.example.demo.tripleStore.bMatrix.TripleDictionary;
import com.example.demo.tripleStore.triple.TestTripleProvider;
import com.example.demo.tripleStore.triple.Triple;
import com.example.demo.tripleStore.triple.TripleProvider;

import java.util.ArrayList;
import java.util.List;

public class TripleStore {

    private TripleDictionary dict;
    private BMatrix bMatrix;

    public void init(TripleProvider tripleProvider) {
        BMatrixBuilder builder = new BMatrixBuilder();
        dict = new TripleDictionary();
        bMatrix = builder.build(2, 10, dict, tripleProvider);
    }

    public List<Triple> query(Triple t) {
        return new ArrayList<Triple>();
    }
    public Boolean create(Triple t) {
        return true;
    }
    public Boolean update(Triple t) {
        return true;
    }
    public Boolean delete(Triple t) {
        return true;
    }

    @Override
    public String toString() {
        return bMatrix.toString() + dict.toString() +
                "-----------------------------------------------------------";
    }
}

