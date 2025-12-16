package cscie97.asn4.asn5.ngatc.staticmap;

/**
 * Represents an airspace area with altitude limits.
 */
public class Airspace extends Area {
    protected double upperLimitFeet;
    protected double lowerLimitFeet;

    public Airspace() {
        super();
    }

    public Airspace(long id, String type, String name, double lowerLimitFeet, double upperLimitFeet) {
        super(id, type, name);
        this.lowerLimitFeet = lowerLimitFeet;
        this.upperLimitFeet = upperLimitFeet;
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

    @Override
    public String toString() {
        return String.format("Airspace{id=%d, type='%s', name='%s', %.0f-%.0fft}",
            id, type, name, lowerLimitFeet, upperLimitFeet);
    }
}
