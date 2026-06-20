package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.List;

public interface BMatrix {

    boolean add(int s, int p, int o) throws IllegalArgumentException;

    boolean delete(int s, int p, int o) throws IllegalArgumentException;

    boolean update(int oldS, int oldP, int oldO, int newS, int newP, int newO) throws IllegalArgumentException;

    boolean spoQuery(int s, int p, int o);

    List<Triple> sp_Query(int s, int p);

    List<Triple> _poQuery(int p, int o);

    List<Triple> s_oQuery(int s, int o);

    List<Triple> s__Query(int s);

    List<Triple> __oQuery(int o);

    List<Triple> _p_Query(int p);

    List<Triple> ___Query();
}
