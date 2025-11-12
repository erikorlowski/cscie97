package cscie97.asn2.housemate.model;

import cscie97.asn1.knowledge.engine.KnowledgeGraph;
import cscie97.asn1.knowledge.engine.Triple;
import cscie97.asn3.housemate.controller.StatusObserver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The ModelServiceApi class provides a singleton API for managing model objects in the housemate system.
 * It maintains a registry of model objects and interacts with the knowledge graph to record relationships.
 */
public class ModelServiceApiImpl implements ModelServiceApi {
    private static ModelServiceApiImpl instance = null;
    private Map <String, ModelObject> modelObjects = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private KnowledgeGraph knowledgeGraph = KnowledgeGraph.getInstance();
    private final ArrayList<StatusObserver> statusObservers = new ArrayList<>();

    private ModelServiceApiImpl() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the singleton instance of the ModelServiceApi.
     *
     * @return the singleton ModelServiceApi instance
     */
    public static ModelServiceApiImpl getInstance() {
        if (instance == null) {
            instance = new ModelServiceApiImpl();
        }
        return instance;
    }

    /**
     * Add a model object to the registry.
     *
     * @param modelObject the ModelObject to add; its fully qualified name is used as the key
     */
    void addModelObject(ModelObject modelObject) {
        modelObjects.putIfAbsent(modelObject.getFullyQualifiedName(), modelObject);
    }

    /**
     * Retrieve a model object by its fully qualified name.
     *
     * @param fullyQualifiedName the fully qualified name of the model object
     * @return the ModelObject if found, or null if not present
     */
    ModelObject getModelObject(String fullyQualifiedName) {
        return modelObjects.get(fullyQualifiedName);
    }

    /**
     * Record an ownership relationship between two model objects in the knowledge graph.
     *
     * @param owner the owning ModelObject
     * @param owned the owned ModelObject
     */
    void addOwnership(ModelObject owner, ModelObject owned) {
        knowledgeGraph.importTriple(owner.getFullyQualifiedName(), "has_a", owned.getFullyQualifiedName());
    }

    /**
     * Gets all the objects directly owned by the specified owner.
     * @param owner The object owner.
     * @return A set of fully qualified names of the owned objects.
     */
    Set<String> getOwnedObjects(ModelObject owner) {
        Set<Triple> triples = knowledgeGraph.executeQuery(owner.getFullyQualifiedName(), "has_a", "?");
        Set<String> ownedObjects = new HashSet<>();
        for (Triple triple : triples) {
            ownedObjects.add(triple.getObject().getIdentifier());
        }
        return ownedObjects;
    }

    /**
     * Execute a command by reading each line and passing it to the CommandParser.
     *
     * @param commandText the command text to execute
     * @param authenticationKey the authentication key for executing commands
     * @throws IllegalArgumentException if the command cannot be executed
     *
     * @return the output of the command execution, or null if it failed
     */
    @Override
    public String executeCommand(String commandText, char[] authenticationKey) {
        try {
            String output = CommandParser.getInstance().executeCommand(commandText);
            return output;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Return a map of all registered model objects. The returned map is the internal registry
     * and is case-insensitive on keys.
     *
     * @return map of fully qualified name -> ModelObject
     */
    Map<String, ModelObject> getAllModelObjects() {
        return modelObjects;
    }

    /**
     * Attach a status observer to the model service.
     *
     * @param observer the status observer to attach
     */
    @Override
    public void attachStatusObserver(StatusObserver observer) {
        statusObservers.add(observer);
    }

    /**
     * Notify all attached status observers of a status change.
     * 
     * @param device the device name
     * @param status the status name
     * @param newValue the new value of the status
     * @param deviceType the type of the device
     */
    @Override
    public void notifyStatusObservers(String device, String status, String newValue, String deviceType) {
        for (StatusObserver observer : statusObservers) {
            observer.onStatusUpdate(modelFullyQualifiedNameToControllerFullyQualifiedName(device), 
                status, newValue, deviceType);
        }
    }

    private String modelFullyQualifiedNameToControllerFullyQualifiedName(String modelFQN) {
        if (modelFQN == null) {
            return null;
        }
        // Remove any occurrences of the specified prefixes (case-insensitive)
        return modelFQN.replaceAll("(?i)(house_|room_|device_|sensor_|appliance_)", "");
    }
}
