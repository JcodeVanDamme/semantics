package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

import java.util.BitSet;
import java.util.List;

public class NaiveBitString implements BitInterface {

    private final int size;
    private final BitSet b;

    public NaiveBitString(List<Boolean> b) {
        size = b.size();
        long[] bits = new long[(b.size() + 63) / 64];
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i)) {
                bits[i / 64] |= (1L << (i % 64));
            }
        }
        this.b = BitSet.valueOf(bits);
    }
    @Override
    public int size() { return size; }

    // counts the number of occurrences of bit c in b up to position i
    @Override
    public int rank1(int i) {
        checkBounds(i);
        return b.get(0, i).cardinality();
    }

    // returns the position in b of the j-th bit set to c
    @Override
    public int select1(int j) {
        int count = 1;
        int pos = -1;
        while (true) {

            pos = b.nextSetBit(pos + 1);

            if (pos < 0) {
                throw new IndexOutOfBoundsException("select1(" + j + ") out of Range");
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
    @Override
    public int countOnes() {
        throw new UnsupportedOperationException("countOnes() not implemented");
    }

    @Override
    public void setBit(int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setBit not implemented for NaiveBitString");
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
