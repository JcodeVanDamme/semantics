package com.github.jcodevandamme.semantics.app.controllers;

import com.github.jcodevandamme.semantics.app.services.domain.DomainActionException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TripleAlreadyExistsException.class)
    public ResponseEntity<String> handleDuplicateTriple(TripleAlreadyExistsException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Triple already exists.\n");
    }

    @ExceptionHandler({TripleNotFoundException.class, TripleCodingException.class})
    public ResponseEntity<String> handleMissingTriple(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Triple not found.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServiceError(RuntimeException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleSerializationError(IOException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error encountered during Serialization. " + ex.getMessage());
    }

    @ExceptionHandler(DomainActionException.class)
    public ResponseEntity<String> handleDomainError(DomainActionException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
