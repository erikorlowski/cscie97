package cscie97.asn4.asn5.ngatc.flighttracker;

import cscie97.asn4.asn5.ngatc.common.Severity;

/**
 * Warning for collision with static obstacles (terrain, buildings).
 */
public class ObstructionWarning extends FlightWarning {
    private double timeBeforeCollisionSeconds;
    private double distanceToCollisionMiles;
    private String counterMeasureInstructions;

    public ObstructionWarning() {
        super();
        this.severity = Severity.CRITICAL;
    }

    public ObstructionWarning(long id, double timeBeforeCollision, double distanceToCollision) {
        super(id, Severity.CRITICAL);
        this.timeBeforeCollisionSeconds = timeBeforeCollision;
        this.distanceToCollisionMiles = distanceToCollision;
        createCounterMeasureInstructions();
    }

    /**
     * Creates counter-measure instructions.
     */
    public void createCounterMeasureInstructions() {
        counterMeasureInstructions = "TERRAIN/OBSTRUCTION ALERT: Climb immediately to safe altitude";
    }

    @Override
    public String getWarningMessage() {
        return String.format("OBSTRUCTION WARNING: %.0f seconds to impact, %.1f miles. %s",
            timeBeforeCollisionSeconds, distanceToCollisionMiles, counterMeasureInstructions);
    }

    // Getters and setters
    public double getTimeBeforeCollisionSeconds() {
        return timeBeforeCollisionSeconds;
    }

    public void setTimeBeforeCollisionSeconds(double timeBeforeCollisionSeconds) {
        this.timeBeforeCollisionSeconds = timeBeforeCollisionSeconds;
    }

    public double getDistanceToCollisionMiles() {
        return distanceToCollisionMiles;
    }

    public void setDistanceToCollisionMiles(double distanceToCollisionMiles) {
        this.distanceToCollisionMiles = distanceToCollisionMiles;
    }

    public String getCounterMeasureInstructions() {
        return counterMeasureInstructions;
    }

    public void setCounterMeasureInstructions(String counterMeasureInstructions) {
        this.counterMeasureInstructions = counterMeasureInstructions;
    }
}
