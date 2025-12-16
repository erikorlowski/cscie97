package cscie97.asn5.ngatc.flighttracker;

import cscie97.asn5.ngatc.common.Location;
import cscie97.asn5.ngatc.common.Trajectory;
import cscie97.asn5.ngatc.staticmap.Waypoint;

/**
 * Represents the flight dynamics (attitude, heading, speed, location) of an aircraft.
 */
public class FlightDynamics {
    private double attitude;
    private double heading;
    private double speedKnots;
    private Location location;
    private Trajectory trajectory;
    private Waypoint targetWaypoint;

    public FlightDynamics() {}

    public FlightDynamics(double attitude, double heading, double speedKnots, Location location) {
        this.attitude = attitude;
        this.heading = heading;
        this.speedKnots = speedKnots;
        this.location = location;
    }

    // Getters and setters
    public double getAttitude() {
        return attitude;
    }

    public void setAttitude(double attitude) {
        this.attitude = attitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeedKnots() {
        return speedKnots;
    }

    public void setSpeedKnots(double speedKnots) {
        this.speedKnots = speedKnots;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Trajectory getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(Trajectory trajectory) {
        this.trajectory = trajectory;
    }

    public Waypoint getTargetWaypoint() {
        return targetWaypoint;
    }

    public void setTargetWaypoint(Waypoint targetWaypoint) {
        this.targetWaypoint = targetWaypoint;
    }

    @Override
    public String toString() {
        return String.format("FlightDynamics{location=%s, heading=%.1f°, speed=%.1fkts, attitude=%.1f°}",
            location, heading, speedKnots, attitude);
    }
}
