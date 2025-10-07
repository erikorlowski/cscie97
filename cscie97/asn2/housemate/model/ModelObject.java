package cscie97.asn2.housemate.model;

/**
 * Interface for all model objects in the housemate system.
 */
public interface ModelObject {
    /**
     * Get the name of the object.
     *
     * @return the name of the object
     */
    public String getName();

    /**
     * Get the fully qualified name of the object.
     *
     * @return the fully qualified name of the object
     */
    public String getFullyQualifiedName();
}