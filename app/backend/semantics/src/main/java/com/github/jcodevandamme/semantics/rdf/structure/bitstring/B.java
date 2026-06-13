package com.github.jcodevandamme.semantics.rdf.structure.bitstring;

public interface B {
    public int rank(boolean c, int i);

    public int select(boolean c, int j);

    public int access(int i);
}
