package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.StaticTripleProvider;
import com.github.jcodevandamme.semantics.rdf.query.QueryType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Benchmark {

    private static final int UNQ_SUBJECT_PERCENTAGE = 30;
    private static final int UNQ_PREDICATES_PERCENTAGE = 5;
    private static final int UNQ_OBJECT_PERCENTAGE = 100;

    private static final int WARMUP_RUNS = 10000;
    private static final int MEASUREMENT_RUNS = 5000;

    private record BenchmarkResult(
            int storeSize,
            String action,
            double medMill,
            double avgMill
    ) {
        public String toCsv() {
            return String.format(java.util.Locale.US, "%d,%s,%.6f,%.6f", storeSize, action, medMill, avgMill);
        }
    }

    public static void benchmarkTripleStore(String outputFilePath, Integer[] benchmarkStoreSizes) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (Integer size : benchmarkStoreSizes) {
            System.out.println("Starting Triple Store Benchmark with Store Size: " + size);

            TripleStore store = initStore(size, results);

            for (QueryType t : QueryType.values()) {
                results.add(queryBenchmark(store, size, t, WARMUP_RUNS, MEASUREMENT_RUNS));
            }

            results.add(addBenchmark(size, WARMUP_RUNS, MEASUREMENT_RUNS));

            int safeWarmup = Math.min(WARMUP_RUNS, size / 3);
            int safeMeasure = Math.min(MEASUREMENT_RUNS, size / 3);

            if (safeMeasure > 0) {
                results.add(deleteBenchmark(size, safeWarmup, safeMeasure));
                results.add(updateBenchmark(size, safeWarmup, safeMeasure));
            }

            System.out.println("Finished Benchmark for Store Size " + size + ".\n");
        }
        exportResultsToCsv(results, outputFilePath);
    }

    public static BenchmarkResult queryBenchmark(TripleStore store, Integer storeSize, QueryType type, Integer warmupRuns, Integer measurementRuns) {
        System.out.println("Warming up. Executing " + warmupRuns + " " + type + " Queries...");

        int numUniqueS = Math.max(1, (storeSize * UNQ_SUBJECT_PERCENTAGE) / 100);
        int numUniqueP = Math.max(1, (storeSize * UNQ_PREDICATES_PERCENTAGE) / 100);
        int numUniqueO = Math.max(1, (storeSize * UNQ_OBJECT_PERCENTAGE) / 100);

        long dummyChecksum = 0;

        for (int i = 0; i < warmupRuns; i++) {
            int queryIndex = i % storeSize;
            String searchS = "s" + (queryIndex % numUniqueS);
            String searchP = "p" + (queryIndex % numUniqueP);
            String searchO = "o" + (queryIndex % numUniqueO);
            dummyChecksum += executeQuery(type, store, searchS, searchP, searchO);
        }

        System.out.println("Starting Measurements. Executing " + measurementRuns + " Runs...");

        long totalQueryTime = 0;
        long[] individualQueryTimes = new long[measurementRuns];

        for (int i = 0; i < measurementRuns; i++) {
            int queryIndex = i % storeSize;
            String searchS = "s" + (queryIndex % numUniqueS);
            String searchP = "p" + (queryIndex % numUniqueP);
            String searchO = "o" + (queryIndex % numUniqueO);

            long startTime = System.nanoTime();
            dummyChecksum += executeQuery(type, store, searchS, searchP, searchO);
            long endTime = System.nanoTime();

            long queryTime = endTime - startTime;
            totalQueryTime += queryTime;
            individualQueryTimes[i] = queryTime;
        }

        if (dummyChecksum == -1) {
            System.out.println("Checksum-Trigger: " + dummyChecksum);
        }

        return calculateResults(storeSize, type.toString(), totalQueryTime, individualQueryTimes, measurementRuns);
    }

    public static BenchmarkResult addBenchmark(Integer storeSize, Integer warmupRuns, Integer measurementRuns) {
        TripleStore store = new TripleStore();
        List<Triple> initialData = generateMockData(storeSize);
        new StaticTripleProvider(initialData).initTriples(store);

        List<Triple> warmupPool = new ArrayList<>();
        for (int i = 0; i < warmupRuns; i++) {
            warmupPool.add(new Triple("s_new_warmup_" + i, "p_new_warmup_" + i, "o_new_warmup_" + i));
        }
        List<Triple> measurePool = new ArrayList<>();
        for (int i = 0; i < measurementRuns; i++) {
            measurePool.add(new Triple("s_new_measure_" + i, "p_new_measure_" + i, "o_new_measure_" + i));
        }

        for (Triple t : warmupPool) {
            store.create(t);
        }

        long totalTime = 0;
        long[] individualTimes = new long[measurementRuns];

        for (int i = 0; i < measurementRuns; i++) {
            Triple target = measurePool.get(i);

            long startTime = System.nanoTime();
            store.create(target);
            long endTime = System.nanoTime();

            long opTime = endTime - startTime;
            totalTime += opTime;
            individualTimes[i] = opTime;
        }

        return calculateResults(storeSize, "add", totalTime, individualTimes, measurementRuns);
    }

    public static BenchmarkResult deleteBenchmark(Integer storeSize, Integer warmupRuns, Integer measurementRuns) {
        if (warmupRuns + measurementRuns > storeSize) {
            throw new IllegalArgumentException("Store size must be larger than warmup + measurement runs to ensure unique deletes!");
        }

        TripleStore store = new TripleStore();
        List<Triple> initialData = generateMockData(storeSize);
        new StaticTripleProvider(initialData).initTriples(store);

        List<Triple> warmupDeletes = initialData.subList(0, warmupRuns);
        List<Triple> measureDeletes = initialData.subList(warmupRuns, warmupRuns + measurementRuns);

        for (Triple t : warmupDeletes) {
            store.delete(t);
        }

        long totalTime = 0;
        long[] individualTimes = new long[measurementRuns];

        for (int i = 0; i < measurementRuns; i++) {
            Triple target = measureDeletes.get(i);

            long startTime = System.nanoTime();
            store.delete(target);
            long endTime = System.nanoTime();

            long opTime = endTime - startTime;
            totalTime += opTime;
            individualTimes[i] = opTime;
        }

        return calculateResults(storeSize, "delete", totalTime, individualTimes, measurementRuns);
    }

    public static BenchmarkResult updateBenchmark(Integer storeSize, Integer warmupRuns, Integer measurementRuns) {
        if (warmupRuns + measurementRuns > storeSize) {
            throw new IllegalArgumentException("Store size must be larger than warmup + measurement runs to ensure unique updates!");
        }

        TripleStore store = new TripleStore();
        List<Triple> initialData = generateMockData(storeSize);
        new StaticTripleProvider(initialData).initTriples(store);

        List<Triple> warmupTargets = initialData.subList(0, warmupRuns);
        List<Triple> measureTargets = initialData.subList(warmupRuns, warmupRuns + measurementRuns);

        for (Triple t : warmupTargets) {
            store.delete(t);
            store.create(new Triple((String) t.s().value(),(String) t.p().value(),(String) t.o().value() + "_updated"));
        }

        long totalTime = 0;
        long[] individualTimes = new long[measurementRuns];

        for (int i = 0; i < measurementRuns; i++) {
            Triple oldTriple = measureTargets.get(i);
            Triple newTriple = new Triple((String)oldTriple.s().value(), (String) oldTriple.p().value(), (String) oldTriple.o().value() + "_updated");

            long startTime = System.nanoTime();
            store.delete(oldTriple);
            store.create(newTriple);
            long endTime = System.nanoTime();

            long opTime = endTime - startTime;
            totalTime += opTime;
            individualTimes[i] = opTime;
        }

        return calculateResults(storeSize, "update", totalTime, individualTimes, measurementRuns);
    }

    private static BenchmarkResult calculateResults(int storeSize, String action, long totalTimeNanos, long[] individualTimesNanos, int runs) {
        double avgTimeMillis = (totalTimeNanos / (double) runs) / 1_000_000.0;

        Arrays.sort(individualTimesNanos);
        double medianTimeNanos;
        int middle = runs / 2;

        if (runs % 2 == 1) {
            medianTimeNanos = individualTimesNanos[middle];
        } else {
            medianTimeNanos = (individualTimesNanos[middle - 1] + individualTimesNanos[middle]) / 2.0;
        }
        double medianTimeMillis = medianTimeNanos / 1_000_000.0;

        System.out.println("========================================");
        System.out.println("Benchmark Results for: " + action);
        System.out.println("Avg. Runtime (Milliseconds):    " + String.format(java.util.Locale.US, "%.6f", avgTimeMillis) + " ms");
        System.out.println("Median Runtime (Milliseconds): " + String.format(java.util.Locale.US, "%.6f", medianTimeMillis) + " ms");
        System.out.println("========================================");

        return new BenchmarkResult(
                storeSize,
                action,
                medianTimeMillis,
                avgTimeMillis
        );
    }

    private static TripleStore initStore(int storeSize, List<BenchmarkResult> results) {
        TripleStore store = new TripleStore();
        List<Triple> benchmarkData = generateMockData(storeSize);
        StaticTripleProvider provider = new StaticTripleProvider(benchmarkData);

        long startTime = System.nanoTime();
        provider.initTriples(store);
        long endTime = System.nanoTime();

        long initTimeNanos = endTime - startTime;
        double initTimeMillis = initTimeNanos / 1_000_000.0;

        System.out.println("========================================");
        System.out.println("Benchmark Results for: init (Bulk Load)");
        System.out.println("Init Time (Milliseconds): " + String.format(java.util.Locale.US, "%.6f", initTimeMillis) + " ms");
        System.out.println("========================================");

        results.add(
                new BenchmarkResult(
                        storeSize,
                        "init",
                        initTimeMillis,
                        initTimeMillis
                )
        );

        return store;
    }

    private static List<Triple> generateMockData(int count) {
        List<Triple> triples = new ArrayList<>();
        int numUniqueS = Math.max(1, (count * UNQ_SUBJECT_PERCENTAGE) / 100);
        int numUniqueP = Math.max(1, (count * UNQ_PREDICATES_PERCENTAGE) / 100);
        int numUniqueO = Math.max(1, (count * UNQ_OBJECT_PERCENTAGE) / 100);

        for (int i = 0; i < count; i++) {
            String s = "s" + (i % numUniqueS);
            String p = "p" + (i % numUniqueP);
            String o = "o" + (i % numUniqueO);
            triples.add(new Triple(s, p, o));
        }
        return triples;
    }

    public static void exportResultsToCsv(List<BenchmarkResult> results, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("StoreSize,QueryType,MedianMillis,AvgMillis");
            for (BenchmarkResult res : results) {
                writer.println(res.toCsv());
            }
            System.out.println("Generated Benchmark File: " + fileName);
        } catch (IOException e) {
            System.err.println("Error during File Generation: " + e.getMessage());
        }
    }

    private static int executeQuery(QueryType type, TripleStore store, String s, String p, String o) {
        Collection<Triple> results = switch (type) {
            case SPO -> store.query(s, p, o);
            case SP_ -> store.query(s, p, null);
            case _PO -> store.query(null, p, o);
            case S_O -> store.query(s, null, o);
            case S__ -> store.query(s, null, null);
            case __O -> store.query(null, null, o);
            case _P_ -> store.query(null, p, null);
            case ___ -> store.query(null, null, null);
        };
        return results != null ? results.size() : 0;
    }
}