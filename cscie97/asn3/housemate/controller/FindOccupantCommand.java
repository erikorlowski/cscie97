package cscie97.asn3.housemate.controller;

public class FindOccupantCommand implements Command {
    private String occupantName;

    public FindOccupantCommand(String occupantName) {
        this.occupantName = occupantName;
    }

    @Override
    public String execute() {
        OccupantTracker tracker = OccupantTracker.getInstance();
        String location = tracker.getOccupantLocation(occupantName);
        if (location != null && !location.equalsIgnoreCase("unknown")) {
            return String.format("Occupant %s found at %s", occupantName, location);
        } else {
            return String.format("Occupant %s not found", occupantName);
        }
    }
    
}
