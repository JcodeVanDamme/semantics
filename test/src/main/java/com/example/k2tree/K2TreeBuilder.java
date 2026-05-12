package com.example.k2tree;


import com.example.matrix.AdjacencyMatrix;

public class K2TreeBuilder {

    public K2Tree build(AdjacencyMatrix matrix) {

        K2Node root =
                buildRecursive(
                        matrix,
                        0,
                        0,
                        matrix.getSize()
                );

        return new K2Tree(root);
    }

    private K2Node buildRecursive(
            AdjacencyMatrix matrix,
            int row,
            int col,
            int size) {

        boolean containsOne =
                containsOne(matrix, row, col, size);

        K2Node node =
                new K2Node(containsOne);

        if (!containsOne || size == 1) {
            return node;
        }

        int half = size / 2;

        node.setChild(0,
                buildRecursive(matrix,
                        row,
                        col,
                        half));

        node.setChild(1,
                buildRecursive(matrix,
                        row,
                        col + half,
                        half));

        node.setChild(2,
                buildRecursive(matrix,
                        row + half,
                        col,
                        half));

        node.setChild(3,
                buildRecursive(matrix,
                        row + half,
                        col + half,
                        half));

        return node;
    }

    private boolean containsOne(
            AdjacencyMatrix matrix,
            int row,
            int col,
            int size) {

        for (int i = row; i < row + size; i++) {

            for (int j = col; j < col + size; j++) {

                if (i < matrix.getSize()
                        && j < matrix.getSize()
                        && matrix.get(i, j) == 1) {

                    return true;
                }
            }
        }

        return false;
    }
}
