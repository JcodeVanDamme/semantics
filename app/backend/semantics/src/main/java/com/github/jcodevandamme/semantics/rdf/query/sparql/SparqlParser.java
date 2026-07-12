package com.github.jcodevandamme.semantics.rdf.query.sparql;

import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import java.util.ArrayList;
import java.util.List;

public class SparqlParser {

    public static SparqlQuery parseSparql(String queryStr, TripleDictionary dict) {
        try {
            org.apache.jena.query.Query jenaQuery = org.apache.jena.query.QueryFactory.create(queryStr);

            // --- A. WHERE-BLOCK EXTRAHIEREN (Deine bestehende Logik) ---
            Element patternElement = jenaQuery.getQueryPattern();
            if (!(patternElement instanceof ElementGroup)) {
                throw new TripleCodingException("Unsupported SPARQL structure. Expected a basic group block.");
            }

            ElementGroup group = (ElementGroup) patternElement;
            List<ParsedPattern> parsedPatterns = new ArrayList<>();

            for (Element element : group.getElements()) {
                if (element instanceof ElementPathBlock) {
                    ElementPathBlock block = (ElementPathBlock) element;

                    for (TriplePath triplePath : block.getPattern()) {
                        Triple jenaTriple = triplePath.asTriple();

                        String sVar = jenaTriple.getSubject().isVariable() ? jenaTriple.getSubject().getName() : null;
                        Integer encodedS = sVar == null ? mapNodeToId(jenaTriple.getSubject(), true, dict) : null;

                        String pVar = jenaTriple.getPredicate().isVariable() ? jenaTriple.getPredicate().getName() : null;
                        Integer encodedP = pVar == null ? mapNodeToId(jenaTriple.getPredicate(), false, dict) : null;

                        String oVar = jenaTriple.getObject().isVariable() ? jenaTriple.getObject().getName() : null;
                        Integer encodedO = oVar == null ? mapNodeToId(jenaTriple.getObject(), true, dict) : null;

                        parsedPatterns.add(new ParsedPattern(encodedS, sVar, encodedP, pVar, encodedO, oVar));
                    }
                }
            }

            if (parsedPatterns.isEmpty()) {
                throw new TripleCodingException("No executable triple patterns found in SPARQL query.");
            }

            // Kapselung der Muster in unser JoinQuery (selbst wenn es nur 1 Pattern ist,
            // läuft es durch die evaluateJoin-Pipeline)
            JoinQuery whereClause = new JoinQuery(parsedPatterns);

            // --- B. QUERY-TYP IDENTIFIZIEREN & WRAPPER BAUEN ---
            if (jenaQuery.isSelectType()) {
                // Holt die Variablen aus "SELECT ?x ?y"
                List<String> selectVars = jenaQuery.getResultVars();
                return new SparqlQuery(whereClause, selectVars);

            } else if (jenaQuery.isConstructType()) {
                // Holt die Konstruktions-Templates aus dem CONSTRUCT { ... } Block
                List<Triple> jenaTemplates = jenaQuery.getConstructTemplate().getTriples();
                List<ParsedPattern> parsedTemplates = new ArrayList<>();

                for (Triple jenaTriple : jenaTemplates) {
                    String sVar = jenaTriple.getSubject().isVariable() ? jenaTriple.getSubject().getName() : null;
                    Integer encodedS = sVar == null ? mapNodeToId(jenaTriple.getSubject(), true, dict) : null;

                    String pVar = jenaTriple.getPredicate().isVariable() ? jenaTriple.getPredicate().getName() : null;
                    Integer encodedP = pVar == null ? mapNodeToId(jenaTriple.getPredicate(), false, dict) : null;

                    String oVar = jenaTriple.getObject().isVariable() ? jenaTriple.getObject().getName() : null;
                    Integer encodedO = oVar == null ? mapNodeToId(jenaTriple.getObject(), true, dict) : null;

                    parsedTemplates.add(new ParsedPattern(encodedS, sVar, encodedP, pVar, encodedO, oVar));
                }
                return new SparqlQuery(whereClause, parsedTemplates, true);

            } else if (jenaQuery.isDescribeType()) {
                // Holt die Variablen aus "DESCRIBE ?x"
                List<Node> resultNodes = jenaQuery.getResultURIs(); // Fall 1: Konkrete URIs im Header
                List<String> describeVars = new ArrayList<>();

                // Fall 2: Variablen im Header
                if (jenaQuery.getProject() != null) {
                    jenaQuery.getProject().getVars().forEach(v -> describeVars.add(v.getVarName()));
                }

                return new SparqlQuery(describeVars, whereClause);
            }

            throw new TripleCodingException("Unsupported SPARQL Query Type (Only SELECT, CONSTRUCT, DESCRIBE allowed).");

        } catch (Exception ex) {
            throw new TripleCodingException("SPARQL Translation failed.");
        }
    }

    private static Integer mapNodeToId(Node node, boolean isSubjectOrObject, TripleDictionary dict) {
        if (node.isVariable()) return null;
        if (node.isURI()) {
            return isSubjectOrObject ? dict.encodeSO(node.getURI()) : dict.encodeP(node.getURI());
        }
        if (node.isLiteral()) {
            return dict.encodeSO(node.getLiteralLexicalForm());
        }
        return null;
    }
}