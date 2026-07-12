package com.github.jcodevandamme.semantics.app.controllers;

import com.github.jcodevandamme.semantics.app.dto.request.*;
import com.github.jcodevandamme.semantics.app.dto.response.*;
import com.github.jcodevandamme.semantics.app.dto.util.*;

import com.github.jcodevandamme.semantics.app.services.rdf.TripleService;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/triples")
public class TriplesController {

    private final TripleService service;

    public TriplesController(TripleService service) {
        this.service = service;
    }

    @PostMapping
    public synchronized HttpStatus createTriple(@Validated @RequestBody TripleDto triple) throws TripleAlreadyExistsException, IOException {
        if (!service.addTriple(triple)) {
            throw new TripleAlreadyExistsException();
        };
        return HttpStatus.CREATED;
    }
    @GetMapping
    public synchronized ResponseEntity<TripleQueryResponse> tripleQuery(
            @RequestParam(required = false) String s,
            @RequestParam(required = false) String p,
            @RequestParam(required = false) String o
            ) {
        TripleDto[] res = service.queryTriples(s, p, o);
        TripleQueryResponse response = new TripleQueryResponse(
                res.length,
                res
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sparql")
    public synchronized ResponseEntity<Object> sparqlQuery(@Validated @RequestBody String query) {
        Object res = service.querySparql(query);
        return ResponseEntity.ok(res);
    }

    @PutMapping
    public synchronized HttpStatus updateTriple(@Validated @RequestBody PutTriplesRequest request) throws TripleNotFoundException, TripleAlreadyExistsException, IOException {
        if (!service.updateTriple(request)) {
            throw new TripleNotFoundException();
        }
        return HttpStatus.OK;
    }

    @DeleteMapping
    public synchronized HttpStatus deleteTriple(@Validated @RequestBody TripleDto triple) throws TripleNotFoundException, IOException {
       if (!service.deleteTriple(triple)) {
           throw new TripleNotFoundException();
       }
        return HttpStatus.OK;
    }
}
