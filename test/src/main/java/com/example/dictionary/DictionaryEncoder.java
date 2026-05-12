package com.example.dictionary;


import java.util.HashMap;
import java.util.Map;

public class DictionaryEncoder {

    private final Map<String, Integer> stringToId = new HashMap<>();
    private final Map<Integer, String> idToString = new HashMap<>();

    private int currentId = 1;

    public int encode(String value) {

        if (stringToId.containsKey(value)) {
            return stringToId.get(value);
        }

        int id = currentId++;

        stringToId.put(value, id);
        idToString.put(id, value);

        return id;
    }

    public String decode(int id) {
        return idToString.get(id);
    }

    public void printDictionary() {

        System.out.println("\n=== Dictionary ===");

        for (Map.Entry<String, Integer> entry : stringToId.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
