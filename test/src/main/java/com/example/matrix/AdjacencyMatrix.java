package com.example.matrix;


public class AdjacencyMatrix {

    private final int size;

    private final int[][] matrix;

    public AdjacencyMatrix(int size) {

        this.size = size;

        matrix = new int[size][size];
    }

    public void set(int row, int col) {

        matrix[row][col] = 1;
    }

    public int get(int row, int col) {

        return matrix[row][col];
    }

    public int getSize() {
        return size;
    }

    public void printMatrix() {

        System.out.println();

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {

                System.out.print(matrix[i][j] + " ");
            }

            System.out.println();
        }
    }
}