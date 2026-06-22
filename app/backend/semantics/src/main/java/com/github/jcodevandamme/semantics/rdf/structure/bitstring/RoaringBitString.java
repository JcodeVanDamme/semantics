package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

import org.roaringbitmap.RoaringBitmap;

import java.util.List;

public class RoaringBitString implements BitInterface {

    private final RoaringBitmap bits;
    private final int size;

    public RoaringBitString(List<Boolean> b) {
        bits = RoaringBitmap.bitmapOf(mapIndexes(b));
        size = b.size();
    }

    private int[] mapIndexes(List<Boolean> bits) {
        int numSetBits = 0;

        for (boolean bit : bits) {
            if (bit) {
                numSetBits++;
            }
        }

        int[] indices = new int[numSetBits];

        int idx = 0;
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) {
                indices[idx++] = i;
            }
        }

        return indices;
    }
    @Override
    public int size() {
        return size;
    }

    @Override
    public int rank1(int i) {
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException("rank1(" + i + ") out of Range");
        }
        if (i == 0) {
            return 0;
        }
        return bits.rank(i - 1);
    }

    @Override
    public int select1(int j) {
        if (j < 0 || j > size) {
            throw new IndexOutOfBoundsException("select1(" + j + ") out of Range");
        }
        return bits.select(j - 1);
    }

    @Override
    public int access(int i) {
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException("access1(" + i + ") out of Range");
        }
        return bits.contains(i) ? 1 : 0;
    }

    @Override
    public int countOnes() {
        return bits.getCardinality();
    }
    @Override
    public void set(int i) {
        bits.add(i);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        boolean first = true;

        for (int i : bits) {
            if (!first) sb.append(", ");
            sb.append(i);
            first = false;
        }

        sb.append("}");
        return sb.toString();
    }
}
