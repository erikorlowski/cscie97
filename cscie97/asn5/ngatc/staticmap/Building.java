package cscie97.asn5.ngatc.staticmap;

/**
 * Represents a building with elevation.
 */
public class Building extends Area {
    private double elevationFeet;

    public Building() {
        super();
    }

    public Building(long id, String name, double elevationFeet) {
        super(id, "BUILDING", name);
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
        return String.format("Building{id=%d, name='%s', elevation=%.0fft}",
            id, name, elevationFeet);
    }
}
