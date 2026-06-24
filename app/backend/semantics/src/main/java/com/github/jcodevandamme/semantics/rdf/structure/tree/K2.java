package com.github.jcodevandamme.semantics.rdf.structure.tree;

import com.github.jcodevandamme.semantics.rdf.model.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Common interface for K2-Tree based Matrix Structures used by the BMatrix. </p>
 *
 * <p> Implementations are required to provide Access to a Cell of the
 *     conceptual NxN binary Matrix through {@link #checkCell(int, int)}.
 *     Higher-level Query operations on the Trees are provided as default
 *     Implementations and consist of repeated Calls to {@link #checkCell(int, int)}. </p>
 */
public interface K2 {

    boolean addEntry(int row, int col);
    boolean removeEntry(int row, int col);
    boolean update(int removeRow, int removeCol, int addRow, int addCol);


    /**
     * Returns the Dimension of the conceptual NxN Matrix.
     *
     * @return the Matrix Dimension N
     */
    int matrixSize();

    /**
     * Returns the Value stored at the specified Cell of the conceptual Matrix.
     *
     * @param row Row Index
     * @param col Column Index
     * @return true if the Cell contains a setBit Bit, otherwise false
     */
    boolean checkCell(int row, int col);

    /**
     * Returns all Column Indices within the specified Bounds whose Cells are setBit
     * in the given row.
     *
     * @param row Row Index
     * @param lColBound Lower Column Index Bound
     * @param uColBound Upper Column Index Bound
     * @return Indices of all Columns with a setBit Bit
     */
    default List<Integer> boundedRowQuery(int row, int lColBound, int uColBound) {
        List<Integer> results = new ArrayList<>();
        for (int col = lColBound; col <= uColBound; col++) {
            if (checkCell(row, col)) {
                results.add(col);
            }
        }
        return results;
    }

    /**
     * Returns the Column Indices of all setBit Cells in the specified row.
     *
     * @param row Row Index
     * @return  Indices of all Columns with a setBit Bit
     */
    default List<Integer> rowQuery(int row) {
        List<Integer> results = new ArrayList<>();
        for (int col = 0; col < matrixSize(); col++) {
            if (checkCell(row, col)) {
                results.add(col);
            }
        }
        return results;
    }

    /**
     * Returns the Row Index of the first Row containing a Set Bit for the given Column.
     *
     * @param col Column Index
     * @return Row Index of the first valid Cell
     */
    default Integer columnQuery(int col) {
        for (int row = 0; row < matrixSize(); row++) {
            if (checkCell(row, col)) {
                return row;
            }
        }
        return null;
    }


    /**
     * Returns all setBit Cells whose Column lies within the specified Bounds.
     *
     * @param lColBound Lower Column Index Bound
     * @param uColBound Upper Column Index Bound
     * @return Set Cells in the specified Column Range
     */
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

    /**
     * Returns all setBit Cells of the specified Column
     *
     * @param col Column Index
     * @return Set Cells in the specified Column Range
     */
    default List<Cell> wholeRowQuery(int col) {
        List<Cell> results = new ArrayList<>();

        for (int row = 0; row < matrixSize(); row++) {
            if (checkCell(row, col)) {
                results.add(new Cell(row, col));
            }
        }
        return results;
    }
}
