package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

import org.roaringbitmap.IntConsumer;
import org.roaringbitmap.RoaringBitmap;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoaringBitString implements BitInterface {

    private RoaringBitmap bits;
    private int size;

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
            return 0;
        }
        return bits.contains(i) ? 1 : 0;
    }

    @Override
    public int countSetBits() {
        return bits.getCardinality();
    }
    @Override
    public void setBit(boolean value, int i) {
        if (value) {
            bits.add(i);
        } else {
            bits.remove(i);
        }
    }

    @Override
    public void addBits(int i, int numBits) {
        assert numBits >= 0;
        if (numBits == 0) {
            return;
        }

        RoaringBitmap updated = new RoaringBitmap();
        bits.forEach((IntConsumer) value -> {
            if (value >= i) {
                updated.add(value + numBits);
            } else {
                updated.add(value);
            }
        });
        bits = updated;
        size += numBits;
    }
    @Override
    public int removeBits(int i, int numBits) {
        if (numBits < 0) {
            numBits = -numBits;
        } else if (numBits == 0) {
            return 0;
        }

        RoaringBitmap updated = new RoaringBitmap();
        AtomicInteger bitsKept = new AtomicInteger();
        int finalNumBits = numBits;
        bits.forEach((IntConsumer) value -> {
            if (value < i) {
                updated.add(value);
                bitsKept.getAndIncrement();
            } else if (value >= i + finalNumBits) {
                updated.add(value - finalNumBits);
                bitsKept.getAndIncrement();
            }
        });

        int bitsRemoved = size - bitsKept.get();

        bits = updated;
        size -= numBits;

        return bitsRemoved;
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
