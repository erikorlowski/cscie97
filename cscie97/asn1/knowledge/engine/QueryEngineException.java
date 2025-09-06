package cscie97.asn1.knowledge.engine;

/**
 * Exception thrown to indicate an error occurred during the execution of a query in the knowledge graph.
 * The exception message contains the query that caused the exception, followed by the reason.
 */
public class QueryEngineException extends Exception {

    /**
     * Constructs a new QueryEngineException with the specified query and reason.
     *
     * @param query  the query that caused the exception
     * @param reason the reason for the exception
     */
    public QueryEngineException(String query, String reason) {
        super("Query: \"" + query + "\" failed. Message: " + reason);
    }
}
