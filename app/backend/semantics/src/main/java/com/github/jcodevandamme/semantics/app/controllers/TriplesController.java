package com.github.jcodevandamme.semantics.app.controllers;

import com.github.jcodevandamme.semantics.app.dto.request.*;
import com.github.jcodevandamme.semantics.app.dto.response.*;
import com.github.jcodevandamme.semantics.app.dto.util.*;

import com.github.jcodevandamme.semantics.app.services.TripleService;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/triples")
public class TriplesController {

    private final TripleService service;

    public TriplesController(TripleService service) {
        this.service = service;
    }

    @PostMapping
    public HttpStatus createTriple(@Validated @RequestBody TripleDto triple) {
        service.addTriple(triple);
        return HttpStatus.CREATED;
    }
    @GetMapping
    public ResponseEntity<TripleQueryResponse> tripleQuery(
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
    public ResponseEntity<TripleQueryResponse> sparqlQuery(@Validated @RequestBody String query) {
        TripleDto[] res = service.querySparql(query);
        TripleQueryResponse response = new TripleQueryResponse(
                res.length,
                res
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public HttpStatus updateTriple(@Validated @RequestBody PutTriplesRequest request) {
        service.updateTriple(request);
        return HttpStatus.OK;
    }

    @DeleteMapping
    public HttpStatus deleteTriple(@Validated @RequestBody TripleDto triple) {
        service.deleteTriple(triple);
        return HttpStatus.OK;
    }
}
