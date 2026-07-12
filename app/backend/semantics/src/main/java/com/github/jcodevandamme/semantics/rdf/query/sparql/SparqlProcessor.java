package com.github.jcodevandamme.semantics.rdf.query.sparql;

import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;
import com.github.jcodevandamme.semantics.rdf.query.TripleQueryProcessor;
import com.github.jcodevandamme.semantics.rdf.query.TripleQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparqlProcessor {

    private final TripleQueryProcessor tripleProcessor;

    public SparqlProcessor(TripleQueryProcessor tripleProcessor) {
        this.tripleProcessor = tripleProcessor;
    }

    public SparqlResult execute(SparqlQuery query) {
        switch (query.getType()) {
            case SELECT:
                List<Map<String, Integer>> selectBindings = processSelect(query.getWhereClause(), query.getSelectVariables());
                return new SparqlResult(selectBindings, SparqlQuery.Type.SELECT);

            case CONSTRUCT:
                List<EncodedTriple> constructedTriples = processConstruct(query.getWhereClause(), query.getConvertTemplates());
                return new SparqlResult(constructedTriples, SparqlQuery.Type.CONSTRUCT, true);

            case DESCRIBE:
                List<EncodedTriple> describedTriples = processDescribe(query.getWhereClause(), query.getDescribeVariables());
                return new SparqlResult(describedTriples, SparqlQuery.Type.DESCRIBE, true);

            default:
                throw new IllegalArgumentException("Unbekannter SPARQL-Query-Typ");
        }
    }

    /**
     * Kern-Methode: Berechnet die reinen Variablen-Bindungen (die "Tabelle")
     * für eine Join-Abfrage. Das ist die Basis für alle SPARQL-Abfragetypen.
     */
    public List<Map<String, Integer>> evaluateJoin(JoinQuery joinQuery) {
        List<ParsedPattern> patterns = joinQuery.getPatterns();

        List<Map<String, Integer>> currentBindings = new ArrayList<>();
        // Start-Zustand: Eine leere Bindung, damit die Schleife anläuft
        currentBindings.add(new HashMap<>());

        for (ParsedPattern pattern : patterns) {
            List<Map<String, Integer>> nextBindings = new ArrayList<>();

            for (Map<String, Integer> binding : currentBindings) {
                Integer s = pattern.sVar() != null ? binding.get(pattern.sVar()) : pattern.sId();
                Integer p = pattern.pVar() != null ? binding.get(pattern.pVar()) : pattern.pId();
                Integer o = pattern.oVar() != null ? binding.get(pattern.oVar()) : pattern.oId();

                TripleQuery subQuery = new TripleQuery(s, p, o);
                List<EncodedTriple> intermediateResults = tripleProcessor.process(subQuery);

                if (intermediateResults.isEmpty()) {
                    System.out.println("[DEBUG] Matrix-Lookup ergab 0 Treffer für Query: "
                            + "S:" + s + " P:" + p + " O:" + o);
                }

                for (EncodedTriple triple : intermediateResults) {
                    Map<String, Integer> newBinding = new HashMap<>(binding);
                    boolean validMatch = true;

                    // Typ-Korrektur auf primitive ints, um Integer vs Long Fehler zu vermeiden
                    if (pattern.sVar() != null) {
                        int tripleS = ((Number) triple.s()).intValue();
                        if (newBinding.containsKey(pattern.sVar())) {
                            int existingS = ((Number) newBinding.get(pattern.sVar())).intValue();
                            if (existingS != tripleS) validMatch = false;
                        }
                        newBinding.put(pattern.sVar(), tripleS);
                    }

                    if (pattern.pVar() != null) {
                        int tripleP = ((Number) triple.p()).intValue();
                        if (newBinding.containsKey(pattern.pVar())) {
                            int existingP = ((Number) newBinding.get(pattern.pVar())).intValue();
                            if (existingP != tripleP) validMatch = false;
                        }
                        newBinding.put(pattern.pVar(), tripleP);
                    }

                    if (pattern.oVar() != null) {
                        int tripleO = ((Number) triple.o()).intValue();
                        if (newBinding.containsKey(pattern.oVar())) {
                            int existingO = ((Number) newBinding.get(pattern.oVar())).intValue();
                            if (existingO != tripleO) validMatch = false;
                        }
                        newBinding.put(pattern.oVar(), tripleO);
                    }

                    if (validMatch) {
                        nextBindings.add(newBinding);
                    }
                }
            }
            currentBindings = nextBindings;

            if (currentBindings.isEmpty()) {
                return new ArrayList<>();
            }
        }

        return currentBindings;
    }

    /**
     * 1. SELECT-Modus:
     * Filtert die generierten Bindungen auf die vom Nutzer gewünschten Variablen (Projection).
     * Gibt eine Liste von Zeilen (Maps) zurück, die exakt den Spalten des SELECTs entsprechen.
     */
    public List<Map<String, Integer>> processSelect(JoinQuery joinQuery, List<String> selectVariables) {
        List<Map<String, Integer>> allBindings = evaluateJoin(joinQuery);
        List<Map<String, Integer>> projectedResults = new ArrayList<>();

        for (Map<String, Integer> fullBinding : allBindings) {
            Map<String, Integer> projectedBinding = new HashMap<>();
            for (String var : selectVariables) {
                // Nur Variablen übernehmen, die im SELECT gefordert wurden (z.B. ?state, ?title)
                if (fullBinding.containsKey(var)) {
                    projectedBinding.put(var, fullBinding.get(var));
                }
            }
            projectedResults.add(projectedBinding);
        }

        return projectedResults;
    }

    /**
     * 2. CONSTRUCT-Modus:
     * Nutzt die Bindungen aus dem WHERE-Teil, um anhand eines "Templates" (vorgegebene Triple-Muster)
     * völlig neue Triple zu konstruieren.
     */
    public List<EncodedTriple> processConstruct(JoinQuery joinQuery, List<ParsedPattern> constructTemplates) {
        List<Map<String, Integer>> allBindings = evaluateJoin(joinQuery);
        List<EncodedTriple> constructedTriples = new ArrayList<>();

        for (Map<String, Integer> binding : allBindings) {
            for (ParsedPattern template : constructTemplates) {
                // Werte aus der "Tabelle" in das Triple-Template einsetzen
                Integer s = template.sVar() != null ? binding.get(template.sVar()) : template.sId();
                Integer p = template.pVar() != null ? binding.get(template.pVar()) : template.pId();
                Integer o = template.oVar() != null ? binding.get(template.oVar()) : template.oId();

                // Nur wenn alle Variablen des Templates auch erfolgreich gebunden wurden,
                // erzeugen wir ein gültiges RDF-Triple.
                if (s != null && p != null && o != null) {
                    constructedTriples.add(new EncodedTriple(s, p, o));
                }
            }
        }

        return constructedTriples;
    }

    /**
     * 3. DESCRIBE-Modus:
     * Gibt alle bekannten Triple aus dem Store zurück, bei denen die gefundenen
     * Ressourcen entweder als Subjekt oder als Objekt vorkommen.
     */
    public List<EncodedTriple> processDescribe(JoinQuery joinQuery, List<String> describeVariables) {
        List<Map<String, Integer>> allBindings = evaluateJoin(joinQuery);
        List<EncodedTriple> descriptionGraph = new ArrayList<>();

        // Um Duplikate im Ergebnisgraphen zu vermeiden
        java.util.Set<Integer> resourcesToDescribe = new java.util.HashSet<>();

        // Schritt A: Sammle alle Ressourcen-IDs, die beschrieben werden sollen
        for (Map<String, Integer> binding : allBindings) {
            for (String var : describeVariables) {
                if (binding.containsKey(var)) {
                    resourcesToDescribe.add(binding.get(var));
                }
            }
        }

        // Schritt B: Hole alle Triple aus dem Store, in denen diese Ressourcen stecken
        for (Integer resourceId : resourcesToDescribe) {
            if (resourceId == null) continue;

            // 1. Ressource als Subjekt (?resource ?p ?o)
            TripleQuery subjectQuery = new TripleQuery(resourceId, null, null);
            descriptionGraph.addAll(tripleProcessor.process(subjectQuery));

            // 2. Ressource als Objekt (?s ?p ?resource)
            TripleQuery objectQuery = new TripleQuery(null, null, resourceId);
            descriptionGraph.addAll(tripleProcessor.process(objectQuery));
        }

        return descriptionGraph;
    }
}