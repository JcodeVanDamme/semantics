package com.example.parser;



import com.example.dictionary.DictionaryEncoder;
import com.example.model.Triple;
import com.example.store.TripleStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RDFParser {

    private final DictionaryEncoder dictionaryEncoder;
    private final TripleStore tripleStore;

    public RDFParser(DictionaryEncoder dictionaryEncoder,
                     TripleStore tripleStore) {

        this.dictionaryEncoder = dictionaryEncoder;
        this.tripleStore = tripleStore;
    }

    public void parse(String filePath) throws IOException {

        BufferedReader reader =
                new BufferedReader(new FileReader(filePath));

        String line;

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(" ");

            if (parts.length < 4) {
                continue;
            }

            String subject = parts[0];
            String predicate = parts[1];
            String object = parts[2];

            int s = dictionaryEncoder.encode(subject);
            int p = dictionaryEncoder.encode(predicate);
            int o = dictionaryEncoder.encode(object);

            Triple triple = new Triple(s, p, o);

            tripleStore.addTriple(triple);
        }

        reader.close();
    }
}
