package com.github.jcodevandamme.semantics.rdf.query;

import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrix;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;

import java.util.ArrayList;
import java.util.List;

public class TripleQueryProcessor {

    private final BMatrix bMatrix;

    public TripleQueryProcessor(BMatrix bMatrix) {
        this.bMatrix = bMatrix;
    }

    public List<EncodedTriple> process(TripleQuery query) {
        return execute(query);
    }

    public List<EncodedTriple> execute(TripleQuery q) {
        switch (type(q)) {
            case SPO:
                if (bMatrix.spoQuery(q.s(), q.p(), q.o())) {
                    return new ArrayList<>(List.of(new EncodedTriple(q.s(), q.p(), q.o())));
                } else {
                    return new ArrayList<>();
                }
            case SP_:
                return bMatrix.sp_Query(q.s(), q.p());
            case S_O:
                return bMatrix.s_oQuery(q.s(), q.o());
            case _PO:
                return bMatrix._poQuery(q.p(), q.o());
            case S__:
                return bMatrix.s__Query(q.s());
            case _P_:
                return bMatrix._p_Query(q.p());
            case __O:
                return bMatrix.__oQuery(q.o());
            case ___:
                return bMatrix.___Query();
            default:
                throw new IllegalStateException("Unknown Query Type: " + type(q));
        }
    }
    
    private static QueryType type(TripleQuery query) {
        boolean s = query.s() != null;
        boolean p = query.p() != null;
        boolean o = query.o() != null;

        if (s && p && o) {
            return QueryType.SPO;

        } else if (s && p) {
            return QueryType.SP_;

        } else if (s && o) {
            return QueryType.S_O;

        } else if (p && o) {
            return QueryType._PO;

        } else if (s) {
            return QueryType.S__;

        } else if (p) {
            return QueryType._P_;

        } else if (o) {
            return QueryType.__O;

        } else {
            return QueryType.___;
        }
    }
}
