package com.github.jcodevandamme.semantics.app.services.rdf;

import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.request.PutTriplesRequest;
import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;
import com.github.jcodevandamme.semantics.app.persistence.TripleLogger;
import com.github.jcodevandamme.semantics.app.services.AppStore;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TripleService {

    private final AppStore tripleStore;
    private final TripleLogger logger;

    public TripleService(
            AppStore tripleStore,
            TripleLogger logger
    ) {
        this.tripleStore = tripleStore;
        this.logger = logger;
    }

    public boolean addTriple(TripleDto dto) throws IOException {
        Triple triple = new Triple(
                dto.s().value(),
                dto.p().value(),
                dto.o().value(),
                dto.o().isLiteral()
        );
        boolean created = tripleStore.create(triple);
        if (created) {
            logger.registerUpdate(UpdateType.ADD, triple);
        }
        return created;
    }

    public TripleDto[] queryTriples(String s, String p, String o) throws TripleCodingException {
        List<Triple> results = tripleStore.query(s, p, o);
        return DTOFactory.tripleArr(results);
    }

    /*public Object querySparql(String sparqlQuery) {
        return tripleStore.query(sparqlQuery);
    }*/

    public boolean updateTriple(PutTriplesRequest request) throws TripleNotFoundException, IOException {
        TripleDto original = request.original();
        Triple oldT = new Triple(
                original.s().value(),
                original.p().value(),
                original.o().value(),
                original.o().isLiteral()
        );
        TripleDto update = request.update();
        Triple newT = new Triple(
                update.s().value(),
                update.p().value(),
                update.o().value(),
                update.o().isLiteral()
        );
        boolean updated = tripleStore.update(oldT, newT);
        if (updated) {
            logger.registerUpdate(UpdateType.DELETE, oldT);
            logger.registerUpdate(UpdateType.ADD, newT);
        }
        return updated;
    }

    public boolean deleteTriple(TripleDto dto) throws IOException {
        Triple triple = new Triple(
                dto.s().value(),
                dto.p().value(),
                dto.o().value(),
                dto.o().isLiteral()
        );
        boolean deleted = tripleStore.delete(triple);
        if (deleted) {
            logger.registerUpdate(UpdateType.DELETE, triple);
        }
        return deleted;
    }
}