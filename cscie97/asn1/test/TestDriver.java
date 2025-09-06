package cscie97.asn1.test;

import cscie97.asn1.knowledge.engine.ImportException;
import cscie97.asn1.knowledge.engine.Importer;
import cscie97.asn1.knowledge.engine.QueryEngine;
import cscie97.asn1.knowledge.engine.QueryEngineException;

/**
 * TestDriver is the entry point for importing triples into the knowledge graph and executing queries.
 * It expects two command-line arguments: the input file containing triples and the query file.
 * Usage: java -cp . cscie97.asn1.test.TestDriver <input file> <query file>
 */
public class TestDriver {

    /**
     * Main method that imports triples and executes queries.
     *
     * @param args command-line arguments: input file and query file
     * @throws ImportException if an error occurs during triple import
     * @throws QueryEngineException if an error occurs during query execution
     */
    public static void main(String[] args) throws ImportException, QueryEngineException {
        if(args.length != 2) {
            System.out.println("Usage: java -cp . cscie97.asn1.test.TestDriver <input file> <query file>");
            System.exit(1);
        }

        new Importer().importTripleFile(args[0]);
        new QueryEngine().executeQueryFile(args[1]);
    }
}
