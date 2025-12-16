package cscie97.asn4.asn5.ngatc.common;

/**
 * Represents a GPS coordinate with altitude.
 * Used throughout the NGATC system to represent positions in 3D space.
 */
public class Location {
    private double latitude;
    private double longitude;
    private double altitudeFeet;

    public Location() {}

    public Location(double latitude, double longitude, double altitudeFeet) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudeFeet = altitudeFeet;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitudeFeet() {
        return altitudeFeet;
    }

    public void setAltitudeFeet(double altitudeFeet) {
        this.altitudeFeet = altitudeFeet;
    }

    @Override
    public String toString() {
        return String.format("Location{lat=%.6f, lon=%.6f, alt=%.2fft}", latitude, longitude, altitudeFeet);
    }
}
