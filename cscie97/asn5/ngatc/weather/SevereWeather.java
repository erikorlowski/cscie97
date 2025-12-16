package cscie97.asn5.ngatc.weather;

import cscie97.asn5.ngatc.common.Location;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Represents a severe weather area (e.g., thunderstorms, turbulence).
 */
public class SevereWeather {
    private long id;
    private String type;
    private double upperLimitFeet;
    private double lowerLimitFeet;
    private String name;
    private String description;
    private ArrayList<Location> boundaries;
    private Instant expirationTime;
    private String warningDescription;

    public SevereWeather() {
        this.boundaries = new ArrayList<>();
    }

    public SevereWeather(String type, String name, String warningDescription, Instant expirationTime) {
        this.type = type;
        this.name = name;
        this.warningDescription = warningDescription;
        this.expirationTime = expirationTime;
        this.boundaries = new ArrayList<>();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getUpperLimitFeet() {
        return upperLimitFeet;
    }

    public void setUpperLimitFeet(double upperLimitFeet) {
        this.upperLimitFeet = upperLimitFeet;
    }

    public double getLowerLimitFeet() {
        return lowerLimitFeet;
    }

    public void setLowerLimitFeet(double lowerLimitFeet) {
        this.lowerLimitFeet = lowerLimitFeet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Location> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(ArrayList<Location> boundaries) {
        this.boundaries = boundaries;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getWarningDescription() {
        return warningDescription;
    }

    public void setWarningDescription(String warningDescription) {
        this.warningDescription = warningDescription;
    }

    /**
     * Checks if the severe weather warning is still active.
     */
    public boolean isActive() {
        return expirationTime == null || Instant.now().isBefore(expirationTime);
    }

    @Override
    public String toString() {
        return String.format("SevereWeather{name='%s', type='%s', warning='%s', expires=%s}",
            name, type, warningDescription, expirationTime);
    }
}
