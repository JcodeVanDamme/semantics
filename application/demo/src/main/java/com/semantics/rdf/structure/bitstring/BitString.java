package com.semantics.rdf.structure.bitstring;

import java.util.BitSet;
import java.util.List;

public class BitString implements B {

    private final int size;
    private final BitSet b;

    public int size() { return size; }

    public BitString(List<Boolean> b) {
        size = b.size();
        long[] bits = new long[(b.size() + 63) / 64];
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i)) {
                bits[i / 64] |= (1L << (i % 64));
            }
        }
        this.b = BitSet.valueOf(bits);
    }

    // counts the number of occurrences of bit c in b up to position i
    @Override
    public int rank(boolean c, int i) {
        checkBounds(i);
        int setBits = b.get(0, i).cardinality();
        return c ? setBits : i - setBits;
    }

    // returns the position in b of the j-th bit set to c
    @Override
    public int select(boolean c, int j) {
        int count = 1;
        int pos = -1;
        while (true) {
            if (c) {
                pos = b.nextSetBit(pos + 1);
            } else {
                pos = b.nextClearBit(pos + 1);
            }
            if (pos < 0) {
                throw new IndexOutOfBoundsException("select(" + c + "," + j + ") out of Range");
            }
            if (count == j) {
                return pos;
            }
            count++;
        }
    }

    // gets the bit value at b[i]
    @Override
    public int access(int i) {
        checkBounds(i);
        return b.get(i) ? 1 : 0;
    }

    private void checkBounds(int i) {
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException("Index: " + i + " out of Bounds for Size: " + size);
        }
    }

    @Override
    public String toString() {
        return b.toString();
    }
}
