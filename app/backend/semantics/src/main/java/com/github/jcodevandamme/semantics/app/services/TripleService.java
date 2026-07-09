package com.github.jcodevandamme.semantics.app.services;

import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.request.PutTriplesRequest;
import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.parser.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TripleService {

    private final TripleStore tripleStore;
    private final Path storagePath;

    public TripleService(
            @Value("${rdf.storage.directory}") String dir,
            @Value("${rdf.storage.filename.data}") String dataFilename,
            @Value("${rdf.storage.filename.update}") String updateFilename
    ) throws IOException {
        this.tripleStore = new TripleStore();
        this.storagePath = Paths.get(dir).resolve(dataFilename);

        Files.createDirectories(storagePath.getParent());
        if (Files.exists(storagePath)) {
            ParserTripleProvider provider = new ParserTripleProvider(storagePath.toString());
            provider.initTriples(tripleStore);
            System.out.println("Successfully loaded existing triples into memory.");
        } else {
            System.out.println("No existing snapshot found. Starting with a fresh, empty TripleStore.");
            Files.createFile(storagePath);
        }
    }

    public void addTriple(TripleDto dto) throws TripleAlreadyExistsException {
        Triple triple = new Triple(dto.s(), dto.p(), dto.o());
        tripleStore.create(triple);
    }

    public TripleDto[] queryTriples(String s, String p, String o) throws TripleCodingException {
        List<Triple> results = tripleStore.query(s, p, o);
        return DTOFactory.tripleArr(results);
    }

    public TripleDto[] querySparql(String sparqlQuery) {
        List<Triple> results = tripleStore.query(sparqlQuery);
        return DTOFactory.tripleArr(results);
    }

    public void updateTriple(PutTriplesRequest request) throws TripleNotFoundException, TripleAlreadyExistsException {
        Triple oldT = new Triple(request.original().s(), request.original().p(), request.original().o());
        Triple newT = new Triple(request.update().s(), request.update().p(), request.update().o());
        tripleStore.update(oldT, newT);
    }

    public void deleteTriple(TripleDto dto) throws TripleNotFoundException {
        Triple triple = new Triple(dto.s(), dto.p(), dto.o());
        tripleStore.delete(triple);
    }
}