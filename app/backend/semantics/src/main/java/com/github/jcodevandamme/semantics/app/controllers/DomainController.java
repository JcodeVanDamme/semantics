package com.github.jcodevandamme.semantics.app.controllers;


import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.request.ChangeRulerRequest;
import com.github.jcodevandamme.semantics.app.dto.request.FoundStateRequest;
import com.github.jcodevandamme.semantics.app.dto.request.MediatizationRequest;
import com.github.jcodevandamme.semantics.app.dto.response.*;
import com.github.jcodevandamme.semantics.app.dto.util.HistoryDto;
import com.github.jcodevandamme.semantics.app.services.domain.DomainService;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.State;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/semantics.rdf.system")
public class DomainController {

    private final DomainService service;
    public DomainController(DomainService service) {
        this.service = service;
    }

    @GetMapping("/activeStateCount")
    public ResponseEntity<CountResponse> getActiveStateCount() {
        int count = service.countActiveStates();
        CountResponse res = new CountResponse(count);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/stateChanges")
    public ResponseEntity<FactorResponse> getStateChangeFactor() {
        double factor = service.calculateStateChangeFactor();
        FactorResponse res = new FactorResponse(factor);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/states")
    public ResponseEntity<StateResponse> getStates(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<State> states = service.getStates(activeOnly);
        StateResponse res = new StateResponse(DTOFactory.statesToDTO(states));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/mediatizate")
    public ResponseEntity<HistoryResponse> mediatizateState(@Validated @RequestBody MediatizationRequest request) throws IOException {
        if (mediatizationRequestNotValid(request)) {
            return ResponseEntity.badRequest().build();
        }
        HistoryDto history = service.mediatizate(request);
        HistoryResponse res = new HistoryResponse(history);
        return ResponseEntity.ok(res);
    }

    private boolean mediatizationRequestNotValid(MediatizationRequest req) {
        return req == null || req.into() == null || req.absorbed() == null;
    }

    @GetMapping("/history")
    public ResponseEntity<HistoryResponse> getHistory() {
        HistoryDto[] history = service.getHistory();
        HistoryResponse res = new HistoryResponse(history);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/changeRuler")
    public ResponseEntity<HistoryResponse> changeStateRuler(@Validated @RequestBody ChangeRulerRequest request) throws IOException {
        if (changeRulerRequestNotValid(request)) {
            return ResponseEntity.badRequest().build();
        }
        HistoryDto history = service.changeRuler(request);
        HistoryResponse res = new HistoryResponse(history);
        return ResponseEntity.ok(res);
    }

    private boolean changeRulerRequestNotValid(ChangeRulerRequest req) {
        return req == null || req.ruler() == null ||req.label() == null
                || req.state() == null || req.title() == null;
    }

    @PostMapping("/foundState")
    public ResponseEntity<HistoryResponse> foundState(@Validated @RequestBody FoundStateRequest request) throws IOException {
        if (foundStateRequestRequestNotValid(request)) {
            return ResponseEntity.badRequest().build();
        }
        HistoryDto history = service.foundState(request);
        HistoryResponse res = new HistoryResponse(history);
        return ResponseEntity.ok(res);
    }

    private boolean foundStateRequestRequestNotValid(FoundStateRequest req) {
        return req == null || req.ruler() == null || req.label() == null
                || req.state() == null || req.type() == null || req.type() == null;
    }
}
