package cscie97.asn4.housemate.entitlement;

/**
 * Base class for all entitlement types in the system. Concrete subclasses
 * include Permission (see file Permission.java) and Role (see file
 * Role.java). Entitlement is the component in the Composite design pattern
 * where Role acts as the composite node and Permission acts as a leaf.
 */
public abstract class Entitlement implements Visitable {
    protected String id;
    protected String name;
    protected String description;

    /**
     * Construct an Entitlement with identifying information.
     *
     * @param id the entitlement identifier
     * @param name the human readable name
     * @param description the description
     */
    public Entitlement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Get the entitlement identifier.
     *
     * @return the id
     */
    public String getId() { return id; }

    /**
     * Get the entitlement name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Get the entitlement description.
     *
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Determine whether this entitlement grants the provided permission.
     * Concrete subclasses implement the evaluation logic (for example,
     * Permission compares identifiers and Role checks children).
     *
     * @param permission the permission to check
     * @return true if the permission is granted
     */
    public abstract boolean checkAccess(Permission permission);
}
