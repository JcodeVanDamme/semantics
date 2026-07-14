package com.github.jcodevandamme.semantics.app.services.domain.actor;
public final class Vocab {

    public static final String ONT_NS = "http://semantics.rdf.system.ontology/";
    public static final String DATA_NS = "http://semantics.rdf.system.data/";
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";

    public static final class Domain {
        public static final String PLACE = ONT_NS + "Place";
        public static final String STATE = ONT_NS + "State";
        public static final String IS_ORIGINAL = ONT_NS + "isOriginalState";
        public static final String IS_ACTIVE = ONT_NS + "isActive";
        public static final String REGION = ONT_NS + "Region";
        public static final String RULER = ONT_NS + "Ruler";
        public static final String POPULATION = ONT_NS + "population";
        public static final String STATE_TYPE = ONT_NS + "stateType";
        public static final String REGION_TYPE = ONT_NS + "regionType";
        public static final String RULER_TITLE = ONT_NS + "rulerTitle";
        public static final String HAS_RULER = ONT_NS + "hasRuler";
        public static final String LOCATED_IN = ONT_NS + "locatedIn";
        public static final String MEDIATIZED = ONT_NS + "mediatized";
    }

    public static final class Rdf {
        public static final String TYPE = RDF_NS + "type";
    }

    public static final class Rdfs {
        public static final String LABEL = RDFS_NS + "label";
    }

    private Vocab() {}
}
