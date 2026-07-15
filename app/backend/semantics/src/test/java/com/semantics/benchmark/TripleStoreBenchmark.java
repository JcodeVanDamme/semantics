package com.semantics.benchmark;

import com.github.jcodevandamme.semantics.rdf.query.QueryType;
import com.github.jcodevandamme.semantics.rdf.tripleStore.Benchmark;
import org.junit.jupiter.api.Test;

public class TripleStoreBenchmark {

    @Test
    void simpleBM() {
        Benchmark.queryBenchmark(1_000_000, QueryType.SPO, 0, 1);
    }

    @Test
    void benchmark_storeSize_500() throws InterruptedException {
        Integer[] storeSizes = { 500 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_500.csv", storeSizes);
        Thread.sleep(60000);
    }
    @Test
    void benchmark_storeSize_5000() {
        Integer[] storeSizes = { 5000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_5000.csv", storeSizes);
    }
    @Test
    void benchmark_storeSize_10_000() {
        Integer[] storeSizes = { 10_000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_10_000.csv", storeSizes);
    }
    @Test
    void benchmark_storeSize_50000() {
        Integer[] storeSizes = { 50_000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_50_000.csv", storeSizes);
    }
    @Test
    void benchmark_storeSize_100000() {
        Integer[] storeSizes = { 100_000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_100_000.csv", storeSizes);
    }
    @Test
    void benchmark_storeSize_500000() {
        Integer[] storeSizes = { 500_000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_500_000.csv", storeSizes);
    }
    @Test
    void benchmark_storeSize_1000000() {
        Integer[] storeSizes = { 1_000_000 };
        Benchmark.benchmarkTripleStore("./tripleStoreBenchmark_1_000_000.csv", storeSizes);
    }
}
