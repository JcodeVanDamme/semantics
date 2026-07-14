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

    public int countActiveStates() {
        return domain.fetchStateCount();
    }

    public double calculateStateChangeFactor() {
        return domain.fetchChangeFactor();
    }

    public List<State> getStates(boolean filterActiveStates) {
        return domain.fetchStateData(filterActiveStates);
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
}
