package cscie97.asn2.housemate.model;

import cscie97.asn1.knowledge.engine.KnowledgeGraph;
import cscie97.asn1.knowledge.engine.Triple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The ModelServiceApi class provides a singleton API for managing model objects in the housemate system.
 * It maintains a registry of model objects and interacts with the knowledge graph to record relationships.
 */
public class ModelServiceApi {
    private static ModelServiceApi instance = null;
    private Map <String, ModelObject> modelObjects = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private KnowledgeGraph knowledgeGraph = KnowledgeGraph.getInstance();

    private ModelServiceApi() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the singleton instance of the ModelServiceApi.
     *
     * @return the singleton ModelServiceApi instance
     */
    public static ModelServiceApi getInstance() {
        if (instance == null) {
            instance = new ModelServiceApi();
        }
        return instance;
    }

    /**
     * Add a model object to the registry.
     *
     * @param modelObject the ModelObject to add; its fully qualified name is used as the key
     */
    public void addModelObject(ModelObject modelObject) {
        modelObjects.put(modelObject.getFullyQualifiedName(), modelObject);
    }

    /**
     * Retrieve a model object by its fully qualified name.
     *
     * @param fullyQualifiedName the fully qualified name of the model object
     * @return the ModelObject if found, or null if not present
     */
    public ModelObject getModelObject(String fullyQualifiedName) {
        return modelObjects.get(fullyQualifiedName);
    }

    /**
     * Record an ownership relationship between two model objects in the knowledge graph.
     *
     * @param owner the owning ModelObject
     * @param owned the owned ModelObject
     */
    public void addOwnership(ModelObject owner, ModelObject owned) {
        knowledgeGraph.importTriple(owner.getFullyQualifiedName(), "has_a", owned.getFullyQualifiedName());
    }

    /**
     * Gets all the objects directly owned by the specified owner.
     * @param owner The object owner.
     * @return A set of fully qualified names of the owned objects.
     */
    public Set<String> getOwnedObjects(ModelObject owner) {
        Set<Triple> triples = knowledgeGraph.executeQuery(owner.getFullyQualifiedName(), "has_a", "?");
        Set<String> ownedObjects = new HashSet<>();
        for (Triple triple : triples) {
            ownedObjects.add(triple.getObject().getIdentifier());
        }
        return ownedObjects;
    }

    /**
     * Execute a script file by reading each line and passing it to the CommandParser.
     *
     * @param filename path to the script file
     * @throws IllegalArgumentException if the file cannot be read
     */
    public void executeScript(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                CommandParser.getInstance().executeCommand(line, lineNumber);
                lineNumber++;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to execute script: " + filename, e);
        }
    }

    /**
     * Return a map of all registered model objects. The returned map is the internal registry
     * and is case-insensitive on keys.
     *
     * @return map of fully qualified name -> ModelObject
     */
    public Map<String, ModelObject> getAllModelObjects() {
        return modelObjects;
    }
}
