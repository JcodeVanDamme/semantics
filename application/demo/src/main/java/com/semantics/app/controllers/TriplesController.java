package com.semantics.app.controllers;

import com.semantics.app.dto.request.PutTriplesRequest;
import com.semantics.app.dto.util.TripleDto;
import com.semantics.app.dto.response.TripleQueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/triples")
public class TriplesController {

    @PostMapping
    public HttpStatus createTriple(@Validated @RequestBody TripleDto triple) {

        return HttpStatus.CREATED;
    }
    @GetMapping
    public ResponseEntity<TripleQueryResponse> tripleQuery(@RequestParam String s, @RequestParam String p, @RequestParam String o) {

        TripleQueryResponse response = new TripleQueryResponse(
                1,
                new TripleDto[] {
                        new TripleDto(
                                s,
                                p,
                                o
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sparql")
    public ResponseEntity<TripleQueryResponse> sparqlQuery(@Validated @RequestBody String query) {

        TripleQueryResponse response = new TripleQueryResponse(
                1,
                new TripleDto[] {
                        new TripleDto(
                                "s",
                                "p",
                                "o"
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public HttpStatus updateTriple(@Validated @RequestBody PutTriplesRequest request) {

        return HttpStatus.OK;
    }

    @DeleteMapping
    public HttpStatus deleteTriple(@Validated @RequestBody TripleDto triple) {

        return HttpStatus.OK;
    }
}
