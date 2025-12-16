package cscie97.asn4.asn5.ngatc.staticmap;

import cscie97.asn4.asn5.ngatc.common.Location;

/**
 * Represents a landmark with elevation (e.g., mountain, tall building).
 */
public class Landmark extends Waypoint {
    protected double elevationFeet;

    public Landmark() {
        super();
    }

    public Landmark(long id, String name, Location location, double elevationFeet) {
        super(id, name, location);
        this.elevationFeet = elevationFeet;
    }

    public double getElevationFeet() {
        return elevationFeet;
    }

    public void setElevationFeet(double elevationFeet) {
        this.elevationFeet = elevationFeet;
    }

    @Override
    public String toString() {
        return String.format("Landmark{id=%d, name='%s', location=%s, elevation=%.0fft}", 
            id, name, location, elevationFeet);
    }
}
