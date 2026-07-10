package com.github.jcodevandamme.semantics.app.controllers;

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
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Triple already exists.");
    }

    @ExceptionHandler({TripleNotFoundException.class, TripleCodingException.class})
    public ResponseEntity<String> handleMissingTriple(TripleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Triple not found.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServiceError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleSerializationError(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error encountered during Serialization. " + ex.getMessage());
    }
}
