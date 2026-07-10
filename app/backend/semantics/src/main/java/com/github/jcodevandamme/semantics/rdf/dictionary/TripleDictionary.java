package com.github.jcodevandamme.semantics.rdf.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripleDictionary {

    private final HashMap<Integer, DictEntry> soDecoding;
    private final HashMap<Integer, DictEntry> pDecoding;

    private final HashMap<String, Integer> soEncoding;
    private final HashMap<String, Integer> pEncoding;

    private final List<Integer> soReferences;
    private final List<Integer> pReferences;

    private int currentSoID;
    private int currentPID;

    private final List<Integer> freedSoIDs;
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

    public void registerSO(String value, boolean isLiteral) {
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
        DictEntry entry = new DictEntry(value, isLiteral);
        soEncoding.put(value, id);
        soDecoding.put(id, entry);
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
        DictEntry entry = new DictEntry(value, false);
        pEncoding.put(value, id);
        pDecoding.put(id, entry);
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
        String string = soDecoding.get(id).value();
        if (string == null) throw new TripleCodingException("Unknown SO ID: " + id);
        return string;
    }
    public int encodeP(String string) throws TripleCodingException {
        Integer id = pEncoding.get(string);
        if (id == null) throw new TripleCodingException("Unknown P String: " + string);
        return id;
    }
    public String decodeP(int id) throws TripleCodingException {
        String string = pDecoding.get(id).value();
        if (string == null) throw new TripleCodingException("Unknown P ID: " + id);
        return string;
    }
    public boolean isLiteral(int id) {
        DictEntry entry = soDecoding.get(id);
        if (entry == null) throw new TripleCodingException("Unknown SO ID: " + id);
        return entry.isLiteral();
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();

        strb.append("----------------------- ")
            .append("Dictionary")
            .append(" ------------------------\n");

        strb.append("Subjects / Objects:\n");
        for (Map.Entry<Integer, DictEntry> e : soDecoding.entrySet()) {
            strb
                    .append("Id: ")
                    .append(e.getKey())
                    .append(" | Ref: ")
                    .append(soReferences.get(e.getKey()))
                    .append(" | Literal: ")
                    .append(e.getValue().isLiteral().toString())
                    .append("\n")
                    .append(e.getValue().value())
                    .append("\n");
        }
        strb.append("\nPredicates:\n");
        for (Map.Entry<Integer, DictEntry> e : pDecoding.entrySet()) {
            strb
                    .append("Id: ")
                    .append(e.getKey())
                    .append(" | Ref: ")
                    .append(soReferences.get(e.getKey()))
                    .append("\n")
                    .append(e.getValue().value())
                    .append("\n");
        }
        return strb.toString();
    }
}
