package com.github.jcodevandamme.semantics.app.services.domain.actor;

import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.util.HistoryDto;
import com.github.jcodevandamme.semantics.app.persistence.TripleLogger;
import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import com.github.jcodevandamme.semantics.app.services.domain.DomainActionException;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.MedState;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.Region;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.Ruler;
import com.github.jcodevandamme.semantics.app.services.domain.actor.data.State;
import com.github.jcodevandamme.semantics.app.services.domain.logger.HistoryLogger;
import com.github.jcodevandamme.semantics.app.services.domain.logger.DomainAction;
import com.github.jcodevandamme.semantics.app.services.domain.logger.TripleAction;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DomainActor {

    private final TripleStore store;
    private final TripleLogger tripleStoreLogger;
    private final HistoryLogger domainHistoryLogger;

    private final String MED_EX = "Mediatization failed. Necessary Triples or Relations are not present.\n Data Store corrupted.";



    public DomainActor(TripleStore store, TripleLogger tripleStoreLogger, HistoryLogger domainHistoryLogger) {
        this.store = store;
        this.tripleStoreLogger = tripleStoreLogger;
        this.domainHistoryLogger = domainHistoryLogger;
    }

    public int fetchStateCount() {
        int stateCount = 0;
        for (String state : fetchStateNames()) {
            if (exists(state, Vocab.Domain.IS_ACTIVE, "true")) {
                stateCount++;
            }
        }
        return stateCount;
    }

    public double fetchChangeFactor() {
        List<String> allStates = fetchStateNames();

        List<String> activeStates = allStates.stream()
                .filter(state -> exists(state, Vocab.Domain.IS_ACTIVE, "true"))
                .toList();

        int totalActive = activeStates.size();

        if (totalActive == 0) {
            return 0.0;
        }

        int originalActiveStates = 0;
        for (String state : activeStates) {
            if (exists(state, Vocab.Domain.IS_ORIGINAL, "true")) {
                originalActiveStates++;
            }
        }

        int newActiveStates = totalActive - originalActiveStates;

        return (double) newActiveStates / totalActive;
    }

    public List<State> fetchStateData() {
        List<State> states = new ArrayList<>();

        List<String> stateURIs = fetchStateNames();
        for (String stateURI : stateURIs) {

            if (!exists(stateURI, Vocab.Domain.IS_ACTIVE, "true")) {
                continue;
            }

            State state = new State();

            state.name = getSingleObjectValue(stateURI, Vocab.Rdfs.LABEL);
            state.ruler = fetchStateRuler(stateURI);
            state.regions = fetchStateRegions(stateURI);
            state.type = getSingleObjectValue(stateURI, Vocab.Domain.STATE_TYPE);
            state.mediatizatedStates = fetchMediatizatedStates(stateURI);
            state.population = fetchPopulation(stateURI);

            states.add(state);
        }
        return states;
    }

    private List<MedState> fetchMediatizatedStates(String stateURI) {
        List<Triple> medTriples = store.query(
                stateURI,
                Vocab.Domain.MEDIATIZED,
                null
        );

        return medTriples.stream()
                .map(t -> t.o().value().toString())
                .map(medStateURI -> {
                    MedState medState = new MedState();

                    medState.name = getSingleObjectValue(medStateURI, Vocab.Rdfs.LABEL);
                    medState.type = getSingleObjectValue(medStateURI, Vocab.Domain.STATE_TYPE);
                    medState.ruler = fetchStateRuler(medStateURI);

                    return medState;
                })
                .collect(Collectors.toList());
    }

    private Ruler fetchStateRuler(String stateURI) {
        String rulerURI = getSingleObjectValue(stateURI, Vocab.Domain.HAS_RULER);
        Ruler ruler = new Ruler();
        ruler.name = getSingleObjectValue(rulerURI, Vocab.Rdfs.LABEL);
        ruler.title = getSingleObjectValue(rulerURI, Vocab.Domain.RULER_TITLE);
        return ruler;
    }

    private List<Region> fetchStateRegions(String stateURI) {
        List<Triple> regionTriples = store.query(
                null,
                Vocab.Domain.LOCATED_IN,
                stateURI
        );
        return regionTriples.stream()
                .map(t -> t.s().value().toString())
                .map(this::fetchRegionData)
                .collect(Collectors.toList());
    }

    private Region fetchRegionData(String regionURI) {
        Region region = new Region();
        region.name = getSingleObjectValue(regionURI, Vocab.Rdfs.LABEL);
        region.type = getSingleObjectValue(regionURI, Vocab.Domain.REGION_TYPE);
        region.population = fetchPopulation(regionURI);
        return region;
    }

    private List<String> fetchStateNames() {
        List<Triple> states =  store.query(
                null,
                Vocab.Rdf.TYPE,
                Vocab.Domain.STATE
        );
        return states.stream()
                .map(t -> t.s().value().toString())
                .collect(Collectors.toList());
    }

    private int fetchPopulation(String subjectURI) {
        List<Triple> pops = store.query(null, Vocab.Domain.POPULATION, null);
        String popValue = pops.stream()
                .filter(tr -> tr.s().value().toString().equals(subjectURI))
                .map(tr -> tr.o().value().toString())
                .findFirst()
                .orElse(null);

        if (popValue != null) {
            try {
                return Integer.parseInt(popValue);
            } catch (NumberFormatException e) {
                System.err.println("PARSE INT FAILED FOR: " + subjectURI + " VAL: " + popValue);
                return 0;
            }
        } else {
            System.out.println("NO POP FOUND FOR: " + subjectURI);
            return 0;
        }
    }

    public HistoryDto mediatizate(String targetStateURI, String actingStateURI) throws DomainActionException, IOException {
        System.out.println("Mediatizating " + targetStateURI + " into " + actingStateURI);

        List<TripleAction> tripleActions = new ArrayList<>();
        boolean res;
        Triple t;

        try {
            // Early Fail if State is corrupted
            if (!exists(actingStateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)
                    || !exists(targetStateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)
                    || exists(actingStateURI, Vocab.Domain.IS_ACTIVE, "false")
                    ||exists(targetStateURI, Vocab.Domain.IS_ACTIVE, "false")) {
                throw new DomainActionException("Mediatization failed. States do not exist or are inactive.");
            }

            // Perform necessary updates

            // Write acting ont:mediatized target
            t = new Triple(actingStateURI, Vocab.Domain.MEDIATIZED, targetStateURI, false);
            res = store.create(t);
            if (!logIfSuccessful(res, tripleActions, UpdateType.ADD, t)) {
                throw new DomainActionException(MED_EX);
            }

            // Write target ont:isActive false
            performUpdate(
                    new Triple(targetStateURI, Vocab.Domain.IS_ACTIVE, "true", true),
                    new Triple(targetStateURI, Vocab.Domain.IS_ACTIVE, "false", true),
                    tripleActions
            );


            // Write acting.population acting.population + target.population
            int targetPop = fetchPopulation(targetStateURI);
            int currentActingPop = fetchPopulation(actingStateURI);

            if (targetPop != 0 && currentActingPop != 0) {
                performUpdate(
                        new Triple(actingStateURI, Vocab.Domain.POPULATION, String.valueOf(currentActingPop), true),
                        new Triple(actingStateURI, Vocab.Domain.POPULATION, String.valueOf(currentActingPop + targetPop), true),
                        tripleActions
                );
            }

            List<String> targetRegionURIs = store.query(null, Vocab.Rdf.TYPE, Vocab.Domain.REGION)
                    .stream()
                    .map(tr -> tr.s().value().toString())
                    .filter(sUri -> exists(sUri, Vocab.Domain.LOCATED_IN, targetStateURI))
                    .toList();

            for (String targetRegionURI : targetRegionURIs) {
                System.out.println(targetRegionURI);
                performUpdate(
                        new Triple(targetRegionURI, Vocab.Domain.LOCATED_IN, targetStateURI, false),
                        new Triple(targetRegionURI, Vocab.Domain.LOCATED_IN, actingStateURI, false),
                        tripleActions
                );
            }

            // If successful, commit Logs

            HistoryDto action = new HistoryDto(
                    DomainAction.MEDIATIZATION,
                    Instant.now(),
                    DTOFactory.tripleActionsToDTO(tripleActions)
            );

            domainHistoryLogger.logDomainAction(action);
            logTripleStoreActions(tripleActions);
            System.out.println("Mediatization done");
            return action;

        } catch (Exception ex) {
            throw new DomainActionException(MED_EX);
        }
    }

    private void performUpdate(Triple oldT, Triple newT, List<TripleAction> actions) throws DomainActionException {
        if (!store.update(oldT, newT)) {
            throw new DomainActionException(MED_EX);
        }
        logIfSuccessful(true, actions, UpdateType.DELETE, oldT);
        logIfSuccessful(true, actions, UpdateType.ADD, newT);
    }

    private boolean logIfSuccessful(boolean b, List<TripleAction> logs, UpdateType action, Triple t) {
        if (b) {
            logs.add(
                    new TripleAction(action, t)
            );
            return true;
        }
        return false;
    }

    private void logTripleStoreActions(List<TripleAction> actions) throws IOException {
        for (TripleAction a : actions) {
            tripleStoreLogger.registerUpdate(a.type(), a.triple());
        }
    }

    private String getSingleObjectValue(String s, String p) {
        List<Triple> results = store.query(s, p, null);
        return results.isEmpty() ? null : results.getFirst().o().value().toString();
    }
    private boolean exists(String s, String p, String o) {
        return !store.query(s, p, o).isEmpty();
    }
}
