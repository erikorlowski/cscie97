package cscie97.asn1.test;

import cscie97.asn1.knowledge.engine.ImportException;
import cscie97.asn1.knowledge.engine.Importer;
import cscie97.asn1.knowledge.engine.QueryEngine;
import cscie97.asn1.knowledge.engine.QueryEngineException;

public class TestDriver {
    public static void main(String[] args) throws ImportException, QueryEngineException {
        if(args.length != 2) {
            System.out.println("Usage: java -cp . cscie97.asn1.test.TestDriver <input file> <query file>");
            System.exit(1);
        }

        new Importer().importTripleFile(args[0]);
        new QueryEngine().executeQueryFile(args[1]);
    }
}
