package com.example.demo.tripleStore.bMatrix;

import com.example.demo.tripleStore.triple.Triple;
import com.example.demo.tripleStore.triple.TripleProvider;

import java.util.HashMap;

public class TripleDictionary {

    private final HashMap<Integer, String> soDecoding;
    private final HashMap<String, Integer> soEncoding;

    private final HashMap<Integer, String> pDecoding;
    private final HashMap<String, Integer> pEncoding;

    private int soID;
    private int pID;
    public TripleDictionary() {
        soDecoding = new HashMap<>();
        soEncoding = new HashMap<>();
        pDecoding = new HashMap<>();
        pEncoding = new HashMap<>();

        soID = 0;
        pID = 0;
    }

    public void registerSO(String value) {
        int id = soID++;
        soEncoding.put(value, id);
        soDecoding.put(id, value);
    }

    public void registerP(String value) {
        int id = pID++;
        pEncoding.put(value, id);
        pDecoding.put(id, value);
    }

    public int encodeSO(String string) {
        Integer id = soEncoding.get(string);
        if (id == null) throw new IllegalStateException("Unknown SO String: " + string);
        return id;
    }
    public String decodeSO(int id) {
        String string = soDecoding.get(id);
        if (string == null) throw new IllegalStateException("Unknown SO ID: " + id);
        return string;
    }
    public int encodeP(String string) {
        Integer id = pEncoding.get(string);
        if (id == null) throw new IllegalStateException("Unknown P String: " + string);
        return id;
    }
    public String decodeP(int id) {
        String string = pDecoding.get(id);
        if (string == null) throw new IllegalStateException("Unknown P ID: " + id);
        return string;
    }
}
