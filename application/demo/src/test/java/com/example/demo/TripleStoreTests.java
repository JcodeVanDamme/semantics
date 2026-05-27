package com.example.demo;
import com.example.demo.tripleStore.TripleStore;
import com.example.demo.tripleStore.triple.TestTripleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TripleStoreTests {

    static TripleStore tripleStore;

    @BeforeAll
    static void init() {
        tripleStore = new TripleStore();
        tripleStore.init(new TestTripleProvider());
    }

    @Test
    public void initTest() {
        System.out.println(tripleStore.toString());
    }
}
