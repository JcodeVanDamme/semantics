package com.github.jcodevandamme.semantics.app.services.semantics.fetcher;

import com.github.jcodevandamme.semantics.app.services.semantics.fetcher.data.MedState;
import com.github.jcodevandamme.semantics.app.services.semantics.fetcher.data.Region;
import com.github.jcodevandamme.semantics.app.services.semantics.fetcher.data.Ruler;
import com.github.jcodevandamme.semantics.app.services.semantics.fetcher.data.State;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomainFetcher {

    private final TripleStore store;

    public DomainFetcher(TripleStore store) {
        this.store = store;
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
        System.out.println("Fetching Change Factor");

        List<String> allStates = fetchStateNames();
        int totalStates = allStates.size();

        if (totalStates == 0) {
            return 0.0;
        }

        int originalStates = 0;
        for (String state : allStates) {
            List<Triple> res = store.query(
                    state,
                    Vocab.Domain.IS_ORIGINAL,
                    "true"
            );
            if (!res.isEmpty()) {
                originalStates++;
            }
        }

        int newStates = totalStates - originalStates;

        double changeFactor = (double) newStates / totalStates;

        System.out.println(
                "State Change Factor: "
                + changeFactor
                + " (" + newStates + " new / " + totalStates + " total)"
        );
        return changeFactor;
    }

    public List<State> fetchStateData() {
        List<State> states = new ArrayList<>();

        List<String> stateURIs = fetchStateNames();
        for (String stateURI : stateURIs) {

            State state = new State();

            state.name = getSingleObjectValue(stateURI, Vocab.Rdfs.LABEL, null);
            state.ruler = fetchStateRuler(stateURI);
            state.regions = fetchStateRegions(stateURI);
            state.type = getSingleObjectValue(stateURI, Vocab.Domain.STATE_TYPE, null);
            state.mediatizatedStates = fetchMediatizatedStates(stateURI);

            String popString = getSingleObjectValue(stateURI, Vocab.Domain.POPULATION, null);
            if (popString != null) {
                try {
                    state.population = Integer.parseInt(popString);
                } catch (NumberFormatException e) {
                    state.population = 0;
                }
            } else {
                state.population = 0;
            }

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

                    medState.name = getSingleObjectValue(medStateURI, Vocab.Rdfs.LABEL, null);
                    medState.type = getSingleObjectValue(medStateURI, Vocab.Domain.STATE_TYPE, null);
                    medState.ruler = fetchStateRuler(medStateURI);

                    return medState;
                })
                .collect(Collectors.toList());
    }

    private Ruler fetchStateRuler(String stateURI) {
        String rulerURI = getSingleObjectValue(stateURI, Vocab.Domain.HAS_RULER, null);
        Ruler ruler = new Ruler();
        ruler.name = getSingleObjectValue(rulerURI, Vocab.Rdfs.LABEL, null);
        ruler.title = getSingleObjectValue(rulerURI, Vocab.Domain.RULER_TITLE, null);
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
        region.name = getSingleObjectValue(regionURI, Vocab.Rdfs.LABEL, null);
        region.type = getSingleObjectValue(regionURI, Vocab.Domain.REGION_TYPE, null);

        String popString = getSingleObjectValue(regionURI, Vocab.Domain.POPULATION, null);
        if (popString != null) {
            try {
                region.population = Integer.parseInt(popString);
            } catch (NumberFormatException e) {
                region.population = 0;
            }
        } else {
            region.population = 0;
        }

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
    private String getSingleObjectValue(String s, String p, String o) {
        List<Triple> results = store.query(s, p, o);
        return results.isEmpty() ? null : results.get(0).o().value().toString();
    }
    private String getSingleSubjectValue(String s, String p, String o) {
        List<Triple> results = store.query(s, p, o);
        return results.isEmpty() ? null : results.get(0).s().value().toString();
    }
    private boolean exists(String s, String p, String o) {
        return !store.query(s, p, o).isEmpty();
    }
}
