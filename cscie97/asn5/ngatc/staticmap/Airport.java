package cscie97.asn5.ngatc.staticmap;

import cscie97.asn5.ngatc.common.Location;

/**
 * Represents an airport.
 */
public class Airport extends Landmark {
    private String code;

    public Airport() {
        super();
    }

    public Airport(long id, String name, String code, Location location, double elevationFeet) {
        super(id, name, location, elevationFeet);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("Airport{id=%d, name='%s', code='%s', location=%s, elevation=%.0fft}",
            id, name, code, location, elevationFeet);
    }
}
