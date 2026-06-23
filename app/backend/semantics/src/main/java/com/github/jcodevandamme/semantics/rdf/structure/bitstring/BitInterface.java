package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

public interface BitInterface {
    int size();
    int rank1(int i);
    int select1(int j);
    int access(int i);
    int setBitCount();
    void setBit(boolean value, int i);
    void addBits(int i, int numBits);
    int removeBits(int i, int numBits);
}
