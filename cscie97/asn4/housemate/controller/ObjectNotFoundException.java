package cscie97.asn3.housemate.controller;

/**
 * Exception thrown when a referenced object cannot be found in the Housemate
 * Model/Controller context.
 *
 * <p>This exception carries the name of the missing object which can be
 * retrieved via {@link #getObjectName()} for logging or error reporting.</p>
 */
public class ObjectNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /** The fully-qualified name of the object that was not found. */
    private final String objectName;

    /**
     * Construct a new ObjectNotFoundException for the specified object name.
     *
     * @param objectName the fully-qualified name of the missing object
     */
    public ObjectNotFoundException(String objectName) {
        super("Object not found: " + objectName);
        this.objectName = objectName;
    }

    /**
     * Get the name of the object that could not be located.
     *
     * @return the object name passed to the constructor
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Get the exception message.
     *
     * @return the exception message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
