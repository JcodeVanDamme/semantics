package com.example.demo.bMatrix;

import java.util.BitSet;
import java.util.List;

public class BitString {

    private final int size;
    private final BitSet b;

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
    public int rank(boolean c, int i) {
        if (i < 0 ||i > size) {
            throw new IndexOutOfBoundsException();
        }

        int setBits = b.get(0, i).cardinality();
        return c ? setBits : i - setBits;
    }

    // returns the position in b of the j-th bit set to c
    public long select(boolean c, int j) {
        int count = 1;
        int pos = -1;
        while (true) {
            if (c) {
                pos = b.nextSetBit(pos + 1);
            } else {
                pos = b.nextClearBit(pos + 1);
            }
            if (pos < 0) {
                throw new ArrayStoreException();
            }
            if (count == j) {
                return pos;
            }
            count++;
        }
    }

    // gets the bit value at b[i]
    public int access(int i) {
        return b.get(i) ? 1 : 0;
    }
}
