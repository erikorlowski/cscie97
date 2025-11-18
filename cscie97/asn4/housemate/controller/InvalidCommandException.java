package cscie97.asn3.housemate.controller;

/**
 * Exception thrown when a provided controller command is invalid.
 *
 * This exception contains the original command text and a short reason
 * describing why it was considered invalid. Callers can obtain a single
 * human-readable description by calling getInvalidCommandMessage().
 */
public class InvalidCommandException extends Exception {

    private static final long serialVersionUID = 1L;

    /** The raw command text that failed validation. */
    private final String commandText;

    /** A short reason explaining why the command is invalid. */
    private final String reason;

    /**
     * Construct a new InvalidCommandException.
     *
     * @param commandText the original command text
     * @param reason a short, human-readable reason the command is invalid
     */
    public InvalidCommandException(String commandText, String reason) {
        super("Invalid command: " + reason + " - " + commandText);
        this.commandText = commandText;
        this.reason = reason;
    }

    /**
     * Construct a new InvalidCommandException with a cause.
     *
     * @param commandText the original command text
     * @param reason a short, human-readable reason the command is invalid
     * @param cause underlying cause
     */
    public InvalidCommandException(String commandText, String reason, Throwable cause) {
        super("Invalid command: " + reason + " - " + commandText, cause);
        this.commandText = commandText;
        this.reason = reason;
    }

    /**
     * Get the original command text that failed validation.
     *
     * @return the command text
     */
    public String getCommandText() {
        return commandText;
    }

    /**
     * Get the short reason why the command was considered invalid.
     *
     * @return the reason text
     */
    public String getReason() {
        return reason;
    }

    /**
     * Return a concise, human-readable message describing the invalid command.
     *
     * @return formatted message containing the command and the reason
     */
    public String getInvalidCommandMessage() {
        return String.format("Command \"%s\" is invalid: %s", commandText, reason);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getInvalidCommandMessage();
    }
}
