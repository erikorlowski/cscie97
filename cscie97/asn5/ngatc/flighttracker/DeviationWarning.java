package cscie97.asn5.ngatc.flighttracker;

import cscie97.asn5.ngatc.common.Severity;

/**
 * Warning for flight plan deviations.
 */
public class DeviationWarning extends FlightWarning {
    private String message;

    public DeviationWarning() {
        super();
        this.severity = Severity.WARNING;
    }

    public DeviationWarning(long id, String message) {
        super(id, Severity.WARNING);
        this.message = message;
    }

    @Override
    public String getWarningMessage() {
        return "DEVIATION WARNING: " + message;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
