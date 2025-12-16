package cscie97.asn4.asn5.ngatc.flighttracker;

import java.time.Instant;
import java.util.ArrayList;

import cscie97.asn4.asn5.ngatc.staticmap.Waypoint;

/**
 * Represents a flight plan with departure time, ETA, and waypoints.
 */
public class FlightPlan {
    private long id;
    private Instant departureTime;
    private Instant eta;
    private ArrayList<Waypoint> waypoints;

    public FlightPlan() {
        this.waypoints = new ArrayList<>();
    }

    public FlightPlan(long id, Instant departureTime, Instant eta) {
        this.id = id;
        this.departureTime = departureTime;
        this.eta = eta;
        this.waypoints = new ArrayList<>();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public void addWaypoint(Waypoint waypoint) {
        this.waypoints.add(waypoint);
    }

    @Override
    public String toString() {
        return String.format("FlightPlan{id=%d, departure=%s, eta=%s, waypoints=%d}",
            id, departureTime, eta, waypoints.size());
    }
}
