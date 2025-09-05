package cscie97.asn1.knowledge.engine;

/**
 * Defines a unique Predicate, which represents the relationship between a subject and an object.
 */
public class Predicate {
    private final String identifier;
    private final long createDate;

    /**
     * Constructs a Predicate with the specified identifier and sets the creation date
     * to the current UNIX timestamp (seconds since epoch).
     *
     * @param identifier the unique identifier for this Predicate
     */
    public Predicate(String identifier) {
        this.identifier = identifier.trim();
        this.createDate = System.currentTimeMillis() / 1000L; // Set to current UNIX timestamp
    }

    /**
     * Returns the unique identifier for this Predicate.
     *
     * @return the unique identifier of the Predicate
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the creation date of this Predicate as a UNIX timestamp (seconds since epoch).
     *
     * @return the creation date as a UNIX timestamp
     */
    public long getCreateDate() {
        return createDate;
    }
}