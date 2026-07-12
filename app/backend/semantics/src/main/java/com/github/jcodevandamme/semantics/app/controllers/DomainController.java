package com.github.jcodevandamme.semantics.app.controllers;


import com.github.jcodevandamme.semantics.app.dto.response.CountResponse;
import com.github.jcodevandamme.semantics.app.dto.response.FactorResponse;
import com.github.jcodevandamme.semantics.app.dto.response.StateResponse;
import com.github.jcodevandamme.semantics.app.dto.util.StateDto;
import com.github.jcodevandamme.semantics.app.services.semantics.DomainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /*
    @PostMapping("/mediatizate")
    public ResponseEntity<TripleLogResponse> mediatizateState(@Validated @RequestBody MediatizationRequest request) {

        TripleLogResponse response = new TripleLogResponse(
                1,
                new TripleActionDto[]{
                        new TripleActionDto(
                                "Exampleaction",
                                new TripleDto(
                                        "s",
                                        "p",
                                        "p"
                                )
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/changeRuler")
    public ResponseEntity<TripleLogResponse> changeStateRuler(@Validated @RequestBody ChangeRulerRequest request) {

        TripleLogResponse response = new TripleLogResponse(
                1,
                new TripleActionDto[]{
                        new TripleActionDto(
                                "Exampleaction",
                                new TripleDto(
                                        "s",
                                        "p",
                                        "p"
                                )
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/foundState")
    public ResponseEntity<TripleLogResponse> foundState(@Validated @RequestBody FoundStateRequest request) {

        TripleLogResponse response = new TripleLogResponse(
                1,
                new TripleActionDto[]{
                        new TripleActionDto(
                                "Exampleaction",
                                new TripleDto(
                                        "s",
                                        "p",
                                        "p"
                                )
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<TripleLogResponse> getHistory() {

        TripleLogResponse response = new TripleLogResponse(
                1,
                new TripleActionDto[]{
                        new TripleActionDto(
                                "Exampleaction",
                                new TripleDto(
                                        "s",
                                        "p",
                                        "p"
                                )
                        )
                }
        );

        return ResponseEntity.ok(response);
    }*/
}
