package cscie97.asn4.asn5.ngatc.weather;

import cscie97.asn4.asn5.ngatc.common.Location;

/**
 * Represents a weather report for a specific location.
 */
public class WeatherReport {
    private long id;
    private double temperatureCelsius;
    private double windSpeedKnots;
    private double windHeading;
    private Location location;
    private java.time.Instant timestamp;

    public WeatherReport() {
        this.timestamp = java.time.Instant.now();
    }

    public WeatherReport(double temperatureCelsius, double windSpeedKnots, double windHeading, Location location) {
        this.temperatureCelsius = temperatureCelsius;
        this.windSpeedKnots = windSpeedKnots;
        this.windHeading = windHeading;
        this.location = location;
        this.timestamp = java.time.Instant.now();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public double getWindSpeedKnots() {
        return windSpeedKnots;
    }

    public void setWindSpeedKnots(double windSpeedKnots) {
        this.windSpeedKnots = windSpeedKnots;
    }

    public double getWindHeading() {
        return windHeading;
    }

    public void setWindHeading(double windHeading) {
        this.windHeading = windHeading;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public java.time.Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.time.Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("WeatherReport{location=%s, temp=%.1f°C, wind=%.1fkts @ %.0f°, time=%s}",
            location, temperatureCelsius, windSpeedKnots, windHeading, timestamp);
    }
}
