package cscie97.asn1.knowledge.engine;

/**
 * Defines a unique Triple, which represents a statement consisting of a subject (Node),
 * a predicate (Predicate), and an object (Node).
 */
public class Triple {
    private final Node subject;
    private final Predicate predicate;
    private final Node object;
    private final long createDate;
    private final String identifier;

    /**
     * Constructs a Triple with the specified subject, predicate, and object,
     * and sets the creation date to the current UNIX timestamp (seconds since epoch).
     * The identifier is a concatenation of the subject, predicate, and object identifiers separated by spaces.
     *
     * @param subject   the subject Node of the Triple
     * @param predicate the Predicate of the Triple
     * @param object    the object Node of the Triple
     */
    public Triple(Node subject, Predicate predicate, Node object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.createDate = System.currentTimeMillis() / 1000L; // Set to current UNIX timestamp
        this.identifier = subject.getIdentifier() + " " + predicate.getIdentifier() + " " + object.getIdentifier() + ".";
    }

    /**
     * Returns the subject Node of this Triple.
     *
     * @return the subject Node
     */
    public Node getSubject() {
        return subject;
    }

    /**
     * Returns the Predicate of this Triple.
     *
     * @return the Predicate
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * Returns the object Node of this Triple.
     *
     * @return the object Node
     */
    public Node getObject() {
        return object;
    }

    /**
     * Returns the creation date of this Triple as a UNIX timestamp (seconds since epoch).
     *
     * @return the creation date as a UNIX timestamp
     */
    public long getCreateDate() {
        return createDate;
    }

    /**
     * Returns the identifier of this Triple, which is a concatenation of the subject,
     * predicate, and object identifiers separated by spaces.
     *
     * @return the identifier of the Triple
     */
    public String getIdentifier() {
        return identifier;
    }
}