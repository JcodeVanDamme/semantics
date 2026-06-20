package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.List;

public interface BMatrix {

    boolean addTriple(int s, int p, int o) throws IllegalArgumentException;

    boolean deleteTriple(int s, int p, int o) throws IllegalArgumentException;

    boolean updateTriple(int oldS, int oldP, int oldO, int newS, int newP, int newO) throws IllegalArgumentException;

    boolean spo(int s, int p, int o);

    List<Triple> sp_(int s, int p);

    List<Triple> _po(int p, int o);

    List<Triple> s_o(int s, int o);

    List<Triple> s__(int s);

    List<Triple> __o(int o);

    List<Triple> _p_(int p);

    List<Triple> ___();
}
