package com.github.jcodevandamme.semantics.rdf.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripleDictionary {
    private final HashMap<Integer, String> soDecoding;
    private final HashMap<String, Integer> soEncoding;
    private final List<Integer> soReferences;

    private final HashMap<Integer, String> pDecoding;
    private final HashMap<String, Integer> pEncoding;
    private final List<Integer> pReferences;

    private int currentSoID;
    private final List<Integer> freedSoIDs;

    private int currentPID;
    private final List<Integer> freedPIDs;

    public TripleDictionary() {
        soDecoding = new HashMap<>();
        soEncoding = new HashMap<>();
        pDecoding = new HashMap<>();
        pEncoding = new HashMap<>();

        soReferences = new ArrayList<>();
        pReferences = new ArrayList<>();

        currentSoID = 0;
        freedSoIDs = new ArrayList<>();

        currentPID = 0;
        freedPIDs = new ArrayList<>();
    }

    public void registerSO(String value) {
        Integer id = soEncoding.get(value);
        if (id != null) {
            int currentRefCount = soReferences.get(id);
            soReferences.set(id, currentRefCount + 1);
            return;
        }
        if (!freedSoIDs.isEmpty()) {
            id = freedSoIDs.removeFirst();
            soReferences.set(id, 1);
        } else {
            id = currentSoID++;
            soReferences.add(1);
        }
        soEncoding.put(value, id);
        soDecoding.put(id, value);
    }

    public void registerP(String value) {
        Integer id = pEncoding.get(value);
        if (id != null) {
            int currentRefCount = pReferences.get(id);
            pReferences.set(id, currentRefCount + 1);
            return;
        }
        if (!freedPIDs.isEmpty()) {
            id = freedPIDs.removeFirst();
            pReferences.set(id, 1);
        } else {
            id = currentPID++;
            pReferences.add(1);
        }
        pEncoding.put(value, id);
        pDecoding.put(id, value);
    }

    public void unregisterSO(String value) {
        Integer id = soEncoding.get(value);
        if (id == null) {
            return;
        }

        int currentRefCount = soReferences.get(id) - 1;

        if (currentRefCount <= 0) {
            soEncoding.remove(value);
            soDecoding.remove(id);
            freedSoIDs.add(id);
        }
        soReferences.set(id, currentRefCount);
    }
    public void unregisterP(String value) {
        Integer id = pEncoding.get(value);
        if (id == null) {
            return;
        }

        int currentRefCount = pReferences.get(id) - 1;

        if (currentRefCount <= 0) {
            pEncoding.remove(value);
            pDecoding.remove(id);
            freedPIDs.add(id);
        }
        pReferences.set(id, currentRefCount);
    }

    public int encodeSO(String string) throws TripleCodingException {
        Integer id = soEncoding.get(string);
        if (id == null) throw new TripleCodingException("Unknown SO String: " + string);
        return id;
    }
    public String decodeSO(int id) throws TripleCodingException {
        String string = soDecoding.get(id);
        if (string == null) throw new TripleCodingException("Unknown SO ID: " + id);
        return string;
    }
    public int encodeP(String string) throws TripleCodingException {
        Integer id = pEncoding.get(string);
        if (id == null) throw new TripleCodingException("Unknown P String: " + string);
        return id;
    }
    public String decodeP(int id) throws TripleCodingException {
        String string = pDecoding.get(id);
        if (string == null) throw new TripleCodingException("Unknown P ID: " + id);
        return string;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append("----------------------- ")
            .append("Dictionary")
            .append(" ------------------------\n");

        strb.append("Subjects / Objects:\n");
        for (Map.Entry<Integer, String> e : soDecoding.entrySet()) {
            strb
                    .append(e.getKey()).append(" - ").append(e.getValue()).append("\n");
        }
        strb.append("\nPredicates:\n");
        for (Map.Entry<Integer, String> e : pDecoding.entrySet()) {
            strb
                    .append(e.getKey()).append(" - ").append(e.getValue()).append("\n");
        }
        return strb.toString();
    }
}
