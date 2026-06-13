package com.semantics.rdf.query;

import com.semantics.rdf.bmatrix.BMatrix;
import com.semantics.rdf.model.Triple;
import com.semantics.rdf.query.Query;

import java.util.ArrayList;
import java.util.List;

public class QueryProcessor {

    private final BMatrix bMatrix;
    public QueryProcessor(BMatrix bMatrix) {
        this.bMatrix = bMatrix;
    }
    public List<Triple> process(Query query) {
        if (query instanceof TripleQuery) {
            return processTripleQuery((TripleQuery) query);
        }
        throw new IllegalStateException("Unhandled Query Type: " + query.toString());
    }

    private List<Triple> processTripleQuery(TripleQuery q) {
        switch (type(q)) {
            case SPO:
                if (bMatrix.spo(q.s(), q.p(), q.o())) {
                    return new ArrayList<>(List.of(new Triple(q.s(), q.p(), q.o())));
                } else {
                    return new ArrayList<>();
                }
            case SP_:
                return bMatrix.sp_(q.s(), q.p());
            case S_O:
                return bMatrix.s_o(q.s(), q.o());
            case _PO:
                return bMatrix._po(q.p(), q.o());
            case S__:
                return bMatrix.s__(q.s());
            case _P_:
                return bMatrix._p_(q.p());
            case __O:
                return bMatrix.__o(q.o());
            case ___:
                return bMatrix.___();
            default:
                throw new IllegalStateException("Unknown Query Type: " + type(q));
        }
    }
    
    private QueryType type(Query query) {
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
