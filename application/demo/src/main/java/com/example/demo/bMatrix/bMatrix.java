package com.example.demo.bMatrix;

import java.util.*;

public class bMatrix {
    private final int k;

    TripleProvider provider;
    List<Triple> encodedTriples;

    int soID;
    HashMap<Integer, String> soStringFromID;
    HashMap<String, Integer> soIDFromString;

    int pID;
    HashMap<Integer, String> pStringFromID;
    HashMap<String, Integer> pIDFromString;

    int numSubjects;
    int numObjects;

    K2Tree st;
    K2Tree ot;


    public bMatrix(int k, TripleProvider provider) {
        this.k = k;

        this.provider = provider;
        encodedTriples = new ArrayList<>();

        soID = 0;
        soStringFromID = new HashMap<>();
        soIDFromString = new HashMap<>();

        pID = 0;
        pStringFromID = new HashMap<>();
        pIDFromString = new HashMap<>();

        numSubjects = 0;
        numObjects = 0;

        //st = new K2Tree();
        //ot = new K2Tree();
    }

    public void init() {
        List<Triple> triples = provider.getTriples();
        assembleIdentifiers(triples);
        decodeTriples(triples);
        assembleBinaryMatrices();
    }

    private void assembleIdentifiers(List<Triple> triples) {
        Set<String> subjects = new HashSet<>();
        Set<String> objects = new HashSet<>();
        Set<String> predicates = new HashSet<>();

        // Collect unique Strings according to Triple-Type
        for (Triple t : triples) {
            subjects.add((String) t.s());
            objects.add((String) t.o());
            predicates.add((String) t.p());
        }
        numSubjects = subjects.size();
        numObjects = objects.size();

        // Subject-Objects need to be remembered to prevent multiple Inclusions
        Set<String> added = new HashSet<>();

        // SO
        for (String s : subjects) {
            if (objects.contains(s)) {
                added.add(s);
                int id = soID++;
                soStringFromID.put(id, s);
                soIDFromString.put(s, id);
            }
        }
        // S
        for (String s : subjects) {
            if (!added.contains(s)) {
                int id = soID++;
                soStringFromID.put(id, s);
                soIDFromString.put(s, id);
            }
        }
        // O
        for (String o : objects) {
            if (!added.contains(o)) {
                int id = soID++;
                soStringFromID.put(id, o);
                soIDFromString.put(o, id);
            }
        }
        // P
        for (String p : predicates) {
            int id = pID++;
            pStringFromID.put(id, p);
            pIDFromString.put(p, id);
        }
    }
    private void decodeTriples(List<Triple> triples) {
        for (Triple t : triples) {
            encodedTriples.add(new Triple(
                    soIDFromString.get(t.s()),
                    pIDFromString.get(t.p()),
                    soIDFromString.get(t.o())
            ));
        }
        encodedTriples.sort(
                Comparator.comparingInt(t -> (Integer) t.p())
        );
    }

    private void assembleBinaryMatrices() {
        // Determine initial Matrix Size
        // -> Max of given Column / Rows increased to next Power of K
        int max = Math.max(
                Math.max(
                        numSubjects,
                        numObjects
                )
                ,encodedTriples.size()
        );
        int size = 1;
        while (size < max) {
            size *= k;
        }
        // Assemble Lists of filled Matrix Cells
        // -> Traverse Matrices sparsely over these instead of looping over all Cells
        List<Cell> stCells = new ArrayList<>();
        List<Cell> otCells = new ArrayList<>();
        for (int i = 0; i < encodedTriples.size(); i++) {
            Triple t = encodedTriples.get(i);
            stCells.add(new Cell(
                    (int) t.s(),
                    i
            ));
            otCells.add(new Cell(
                    (int) t.o(),
                    i
            ));
        }

        K2TreeBuilder k2Builder = new K2TreeBuilder();
        st = k2Builder.constructK2(k, stCells, size);
        ot = k2Builder.constructK2(k, otCells, size);
    }
}
