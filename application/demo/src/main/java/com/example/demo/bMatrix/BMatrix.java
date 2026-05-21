package com.example.demo.bMatrix;

import java.util.*;

public class BMatrix {

    // Subdivision Factor for the K2 Trees
    private final int k;

    // List of ID-encoded Triples
    private final List<Triple> encodedTriples;

    // Dictionary to access Triple-Subject/ and -Object Strings using their encoded IDs
    private final HashMap<Integer, String> soStringFromID;

    // Dictionary to access Triple-Subject/ and -Object IDs using their original String Value
    private final HashMap<String, Integer> soIDFromString;

    // Dictionary to access Predicate Strings using their encoded IDs
    private final HashMap<Integer, String> pStringFromID;

    // Dictionary to access Predicate IDs using their original String Value
    private final HashMap<String, Integer> pIDFromString;

    // Current Subject / Object and Predicate ID
    private int soID = 0;
    private int pID = 0;

    // Number of unique Subjects / Predicates / Objects
    private int sCount = 0;
    private int pCount = 0;
    private int oCount = 0;

    // Subject x Triple Matrix
    private K2Tree st;

    // Object x Triple Matrix
    private K2Tree ot;

    private final BitStringPredicate bp;


    public BMatrix(int k, int d, TripleProvider provider) {
        this.k = k;

        encodedTriples = new ArrayList<>();
        soStringFromID = new HashMap<>();
        soIDFromString = new HashMap<>();
        pStringFromID = new HashMap<>();
        pIDFromString = new HashMap<>();

        // Init

        List<Triple> triples = provider.getTriples();
        assembleIdentifiers(triples);
        decodeTriples(triples);
        assembleBinaryMatrices();
        bp = new BitStringPredicate(pCount, encodedTriples, d);

        ///
        for (Triple t : encodedTriples) {
            System.out.println(t.toString());
        }
        System.out.println();
        System.out.println(bp.toString());
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
        sCount = subjects.size();
        oCount = objects.size();
        pCount = predicates.size();

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
        // Encode Triples by looking up a Strings respective ID using the Value
        for (Triple t : triples) {
            encodedTriples.add(
                    new Triple(
                            soIDFromString.get(t.s()),
                            pIDFromString.get(t.p()),
                            soIDFromString.get(t.o())
                    )
            );
        }
        // Sorting by the Predicate ID groups the encoded Triples along their Predicates
        encodedTriples.sort(
                Comparator.comparingInt(t -> (Integer) t.p())
        );
    }

    private void assembleBinaryMatrices() {
        // Determine initial Matrix Size
        // -> Max of given Column / Rows increased to next Power of K
        int max = Math.max(
                Math.max(
                        sCount,
                        oCount
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
