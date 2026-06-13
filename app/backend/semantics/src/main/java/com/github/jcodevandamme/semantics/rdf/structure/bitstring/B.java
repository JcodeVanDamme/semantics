package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

public interface B {
    int size();
    int rank(boolean c, int i);
    int select(boolean c, int j);
    int access(int i);
}
