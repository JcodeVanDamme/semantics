package com.example;

import com.example.dictionary.DictionaryEncoder;
import com.example.matrix.AdjacencyMatrix;
import com.example.matrix.PredicateMatrixBuilder;
import com.example.parser.RDFParser;
import com.example.store.TripleStore;
import com.example.k2tree.K2Tree;
import com.example.k2tree.K2TreeBuilder;
import com.example.k2tree.K2TreePrinter;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Map<Integer, AdjacencyMatrix> matrices = null;
        try {

            DictionaryEncoder dictionary =
                    new DictionaryEncoder();

            TripleStore tripleStore =
                    new TripleStore();

            RDFParser parser =
                    new RDFParser(dictionary, tripleStore);

            parser.parse("data/history.ttl");

            dictionary.printDictionary();

            tripleStore.printTriples();

            PredicateMatrixBuilder builder =
                    new PredicateMatrixBuilder(
                            tripleStore.getTriples()
                    );

            matrices = builder.buildMatrices();

            System.out.println("\n=== MATRICES ===");

            for (Map.Entry<Integer, AdjacencyMatrix> entry
                    : matrices.entrySet()) {

                int predicate = entry.getKey();

                AdjacencyMatrix matrix =
                        entry.getValue();

                System.out.println(
                        "\nPredicate ID: " + predicate
                );

                matrix.printMatrix();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n=== K2 TREES ===");

        K2TreeBuilder k2Builder =
                new K2TreeBuilder();

        K2TreePrinter printer =
                new K2TreePrinter();

        for (Map.Entry<Integer, AdjacencyMatrix> entry
                : matrices.entrySet()) {

            System.out.println(
                    "\nPredicate ID: "
                            + entry.getKey()
            );

            K2Tree tree =
                    k2Builder.build(entry.getValue());

            printer.print(tree);
        }
    }
}