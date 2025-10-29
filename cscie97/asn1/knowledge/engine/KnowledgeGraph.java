package cscie97.asn1.knowledge.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class KnowledgeGraph {

    // Singleton instance
    private static KnowledgeGraph instance;

    /* Note: Using TreeMap allows for automatic handling of case-insensitive keys
       with the tradeoff that put operations are now O(log n) instead of O(1) for a HashMap.
       I believe this the correct tradeoff to simplify the implementation of the application.
       */
    private final Map<String, Node> nodeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Predicate> predicateMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Triple> tripleMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Set<Triple>> queryMapSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    // Private constructor to prevent instantiation
    private KnowledgeGraph() {}

    /**
     * Returns the singleton instance of KnowledgeGraph.
     *
     * @return the singleton instance
     */
    public static synchronized KnowledgeGraph getInstance() {
        if (instance == null) {
            instance = new KnowledgeGraph();
        }
        return instance;
    }

    /**
     * Returns the Node with the specified identifier. If it does not exist, creates it.
     *
     * @param identifier the unique identifier for the Node with leading or trailing whitespace removed
     * @return the Node with the specified identifier
     */
    public Node getNode(String identifier) {
        nodeMap.putIfAbsent(identifier, new Node(identifier));
        return nodeMap.get(identifier);
    }

    /**
     * Returns the Predicate with the specified identifier. If it does not exist, creates it.
     *
     * @param identifier the unique identifier for the Predicate with leading or trailing whitespace removed
     * @return the Predicate with the specified identifier
     */
    public Predicate getPredicate(String identifier) {
        predicateMap.putIfAbsent(identifier, new Predicate(identifier));
        return predicateMap.get(identifier);
    }

    /**
     * Returns the Triple with the specified subject, predicate, and object. If it does not exist,
     * creates a new Triple, stores it, and returns it. The Triple is uniquely identified by the
     * concatenation of the subject, predicate, and object identifiers separated by spaces.
     *
     * @param subject   the subject Node of the Triple
     * @param predicate the Predicate of the Triple
     * @param object    the object Node of the Triple
     * @return the Triple with the specified subject, predicate, and object
     */
    public Triple getTriple(Node subject, Predicate predicate, Node object) {
        Triple tripleToAdd = new Triple(subject, predicate, object);
        tripleMap.putIfAbsent(tripleToAdd.getIdentifier(), tripleToAdd);
        return tripleMap.get(tripleToAdd.getIdentifier());
    }

    /**
     * Imports a triple into the knowledge graph using the provided subject, predicate, and object identifiers.
     * This method updates the nodeMap, predicateMap, tripleMap, and queryMapSet accordingly.
     *
     * @param subject   the identifier for the subject Node
     * @param predicate the identifier for the Predicate
     * @param object    the identifier for the object Node
     */
    public void importTriple(String subject, String predicate, String object) {
        Node subjectNode = getNode(subject);
        Predicate predicateObject = getPredicate(predicate);
        Node objectNode = getNode(object);
        Triple triple = getTriple(subjectNode, predicateObject, objectNode);

        addPotentialQueryToSet(triple.getIdentifier(), triple);
        addPotentialQueryToSet(subjectNode.getIdentifier() + " " + predicateObject.getIdentifier() + " ?.", triple);
        addPotentialQueryToSet(subjectNode.getIdentifier() + " ? " + objectNode.getIdentifier() + ".", triple); 
        addPotentialQueryToSet(subjectNode.getIdentifier() + " ? ?.", triple);
        addPotentialQueryToSet("? " + predicateObject.getIdentifier() + " " + objectNode.getIdentifier() + ".", triple);
        addPotentialQueryToSet("? " + predicateObject.getIdentifier() + " ?.", triple);
        addPotentialQueryToSet("? ? " + objectNode.getIdentifier() + ".", triple);
        addPotentialQueryToSet("? ? ?.", triple);
    }

    /**
     * Executes a query against the knowledge graph using the provided subject, predicate, and object identifiers.
     * Wildcards can be represented by the string "?" for any of the arguments. The method returns a set of Triples
     * that match the query pattern, or an empty set if no matches are found.
     *
     * @param subject   the identifier for the subject Node, or "?" as a wildcard
     * @param predicate the identifier for the Predicate, or "?" as a wildcard
     * @param object    the identifier for the object Node, or "?" as a wildcard
     * @return a set of Triples matching the query pattern, or an empty set if none are found
     */
    public Set<Triple> executeQuery(String subject, String predicate, String object) {
        String queryKey = subject + " " + predicate + " " + object + ".";
        return queryMapSet.getOrDefault(queryKey, java.util.Collections.emptySet());
    }

    /**
     * Adds the specified triple to the set associated with the given identifier in the query map.
     * If the set does not exist, it is created and added to the map.
     *
     * @param identifier the query pattern identifier
     * @param triple     the Triple to add to the set
     */
    private void addPotentialQueryToSet(String identifier, Triple triple) {
        queryMapSet.putIfAbsent(identifier, new java.util.HashSet<>());
        Set<Triple> querySet = queryMapSet.get(identifier);
        querySet.add(triple);
    }

    /**
     * Remove a single Triple from the internal maps and query sets.
     * This updates tripleMap and removes the triple from any query set that
     * referenced it. Empty query sets are removed.
     *
     * @param triple the Triple to remove
     */
    private void removeTriple(Triple triple) {
        if (triple == null) return;
        // remove from tripleMap
        tripleMap.remove(triple.getIdentifier());

        // remove from all query sets; collect empty keys to remove afterwards
        List<String> emptyKeys = new ArrayList<>();
        for (Map.Entry<String, Set<Triple>> e : queryMapSet.entrySet()) {
            Set<Triple> set = e.getValue();
            if (set.remove(triple)) {
                if (set.isEmpty()) {
                    emptyKeys.add(e.getKey());
                }
            }
        }
        for (String k : emptyKeys) {
            queryMapSet.remove(k);
        }
    }

    /**
     * Remove all triples that match the given subject identifier and predicate identifier.
     * Matching is case-insensitive.
     *
     * @param subject the subject identifier to match (not null)
     * @param predicate the predicate identifier to match (not null)
     */
    public void removeTriplesBySubjectAndPredicate(String subject, String predicate) {
        if (subject == null || predicate == null) return;
        List<Triple> toRemove = new ArrayList<>();
        for (Triple t : tripleMap.values()) {
            if (t.getSubject().getIdentifier().equalsIgnoreCase(subject)
                    && t.getPredicate().getIdentifier().equalsIgnoreCase(predicate)) {
                toRemove.add(t);
            }
        }
        for (Triple t : toRemove) {
            removeTriple(t);
        }
        // cleanup nodes/predicates that are no longer referenced
        cleanupNodeIfUnused(subject);
        cleanupPredicateIfUnused(predicate);
    }

    /**
     * Remove all triples that mention the given subject (either as subject or object).
     * Matching is case-insensitive.
     *
     * @param subject the subject identifier to remove
     */
    public void removeAllMentionsOfSubject(String subject) {
        if (subject == null) return;
        List<Triple> toRemove = new ArrayList<>();
        for (Triple t : tripleMap.values()) {
            if (t.getSubject().getIdentifier().equalsIgnoreCase(subject)
                    || t.getObject().getIdentifier().equalsIgnoreCase(subject)) {
                toRemove.add(t);
            }
        }
        for (Triple t : toRemove) removeTriple(t);
        cleanupNodeIfUnused(subject);
    }

    /**
     * Remove all triples that mention the given predicate.
     * Matching is case-insensitive.
     *
     * @param predicate the predicate identifier to remove
     */
    public void removeAllMentionsOfPredicate(String predicate) {
        if (predicate == null) return;
        List<Triple> toRemove = new ArrayList<>();
        for (Triple t : tripleMap.values()) {
            if (t.getPredicate().getIdentifier().equalsIgnoreCase(predicate)) {
                toRemove.add(t);
            }
        }
        for (Triple t : toRemove) removeTriple(t);
        cleanupPredicateIfUnused(predicate);
    }

    private void cleanupNodeIfUnused(String nodeId) {
        if (nodeId == null) return;
        for (Triple t : tripleMap.values()) {
            if (t.getSubject().getIdentifier().equalsIgnoreCase(nodeId)
                    || t.getObject().getIdentifier().equalsIgnoreCase(nodeId)) {
                return; // still in use
            }
        }
        nodeMap.remove(nodeId);
    }

    private void cleanupPredicateIfUnused(String predicateId) {
        if (predicateId == null) return;
        for (Triple t : tripleMap.values()) {
            if (t.getPredicate().getIdentifier().equalsIgnoreCase(predicateId)) {
                return; // still in use
            }
        }
        predicateMap.remove(predicateId);
    }

}
