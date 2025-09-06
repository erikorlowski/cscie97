package cscie97.asn1.knowledge.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles importing triples from a file into the knowledge graph.
 */
public class Importer {
    /**
     * Reads triples from the specified file and processes each line.
     * Each line must contain at least three whitespace-separated words: subject, predicate, and object.
     *
     * @param fileName the name of the file containing triples
     * @throws Exception if an error occurs while reading the file or parsing a line
     */
    public void importTripleFile(String fileName) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.charAt(line.length() - 1) != '.') {
                    throw new ImportException("Triple must end with a '.' at line " + lineNumber + ": " + line);
                }
                line = line.substring(0, line.length() - 1).trim();

                String[] parts = line.trim().split("\\s+");
                if (parts.length != 3) {
                    throw new ImportException("Invalid triple format at line " + lineNumber + ": " + line);
                }
                String subject = parts[0].trim();
                String predicate = parts[1].trim();
                String object = parts[2].trim();

                KnowledgeGraph.getInstance().importTriple(subject, predicate, object);
            }
        } catch (IOException e) {
            throw new ImportException("Error reading file: " + fileName);
        }
    }
}
