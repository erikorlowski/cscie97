package cscie97.asn5.ngatc.staticmap;

import cscie97.asn5.ngatc.common.Location;

/**
 * Represents a waypoint in the airspace.
 * Base class for Landmarks and Airports.
 */
public class Waypoint {
    protected long id;
    protected String name;
    protected Location location;

    public Waypoint() {}

    public Waypoint(long id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("Waypoint{id=%d, name='%s', location=%s}", id, name, location);
    }
}
