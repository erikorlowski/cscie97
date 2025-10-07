package cscie97.asn2.housemate.model;

public class Occupant implements ModelObject {
    private String name;
    private OccupantType type;
    private OccupantStatus status;
    private boolean isKnown;
    private String fullyQualifiedName;

    public Occupant(String fullyQualifiedName, OccupantType type) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.type = type;
        this.status = OccupantStatus.ACTIVE;
        this.isKnown = false;
    }

    public String getName() {
        return fullyQualifiedName.split("occupant_", 2)[1];
    }

    public OccupantType getType() {
        return type;
    }

    public OccupantStatus getStatus() {
        return status;
    }

    public boolean isKnown() {
        return isKnown;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
