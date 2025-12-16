package cscie97.asn4.asn5.ngatc.flighttracker;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Represents a flight in the NGATC system.
 * Top-level class encapsulating all flight information.
 */
public class Flight {
    private long id;
    private String callSign;
    private Instant departureTime;
    private Instant eta;
    private String flightStatus;
    private int passengerCount;
    private double fuelAmountPounds;
    private double availableFlightTimeSeconds;
    private double remainingFlightTimeSeconds;
    private FlightPlan flightPlan;
    private FlightPlan proposedUpdateToFlightPlan;
    private FlightDynamics requestedFlightDynamics;
    private FlightDynamics actualFlightDynamics;
    private boolean isFlightAccepted;

    public Flight() {
        this.flightStatus = "NOT_DEPARTED";
        this.isFlightAccepted = false;
    }

    public Flight(long id, String callSign) {
        this.id = id;
        this.callSign = callSign;
        this.flightStatus = "NOT_DEPARTED";
        this.isFlightAccepted = false;
    }

    /**
     * Accepts a proposed flight plan update.
     */
    public void acceptFlightPlanUpdate() {
        if (proposedUpdateToFlightPlan != null) {
            this.flightPlan = proposedUpdateToFlightPlan;
            this.proposedUpdateToFlightPlan = null;
            System.out.println("Flight " + callSign + " accepted flight plan update");
        }
    }

    /**
     * Rejects a proposed flight plan update.
     */
    public void rejectFlightPlanUpdate() {
        this.proposedUpdateToFlightPlan = null;
        System.out.println("Flight " + callSign + " rejected flight plan update");
    }

    /**
     * Accepts the flight into the NGATC system.
     */
    public void acceptFlight() {
        this.isFlightAccepted = true;
        System.out.println("Flight " + callSign + " accepted into NGATC");
    }

    /**
     * Proposes a new flight plan.
     */
    public void proposeNewFlightPlan(FlightPlan proposedFlightPlan) {
        this.proposedUpdateToFlightPlan = proposedFlightPlan;
        System.out.println("Flight " + callSign + " received proposed flight plan");
    }

    /**
     * Updates flight with new dynamics.
     */
    public void updateFlight(FlightDynamics newDynamics) {
        this.actualFlightDynamics = newDynamics;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getEta() {
        return eta;
    }

    public void setEta(Instant eta) {
        this.eta = eta;
    }

    public String getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getFuelAmountPounds() {
        return fuelAmountPounds;
    }

    public void setFuelAmountPounds(double fuelAmountPounds) {
        this.fuelAmountPounds = fuelAmountPounds;
    }

    public double getAvailableFlightTimeSeconds() {
        return availableFlightTimeSeconds;
    }

    public void setAvailableFlightTimeSeconds(double availableFlightTimeSeconds) {
        this.availableFlightTimeSeconds = availableFlightTimeSeconds;
    }

    public double getRemainingFlightTimeSeconds() {
        return remainingFlightTimeSeconds;
    }

    public void setRemainingFlightTimeSeconds(double remainingFlightTimeSeconds) {
        this.remainingFlightTimeSeconds = remainingFlightTimeSeconds;
    }

    public FlightPlan getFlightPlan() {
        return flightPlan;
    }

    public void setFlightPlan(FlightPlan flightPlan) {
        this.flightPlan = flightPlan;
    }

    public FlightPlan getProposedUpdateToFlightPlan() {
        return proposedUpdateToFlightPlan;
    }

    public void setProposedUpdateToFlightPlan(FlightPlan proposedUpdateToFlightPlan) {
        this.proposedUpdateToFlightPlan = proposedUpdateToFlightPlan;
    }

    public FlightDynamics getRequestedFlightDynamics() {
        return requestedFlightDynamics;
    }

    public void setRequestedFlightDynamics(FlightDynamics requestedFlightDynamics) {
        this.requestedFlightDynamics = requestedFlightDynamics;
    }

    public FlightDynamics getActualFlightDynamics() {
        return actualFlightDynamics;
    }

    public void setActualFlightDynamics(FlightDynamics actualFlightDynamics) {
        this.actualFlightDynamics = actualFlightDynamics;
    }

    public boolean isFlightAccepted() {
        return isFlightAccepted;
    }

    public void setFlightAccepted(boolean flightAccepted) {
        isFlightAccepted = flightAccepted;
    }

    @Override
    public String toString() {
        return String.format("Flight{id=%d, callSign='%s', status='%s', accepted=%b}",
            id, callSign, flightStatus, isFlightAccepted);
    }
}
