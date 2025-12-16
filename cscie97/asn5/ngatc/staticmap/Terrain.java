package cscie97.asn5.ngatc.staticmap;

import cscie97.asn5.ngatc.common.Location;

/**
 * Represents terrain with elevation (e.g., mountains).
 */
public class Terrain extends Area {
    private double elevationFeet;

    public Terrain() {
        super();
    }

    public Terrain(long id, String name, double elevationFeet) {
        super(id, "TERRAIN", name);
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
        return String.format("Terrain{id=%d, name='%s', elevation=%.0fft, boundaries=%d}",
            id, name, elevationFeet, boundaries.size());
    }
}
