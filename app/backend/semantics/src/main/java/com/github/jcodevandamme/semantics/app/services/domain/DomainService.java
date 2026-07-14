package com.github.jcodevandamme.semantics.app.services.domain;

import com.github.jcodevandamme.semantics.app.dto.request.ChangeRulerRequest;
import com.github.jcodevandamme.semantics.app.dto.request.FoundStateRequest;
import com.github.jcodevandamme.semantics.app.dto.request.MediatizationRequest;
import com.github.jcodevandamme.semantics.app.dto.response.CountResponse;
import com.github.jcodevandamme.semantics.app.dto.response.FactorResponse;
import com.github.jcodevandamme.semantics.app.dto.response.StateResponse;
import com.github.jcodevandamme.semantics.app.dto.util.StateDto;
import com.github.jcodevandamme.semantics.app.dto.util.*;
import com.github.jcodevandamme.semantics.app.services.domain.actor.DomainActor;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.MedState;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.Region;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.State;
import com.github.jcodevandamme.semantics.app.services.domain.logger.HistoryLogger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DomainService {

    private final DomainActor domain;
    private final HistoryLogger logger;

    public DomainService(
            DomainActor domain,
            HistoryLogger logger
    ) {
        this.domain = domain;
        this.logger = logger;
    }

    public CountResponse countActiveStates() {
        return new CountResponse(domain.fetchStateCount());
    }

    public FactorResponse calculateStateChangeFactor() {
        return new FactorResponse(domain.fetchChangeFactor());
    }

    public StateResponse getStates(boolean filterActiveStates) {
        List<State> states = domain.fetchStateData(filterActiveStates);
        return new StateResponse(statesToDTO(states));
    }

    public HistoryDto mediatizate(MediatizationRequest req) throws IOException, DomainActionException {
        String target = req.absorbed();
        String into = req.into();
        return domain.mediatizate(target, into);
    }

    public HistoryDto changeRuler(ChangeRulerRequest req) throws DomainActionException, IOException {
        String state = req.state();
        String ruler = req.ruler();
        String label = req.label();
        String title = req.title();

        return domain.performRulerUpdate(state, ruler, label, title);
    }

    public HistoryDto[] getHistory() {
        return logger.flushHistory();
    }

    public HistoryDto foundState(FoundStateRequest req) throws IOException {
        String state = req.state();
        String ruler = req.ruler();
        String label = req.label();
        String type = req.type();
        String pop = String.valueOf(req.population());

        return domain.createState(state, ruler, pop, label, type);
    }

    private StateDto[] statesToDTO(List<State> states) {
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

    private MediatizatedStateDto[] medStatesToDTO(List<MedState> medStates) {
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
    private RegionDto[] regionsToDTO(List<Region> regions) {
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
}
