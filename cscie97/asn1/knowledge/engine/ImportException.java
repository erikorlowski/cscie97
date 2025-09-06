/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cscie97.asn1.knowledge.engine;

/**
 * Exception thrown to indicate an error occurred during the import of triples into the knowledge graph.
 */
public class ImportException extends Exception {

    /**
     * Constructs a new ImportException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImportException(String message) {
        super(message);
    }

}
