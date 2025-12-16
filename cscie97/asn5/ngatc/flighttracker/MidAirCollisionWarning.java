package cscie97.asn4.asn5.ngatc.flighttracker;

import cscie97.asn4.asn5.ngatc.common.Severity;

/**
 * Warning for mid-air collision risk between aircraft.
 */
public class MidAirCollisionWarning extends FlightWarning {
    private double timeBeforeCollisionSeconds;
    private double distanceToCollisionMiles;
    private String counterMeasureInstructions;

    public MidAirCollisionWarning() {
        super();
        this.severity = Severity.CRITICAL;
    }

    public MidAirCollisionWarning(long id, double timeBeforeCollision, double distanceToCollision) {
        super(id, Severity.CRITICAL);
        this.timeBeforeCollisionSeconds = timeBeforeCollision;
        this.distanceToCollisionMiles = distanceToCollision;
        createCounterMeasureInstructions();
    }

    /**
     * Creates counter-measure instructions based on conflict geometry.
     */
    public void createCounterMeasureInstructions() {
        // Simplified logic - real implementation would be more sophisticated
        if (timeBeforeCollisionSeconds < 60) {
            counterMeasureInstructions = "IMMEDIATE ACTION REQUIRED: Climb 1000ft or turn 45° right";
        } else {
            counterMeasureInstructions = "Turn 30° right and climb 500ft";
        }
    }

    @Override
    public String getWarningMessage() {
        return String.format("MID-AIR COLLISION WARNING: %.0f seconds to conflict, %.1f miles. %s",
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
