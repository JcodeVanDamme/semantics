package com.semantics.triplestore;

import com.github.jcodevandamme.semantics.rdf.provider.parser.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void initTripleStore_withParser_init() {
        ParserTripleProvider provider = new ParserTripleProvider("src/main/java/com/github/jcodevandamme/semantics/rdf/data.ttl");
        TripleStore store = new TripleStore();
        store.init(provider);

        System.out.println(store);
    }
}
