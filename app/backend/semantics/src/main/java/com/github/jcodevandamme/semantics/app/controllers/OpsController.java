package com.github.jcodevandamme.semantics.app.controllers;

import com.github.jcodevandamme.semantics.app.dto.request.*;
import com.github.jcodevandamme.semantics.app.dto.response.*;
import com.github.jcodevandamme.semantics.app.dto.util.*;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ops")
public class OpsController {

    /*@GetMapping("/activeStates")
    public ResponseEntity<CountResponse> getActiveStateCount() {

        CountResponse response = new CountResponse(0);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stateChanges")
    public ResponseEntity<FactorResponse> getStateChangeFactor() {

        FactorResponse response = new FactorResponse(0.0f);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/states")
    public ResponseEntity<StateResponse> getStates() {

        StateResponse response = new StateResponse(
                1,
                new StateDto[] {
                        new StateDto(
                                "Statename",
                                new RulerDto(
                                        "Rulername",
                                        "Title"
                                ),
                                new MediatizatedStatesDto(
                                        1,
                                        new MediatizatedStateDto[] {
                                                new MediatizatedStateDto(
                                                        "Statename",
                                                        "Some Type"
                                                )
                                        }
                                ),
                                new RegionsDto(
                                        1,
                                        new RegionDto[]{
                                                new RegionDto(
                                                        "Regionname",
                                                        "Some Type"
                                                )
                                        }
                                ),
                                0,
                                "Some Type"
                        )
                }
        );

        return ResponseEntity.ok(response);
    }

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
