package cscie97.asn5.ngatc.staticmap;

/**
 * Represents restricted airspace that aircraft should not enter.
 */
public class RestrictedAirspace extends Airspace {
    
    public RestrictedAirspace() {
        super();
    }

    public RestrictedAirspace(long id, String name, double lowerLimitFeet, double upperLimitFeet) {
        super(id, "RESTRICTED", name, lowerLimitFeet, upperLimitFeet);
    }

    @Override
    public String toString() {
        return String.format("RestrictedAirspace{id=%d, name='%s', %.0f-%.0fft}",
            id, name, lowerLimitFeet, upperLimitFeet);
    }
}
