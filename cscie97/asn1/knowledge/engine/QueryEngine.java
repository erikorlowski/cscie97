package cscie97.asn1.knowledge.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

/**
 * Provides methods for executing queries .
 */
public class QueryEngine {

    /**
     * Executes a single query.
     * The query must be a string containing three whitespace-separated terms (subject, predicate, object)
     * and must end with a period ('.'). Wildcards can be represented by '?'.
     * Prints the results to the terminal.
     *
     * @param query the query string to execute
     * @throws QueryEngineException if the query format is invalid
     */
    public void executeQuery(String query) throws QueryEngineException {
        if(query == null || query.trim().isEmpty()) {
            // For a blank query, simply proceed to the next query
            return;
        }

        if (query.trim().charAt(query.trim().length() - 1) != '.') {
            throw new QueryEngineException(query, "Query must end with a '.'");
        }

        // Print out the query
        System.out.println(query);

        query = query.trim();
        query = query.substring(0, query.length() - 1);

        String[] parts = query.trim().split("\\s+");
        if (parts.length != 3) {
            throw new QueryEngineException(query, "Invalid query format");
        }

        String subject = parts[0].trim();
        String predicate = parts[1].trim();
        String object = parts[2].trim();

        Set<Triple> results = KnowledgeGraph.getInstance().executeQuery(subject, predicate, object);

        if (results.isEmpty()) {
            System.out.println("<null>");
        } else {
            for (Triple triple : results) {
                System.out.println(triple.getIdentifier());
            }
        }

        System.out.println();
    }

    /**
     * Executes queries from a file, one per line.
     *
     * @param fileName the name of the file containing queries
     * @throws QueryEngineException if an error occurs while reading the file
     */
    public void executeQueryFile(String fileName) throws QueryEngineException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Strip BOM character
                // (I don't know why, but this was causing me issues)
                if (line.length() > 0 && line.charAt(0) == '\uFEFF') {
                    line = line.substring(1);
                }

                executeQuery(line);
            }
        } catch (Exception e) {
            throw new QueryEngineException("N/A", "Error reading query file: " + fileName);
        }
    }
}
