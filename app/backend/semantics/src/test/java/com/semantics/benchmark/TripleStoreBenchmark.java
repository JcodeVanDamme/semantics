package com.semantics.benchmark;

import com.github.jcodevandamme.semantics.rdf.query.QueryType;
import com.github.jcodevandamme.semantics.rdf.tripleStore.Benchmark;
import org.junit.jupiter.api.Test;

public class TripleStoreBenchmark {

    @Test
    void benchmark_storeSize() throws InterruptedException {
        Integer[] storeSizes = { 64, 128, 256, 512, 1024, 2048, 4096 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark.csv", storeSizes);
    }

}
