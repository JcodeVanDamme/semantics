package com.github.jcodevandamme.semantics.app.controllers;


import com.github.jcodevandamme.semantics.app.dto.request.MediatizationRequest;
import com.github.jcodevandamme.semantics.app.dto.response.CountResponse;
import com.github.jcodevandamme.semantics.app.dto.response.FactorResponse;
import com.github.jcodevandamme.semantics.app.dto.response.StateResponse;
import com.github.jcodevandamme.semantics.app.dto.response.TripleLogResponse;
import com.github.jcodevandamme.semantics.app.dto.util.HistoryDto;
import com.github.jcodevandamme.semantics.app.services.domain.DomainService;
import com.github.jcodevandamme.semantics.app.services.domain.logger.DomainAction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;

@RestController
@RequestMapping("/semantics.rdf.system")
public class DomainController {

    private final DomainService service;
    public DomainController(DomainService service) {
        this.service = service;
    }

    @GetMapping("/activeStates")
    public ResponseEntity<CountResponse> getActiveStateCount() {

        CountResponse response = service.countActiveStates();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stateChanges")
    public ResponseEntity<FactorResponse> getStateChangeFactor() {

        FactorResponse response = service.calculateStateChangeFactor();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/states")
    public ResponseEntity<StateResponse> getStates() {

        StateResponse response = service.getStates();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/mediatizate")
    public ResponseEntity<HistoryDto> mediatizateState(@Validated @RequestBody MediatizationRequest request) throws IOException {
        HistoryDto response = service.mediatizate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<HistoryDto[]> getHistory() {
        HistoryDto[] response = service.getHistory();
        return ResponseEntity.ok(response);
    }

    /*@PostMapping("/changeRuler")
    public ResponseEntity<TripleLogResponse> changeStateRuler(@Validated @RequestBody ChangeRulerRequest request) {

    }

    @PostMapping("/foundState")
    public ResponseEntity<TripleLogResponse> foundState(@Validated @RequestBody FoundStateRequest request) {

    }*/
}
