package cscie97.asn4.housemate.model;

/**
 * Represents an occupant in the housemate model. An Occupant is a ModelObject
 * that carries a type, status, and known/unknown flag. The fully qualified name
 * is used as the registry key and the simple name is derived from it.
 */
class Occupant implements ModelObject {
    private String name;
    private OccupantType type;
    private OccupantStatus status;
    private boolean isKnown;
    private String fullyQualifiedName;

    /**
     * Create a new Occupant.
     *
     * @param fullyQualifiedName the unique fully qualified name for the occupant (e.g. "occupant_John")
     * @param type the OccupantType for this occupant
     */
    public Occupant(String fullyQualifiedName, OccupantType type) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.type = type;
        this.status = OccupantStatus.ACTIVE;
        this.isKnown = false;
    }

    /**
     * Get the short name of the occupant (the portion after "occupant_").
     *
     * @return the occupant name
     */
    public String getName() {
        return fullyQualifiedName.split("occupant_", 2)[1];
    }

    /**
     * Get the occupant's type.
     *
     * @return the OccupantType of this occupant
     */
    public OccupantType getType() {
        return type;
    }

    /**
     * Get the occupant's status (e.g. ACTIVE, INACTIVE).
     *
     * @return the OccupantStatus of this occupant
     */
    public OccupantStatus getStatus() {
        return status;
    }

    /**
     * Indicates whether this occupant is known to the system.
     *
     * @return true if the occupant is known; false otherwise
     */
    public boolean isKnown() {
        return isKnown;
    }

    /**
     * Get the fully qualified name used as the registry key.
     *
     * @return the fully qualified name of the occupant
     */
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
