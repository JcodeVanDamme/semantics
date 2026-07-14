package com.github.jcodevandamme.semantics.app.dto;

import com.github.jcodevandamme.semantics.app.dto.util.*;
import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.MedState;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.Region;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.State;
import com.github.jcodevandamme.semantics.app.services.domain.logger.TripleAction;
import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.ArrayList;
import java.util.List;

public class DTOFactory {

    public static TripleDto[] tripleArr(List<Triple> triples) {
        TripleDto[] res = new TripleDto[triples.size()];
        for (int i = 0; i < triples.size(); i++) {
            res[i] = triple(triples.get(i));
        }
        return res;
    }

    public static TripleDto triple(Triple t) {
        return new TripleDto(
                new RDFObjectDTO((String) t.s().value(), false),
                new RDFObjectDTO((String) t.p().value(), false),
                new RDFObjectDTO((String) t.o().value(), t.o().isLiteral())
        );
    }

    public static TripleActionDto[] tripleActionsToDTO(List<TripleAction> actions) {
        TripleActionDto[] dtos = new TripleActionDto[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            dtos[i] = new TripleActionDto(
                    actionToString(actions.get(i).type()),
                    DTOFactory.triple(actions.get(i).triple())
            );
        }
        return dtos;
    }

    public static StateDto[] statesToDTO(List<State> states) {
        StateDto[] dtos = new StateDto[states.size()];
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            dtos[i] = new StateDto(
                    state.name,
                    state.URI,
                    state.type,
                    state.population,
                    new RulerDto(
                            state.ruler.name,
                            state.ruler.URI,
                            state.ruler.title
                    ),
                    regionsToDTO(state.regions),
                    medStatesToDTO(state.mediatizatedStates)
            );
        }
        return dtos;
    }

    private static MediatizatedStateDto[] medStatesToDTO(List<MedState> medStates) {
        MediatizatedStateDto[] dtos = new MediatizatedStateDto[medStates.size()];
        for (int i = 0; i < medStates.size(); i++) {
            MedState medState = medStates.get(i);
            dtos[i] = new MediatizatedStateDto(
                    medState.name,
                    medState.type,
                    new RulerDto(
                            medState.ruler.name,
                            medState.ruler.URI,
                            medState.ruler.title
                    )
            );
        }
        return dtos;
    }
    private static RegionDto[] regionsToDTO(List<Region> regions) {
        RegionDto[] dtos = new RegionDto[regions.size()];
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            dtos[i] = new RegionDto(
                    region.name,
                    region.type,
                    region.population
            );
        }
        return dtos;
    }

    private static String actionToString(UpdateType t) {
        switch (t) {
            case ADD -> {
                return "Created";
            }
            case DELETE -> {
                return "Deleted";
            }
            default -> {
                return "Unknown";
            }
        }
    }
}
