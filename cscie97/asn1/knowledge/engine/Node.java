package cscie97.asn1.knowledge.engine;

/**
 * Defines a unique Node, which can be used as both a subject or object.
 */
public class Node {
    private final String identifier;
    private final long createDate;

    /**
     * Constructs a Node with the specified identifier and sets the creation date
     * to the current UNIX timestamp (seconds since epoch).
     *
     * @param identifier the unique identifier for this Node
     */
    public Node(String identifier) {
        this.identifier = identifier.trim();
        this.createDate = System.currentTimeMillis() / 1000L; // Set to current UNIX timestamp
    }

    /**
     * Returns the unique identifier for this Node.
     *
     * @return the unique identifier of the Node
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the creation date of this Node as a UNIX timestamp (seconds since epoch).
     *
     * @return the creation date as a UNIX timestamp
     */
    public long getCreateDate() {
        return createDate;
    }
}