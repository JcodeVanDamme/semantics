package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.StaticTripleProvider;
import com.github.jcodevandamme.semantics.rdf.query.QueryType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Benchmark {

    private static final int UNQ_SUBJECT_PERCENTAGE = 100;
    private static final int UNQ_PREDICATES_PERCENTAGE = 100;
    private static final int UNQ_OBJECT_PERCENTAGE = 100;

    private static final int WARMUP_RUNS = 500;
    private static final int MEASUREMENT_RUNS = 1000;


    private record BenchmarkResult(
            int storeSize,
            String action,
            double medNano,
            double medMill,
            double avgNano,
            double avgMill
    ) {
        public String toCsv() {
            return String.format(java.util.Locale.US, "%d,%s,%.2f,%.2f,%.2f,%.2f", storeSize, action, medNano, medMill, avgNano, avgMill);
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
            System.out.println("Finished Benchmark.\n");
        }
        exportResultsToCsv(results, outputFilePath);
    }

    public static BenchmarkResult queryBenchmark(TripleStore store, Integer storeSize, QueryType type, Integer warmupRuns, Integer measurementRuns) {
        System.out.println("Warming up. Executing: " + warmupRuns + " Queries...");

        int maxSubjects = Math.max(1, storeSize / UNQ_SUBJECT_PERCENTAGE);
        for (int i = 0; i < warmupRuns; i++) {
            String searchS = "s" + (i % maxSubjects);
            String searchP = "p" + (i % 20);
            String searchO = "o" + (i % storeSize);
            executeQuery(type, store, searchS, searchP, searchO);
        }

        System.out.println("Starting Measurements. Executing " + measurementRuns + " Runs...");

        long totalQueryTime = 0;
        long[] individualQueryTimes = new long[measurementRuns];

        for (int i = 0; i < measurementRuns; i++) {

            long startTime = System.nanoTime();

            String searchS = "s" + (i % maxSubjects);
            String searchP = "p" + (i % 20);
            String searchO = "o" + (i % storeSize);
            executeQuery(type, store, searchS, searchP, searchO);

            long endTime = System.nanoTime();

            long queryTime = endTime - startTime;

            totalQueryTime += queryTime;
            individualQueryTimes[i] = queryTime;
        }

        double avgTimeNanos = (double) totalQueryTime / measurementRuns;
        double avgTimeMillis = avgTimeNanos / 1_000_000.0;

        double medianQueryTimeNanos;
        Arrays.sort(individualQueryTimes);
        int middle = measurementRuns / 2;

        if (measurementRuns % 2 == 1) {
            medianQueryTimeNanos = individualQueryTimes[middle];
        } else {
            medianQueryTimeNanos = (individualQueryTimes[middle - 1] + individualQueryTimes[middle]) / 2.0;
        }
        double medianQueryTimesMillis = medianQueryTimeNanos / 1_000_000.0;


        System.out.println("========================================");
        System.out.println("Benchmark Results for " + type + " Query:");
        System.out.println("Avg. Runtime (Nanoseconds): " + String.format("%.2f", avgTimeNanos) + " ns");
        System.out.println("Avg. Runtime (Milliseconds): " + String.format("%.4f", avgTimeMillis) + " ms");
        System.out.println("Median Runtime (Nanoseconds): " + String.format("%.2f", medianQueryTimeNanos) + " ns");
        System.out.println("Median Runtime (Milliseconds): " + String.format("%.4f", medianQueryTimesMillis) + " ms");
        System.out.println("========================================");

        return new BenchmarkResult(
                storeSize,
                type.toString(),
                medianQueryTimeNanos,
                medianQueryTimesMillis,
                avgTimeNanos,
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
        System.out.println("Benchmark Results for Initialisation:");
        System.out.println("Init Time (Nanoseconds): " + String.format("%.2f", (double) initTimeNanos) + " ns");
        System.out.println("Init Time (Milliseconds): " + String.format("%.2f", initTimeMillis) + " ns");
        System.out.println("========================================");

        results.add(
                new BenchmarkResult(
                        storeSize,
                        "init",
                        initTimeNanos,
                        initTimeNanos,
                        initTimeNanos,
                        initTimeNanos
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
            writer.println("StoreSize,QueryType,MedianNanos,MedianMillis,AvgNanos,AvgMillis");
            for (BenchmarkResult res : results) {
                writer.println(res.toCsv());
            }
            System.out.println("Generated Benchmark File: " + fileName);
        } catch (IOException e) {
            System.err.println("Error during File Generation: " + e.getMessage());
        }
    }

    private static void executeQuery(QueryType type, TripleStore store, String s, String p, String o) {
        switch (type) {
            case SPO -> {
                store.query(s, p, o);
            }
            case SP_ -> {
                store.query(s, p, null);
            }
            case _PO -> {
                store.query(null, p, o);
            }
            case S_O -> {
                store.query(s, null, o);
            }
            case S__ -> {
                store.query(s, null, null);
            }
            case __O -> {
                store.query(null,null, o);
            }
            case _P_ -> {
                store.query(null, p, null);
            }
            case ___ -> {
                store.query(null, null, null);
            }
            default -> throw new IllegalArgumentException("Unknown Query Type: " + type);
        }
    }
}