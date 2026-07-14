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
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
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

    private final String MED_EX =
            "Mediatization failed. Necessary Triples or Relations are not present.\n Data Store corrupted.";
    private final String RULER_EX =
            "Ruler Change failed. Necessary Triples or Relations are not present.\n Data Store corrupted.";
    private final String STATE_EX =
            "State founding failed. Necessary Triples or Relations are not present.\n Data Store corrupted.";




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

        double result = (double) newActiveStates / totalActive;
        return Math.round(result * 100.0) / 100.0;
    }

    public List<State> fetchStateData(boolean filterActiveStates) {
        List<State> states = new ArrayList<>();

        List<String> stateURIs = fetchStateNames();
        for (String stateURI : stateURIs) {

            if (filterActiveStates && !exists(stateURI, Vocab.Domain.IS_ACTIVE, "true")) {
                continue;
            }

            State state = new State();

            state.name = getSingleObjectValue(stateURI, Vocab.Rdfs.LABEL);
            state.URI = stateURI;
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
        ruler.URI = rulerURI;
        ruler.title = getSingleObjectValue(rulerURI, Vocab.Domain.RULER_TITLE);
        return ruler;
    }

    private List<Region> fetchStateRegions(String stateURI) {
        List<Triple> regionTriples = store.query(
                null,
                Vocab.Rdf.TYPE,
                Vocab.Domain.REGION
        );
        return regionTriples.stream()
                .map(t -> t.s().value().toString())
                .filter(rURI -> exists(rURI, Vocab.Domain.LOCATED_IN, stateURI))
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

    public HistoryDto mediatizate(String targetStateURI, String actingStateURI) throws DomainActionException, TripleNotFoundException, IOException {
        System.out.println("Mediatizating " + targetStateURI + " into " + actingStateURI);

        List<TripleAction> tripleActions = new ArrayList<>();
        boolean res;
        Triple t;

        // Early Fail if Params are invalid
        if (!exists(actingStateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)) {
            throw new TripleNotFoundException();
        }
        if (exists(actingStateURI, Vocab.Domain.MEDIATIZED, targetStateURI)) {
            throw new TripleAlreadyExistsException();
        }

        if (!exists(targetStateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)
                || exists(actingStateURI, Vocab.Domain.IS_ACTIVE, "false")
                ||exists(targetStateURI, Vocab.Domain.IS_ACTIVE, "false")) {
            throw new DomainActionException("Mediatization failed. States are inactive.");
        }

        // Perform necessary updates

        // actingStateURI ont:mediatized targetStateURI
        t = new Triple(actingStateURI, Vocab.Domain.MEDIATIZED, targetStateURI, false);
        performCreate(t, tripleActions, MED_EX);

        // targetStateURI ont:isActive false ;
        performUpdate(
                new Triple(targetStateURI, Vocab.Domain.IS_ACTIVE, "true", true),
                new Triple(targetStateURI, Vocab.Domain.IS_ACTIVE, "false", true),
                tripleActions,
                MED_EX
        );

        int targetPop = fetchPopulation(targetStateURI);
        int currentActingPop = fetchPopulation(actingStateURI);

        if (targetPop != 0 && currentActingPop != 0) {
            performUpdate(
                    new Triple(actingStateURI, Vocab.Domain.POPULATION, String.valueOf(currentActingPop), true),
                    new Triple(actingStateURI, Vocab.Domain.POPULATION, String.valueOf(currentActingPop + targetPop), true),
                    tripleActions,
                    MED_EX
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
                    tripleActions,
                    MED_EX
            );
        }

        // If successful, commit Logs
        HistoryDto action = generateHistory(tripleActions, DomainAction.MEDIATIZATION);
        commitLogs(action, tripleActions);

        System.out.println("Mediatization done");
        return action;
    }

    public HistoryDto performRulerUpdate(String stateURI, String rulerURI, String labelLiteral, String titleLiteral) throws DomainActionException, IOException {
        System.out.println("Changing Ruler of " + stateURI + rulerURI + " , " + titleLiteral);

        List<TripleAction> tripleActions = new ArrayList<>();
        Triple newT;
        Triple oldT;

        // Early Fail if Params are invalid
        if (!exists(stateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)) {
            throw new TripleNotFoundException();
        }

        // data:rulerURI a ont:Ruler
        if (!exists(rulerURI, Vocab.Rdf.TYPE, Vocab.Domain.RULER)) {
            newT = new Triple(rulerURI, Vocab.Rdf.TYPE, Vocab.Domain.RULER);
            performCreate(newT, tripleActions, RULER_EX);
        }

        // data:rulerURI rdfs:label labelLiteral
        if (!exists(rulerURI, Vocab.Rdfs.LABEL, labelLiteral)) {
            newT = new Triple(rulerURI, Vocab.Rdfs.LABEL, labelLiteral, true);
            performCreate(newT, tripleActions, RULER_EX);
        }

        // data:rulerURI ont:rulerTitle titleLiteral
        if (!exists(rulerURI, Vocab.Domain.RULER_TITLE, titleLiteral)) {
            newT = new Triple(labelLiteral,  Vocab.Domain.RULER_TITLE, titleLiteral, true);
            performCreate(newT, tripleActions, RULER_EX);
        }

        String currentRulerURI = getSingleObjectValue(stateURI, Vocab.Domain.HAS_RULER);

        // stateURI ont:hasRuler current
        oldT = new Triple(stateURI, Vocab.Domain.HAS_RULER, currentRulerURI);
        // stateURI ont:hasRuler rulerURI
        newT = new Triple(stateURI, Vocab.Domain.HAS_RULER, rulerURI);

        performUpdate(oldT, newT, tripleActions, RULER_EX);

        // If successful, commit
        HistoryDto action = generateHistory(tripleActions, DomainAction.RULER_CHANGE);
        commitLogs(action, tripleActions);

        System.out.println("Ruler Change done");
        return action;
    }

    public HistoryDto createState(String stateURI, String rulerURI, String populationLiteral, String stateLabelLiteral, String stateTypeLiteral) throws DomainActionException, IOException {
        System.out.println(
                "Creating State: " + stateLabelLiteral + ", " + stateTypeLiteral
                + " with URI: " + stateURI +
                ",\nRuler:" + rulerURI + ", Pop: " + populationLiteral)
        ;

        List<TripleAction> tripleActions = new ArrayList<>();
        Triple t;

        // Early Fail if Params are invalid
        if (!exists(rulerURI, Vocab.Rdf.TYPE, Vocab.Domain.RULER)) {
            throw new TripleNotFoundException();
        }
        if (exists(stateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE)) {
            throw new TripleAlreadyExistsException();
        }

        // data:state rdf:type ont:State
        t = new Triple(stateURI, Vocab.Rdf.TYPE, Vocab.Domain.STATE);
        performCreate(t, tripleActions, STATE_EX);

        // data:state rdfs:label labelLiteral
        t = new Triple(stateURI, Vocab.Rdfs.LABEL, stateLabelLiteral, true);
        performCreate(t, tripleActions, STATE_EX);

        // data:state ont:hasRuler ruler
        t = new Triple(stateURI, Vocab.Domain.HAS_RULER, rulerURI);
        performCreate(t, tripleActions, STATE_EX);

        // data:state ont:population population
        t = new Triple(stateURI, Vocab.Domain.POPULATION, populationLiteral, true);
        performCreate(t, tripleActions, STATE_EX);

        // data:state ont:stateType label
        t = new Triple(stateURI, Vocab.Domain.STATE_TYPE, stateLabelLiteral, true);
        performCreate(t, tripleActions, STATE_EX);

        // data:state ont:isOriginalState false
        t = new Triple(stateURI, Vocab.Domain.IS_ORIGINAL, "false", true);
        performCreate(t, tripleActions, STATE_EX);

        // data:state ont:isActive true
        t = new Triple(stateURI, Vocab.Domain.IS_ACTIVE, "true", true);
        performCreate(t, tripleActions, STATE_EX);

        // If successful, commit
        HistoryDto action = generateHistory(tripleActions, DomainAction.STATE_FOUNDING);
        commitLogs(action, tripleActions);

        System.out.println("State Creation done");
        return action;
    }

    private void performUpdate(Triple oldT, Triple newT, List<TripleAction> actions, String exception) throws DomainActionException {
        if (!store.update(oldT, newT)) {
            throw new DomainActionException(exception);
        }
        logIfSuccessful(true, actions, UpdateType.DELETE, oldT);
        logIfSuccessful(true, actions, UpdateType.ADD, newT);
    }

    private void performCreate(Triple t, List<TripleAction> actions, String exception) throws DomainActionException {
        if (!store.create(t)) {
            throw new DomainActionException(exception);
        }
        logIfSuccessful(true, actions, UpdateType.ADD, t);
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
        //List<Triple> results = store.query(s, p, null);
        //return results.isEmpty() ? null : results.getFirst().o().value().toString();
        List<Triple> allTriplesForSubject = store.query(s, null, null);
        return allTriplesForSubject.stream()
                .filter(t -> t.p().value().toString().equals(p))
                .map(t -> t.o().value().toString())
                .findFirst()
                .orElse(null);
    }

    private boolean exists(String s, String p, String o) {
        return !store.query(s, p, o).isEmpty();
    }

    private static HistoryDto generateHistory (List<TripleAction> tripleActions, DomainAction domainAction) {
        return new HistoryDto(
                domainAction,
                Instant.now(),
                DTOFactory.tripleActionsToDTO(tripleActions)
        );
    }

    private void commitLogs(HistoryDto action, List<TripleAction> tripleActions) throws IOException {
        domainHistoryLogger.logDomainAction(action);
        logTripleStoreActions(tripleActions);
    }
}
