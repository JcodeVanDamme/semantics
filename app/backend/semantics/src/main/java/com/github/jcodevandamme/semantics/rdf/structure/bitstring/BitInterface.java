package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

public interface BitInterface {
    int size();
    int rank1(int i);
    int select1(int j);
    int access(int i);
    int countOnes();
    void set(int i);
}
