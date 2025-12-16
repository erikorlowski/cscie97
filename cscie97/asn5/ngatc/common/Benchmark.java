package cscie97.asn5.ngatc.common;

import java.time.Instant;

/**
 * Represents a location at a specific time, used for trajectory prediction.
 */
public class Benchmark {
    private Instant time;
    private Location location;

    public Benchmark() {}

    public Benchmark(Instant time, Location location) {
        this.time = time;
        this.location = location;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("Benchmark{time=%s, location=%s}", time, location);
    }
}
