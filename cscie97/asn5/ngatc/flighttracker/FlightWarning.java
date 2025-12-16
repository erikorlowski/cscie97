package cscie97.asn4.asn5.ngatc.flighttracker;

import java.time.Instant;
import java.util.ArrayList;

import cscie97.asn4.asn5.ngatc.common.Severity;

/**
 * Base class for flight warnings.
 */
public abstract class FlightWarning {
    protected long id;
    protected Instant time;
    protected Severity severity;
    protected ArrayList<Flight> flightsInDanger;

    public FlightWarning() {
        this.time = Instant.now();
        this.flightsInDanger = new ArrayList<>();
    }

    public FlightWarning(long id, Severity severity) {
        this.id = id;
        this.time = Instant.now();
        this.severity = severity;
        this.flightsInDanger = new ArrayList<>();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public ArrayList<Flight> getFlightsInDanger() {
        return flightsInDanger;
    }

    public void setFlightsInDanger(ArrayList<Flight> flightsInDanger) {
        this.flightsInDanger = flightsInDanger;
    }

    public void addFlightInDanger(Flight flight) {
        this.flightsInDanger.add(flight);
    }

    public abstract String getWarningMessage();
}
