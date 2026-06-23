package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

import it.unimi.dsi.bits.LongArrayBitVector;
import it.unimi.dsi.sux4j.bits.*;

import java.util.List;

public class SuxBitString implements BitInterface {

    private final Rank rank;
    private final Select select;
    private final long[] bits;
    private int size;

    public SuxBitString(List<Boolean> b) {
        long[] bits = new long[(b.size() + 63) / 64];
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i)) {
                bits[i / 64] |= (1L << (i % 64));
            }
        }

        rank = new Rank16(bits, b.size());
        select = new Select9(new Rank9(bits, b.size()));
        this.bits = bits;
        size = b.size();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int rank1(int i) {
        try {
            return (int) rank.rank(i);
        } catch (AssertionError err) {
            throw new IndexOutOfBoundsException("Index: " + i + " out of Bounds for Size: " + size);
        }
    }

    @Override
    public int select1(int j) {
        try {
            // Previous Implementation expected index to be 1-based for Select
            // ; Sux expects 0 so Select is being called with j+1 to preserve past Behaviour
            return (int) select.select(j - 1);
        } catch (AssertionError err) {
            throw new IndexOutOfBoundsException("select1(" + j + ") out of Range");
        }
    }

    @Override
    public int access(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("access(" + i + ") out of Range");
        }
        return ((bits[i / 64] >>> (i % 64)) & 1L) != 0 ? 1 : 0;
    }

    @Override
    public int setBitCount() {
        return (int) rank.count();
    }

    @Override
    public void setBit(boolean value, int i) {
        throw new UnsupportedOperationException("setBit(int i) not implemented");
    }
    @Override
    public void addBits(int i, int numBits) {
        throw new UnsupportedOperationException("addBits(int i, int numBits) not implemented");
    }

    @Override
    public int removeBits(int i, int numBits) {
        throw new UnsupportedOperationException("removeBits(int i, int numBits) not implemented");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        boolean first = true;

        for (int i = 0; i < size; i++) {
            if (((bits[i / 64] >>> (i % 64)) & 1L) != 0) {
                if (!first) sb.append(", ");
                sb.append(i);
                first = false;
            }
        }

        sb.append("}");
        return sb.toString();
    }
}
