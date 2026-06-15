package com.github.jcodevandamme.semantics.rdf.structure.tree;

import com.github.jcodevandamme.semantics.rdf.model.Cell;

import java.util.ArrayList;
import java.util.List;

public interface KTree {

    int matrixSize();
    boolean checkCell(int row, int col);
    default List<Integer> boundedRowQuery(int row, int lColBound, int uColBound) {
        List<Integer> results = new ArrayList<>();
        for (int col = lColBound; col <= uColBound; col++) {
            if (checkCell(row, col)) {
                results.add(col);
            }
        }
        return results;
    }
    default List<Integer> rowQuery(int row) {
        List<Integer> results = new ArrayList<>();
        for (int col = 0; col < matrixSize(); col++) {
            if (checkCell(row, col)) {
                results.add(col);
            }
        }
        return results;
    }
    default Integer columnQuery(int col) {
        for (int row = 0; row < matrixSize(); row++) {
            if (checkCell(row, col)) {
                return row;
            }
        }
        return null;
    }
    default List<Cell> boundedRangeQuery(int lColBound, int uColBound) {
        List<Cell> results = new ArrayList<>();
        for (int row = 0; row < matrixSize(); row ++) {
            for (int col = lColBound; col <= uColBound; col++) {
                if (checkCell(row, col)) {
                    results.add(new Cell(row, col));
                }
            }
        }
        return results;
    }
}
