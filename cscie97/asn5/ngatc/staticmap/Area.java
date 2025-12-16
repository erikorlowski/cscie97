package cscie97.asn4.asn5.ngatc.staticmap;

import java.util.ArrayList;

import cscie97.asn4.asn5.ngatc.common.Location;

/**
 * Base class for geographic areas (terrain, buildings, airspace).
 */
public class Area {
    protected long id;
    protected String type;
    protected double radiusMiles;
    protected String name;
    protected String description;
    protected ArrayList<Location> boundaries;

    public Area() {
        this.boundaries = new ArrayList<>();
    }

    public Area(long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
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

    public double getRadiusMiles() {
        return radiusMiles;
    }

    public void setRadiusMiles(double radiusMiles) {
        this.radiusMiles = radiusMiles;
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

    public void addBoundary(Location location) {
        this.boundaries.add(location);
    }

    @Override
    public String toString() {
        return String.format("Area{id=%d, type='%s', name='%s', boundaries=%d}",
            id, type, name, boundaries.size());
    }
}
