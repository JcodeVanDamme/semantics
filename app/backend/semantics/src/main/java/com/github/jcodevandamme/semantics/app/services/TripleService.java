package com.github.jcodevandamme.semantics.app.services;

import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.request.PutTriplesRequest;
import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;
import com.github.jcodevandamme.semantics.app.persistence.StorageInitializer;
import com.github.jcodevandamme.semantics.app.persistence.TripleLogger;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.springframework.beans.factory.annotation.Value;
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

    public void addTriple(TripleDto dto) throws TripleAlreadyExistsException, IOException {
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
    }

    public TripleDto[] queryTriples(String s, String p, String o) throws TripleCodingException {
        List<Triple> results = tripleStore.query(s, p, o);
        return DTOFactory.tripleArr(results);
    }

    public TripleDto[] querySparql(String sparqlQuery) {
        List<Triple> results = tripleStore.query(sparqlQuery);
        return DTOFactory.tripleArr(results);
    }

    public void updateTriple(PutTriplesRequest request) throws TripleNotFoundException, TripleAlreadyExistsException, IOException {
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
    }

    public void deleteTriple(TripleDto dto) throws TripleNotFoundException, IOException {
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
    }
}