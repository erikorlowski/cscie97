package cscie97.asn3.housemate.controller;

/**
 * OccupantStatusCommand is a command that updates the status of an occupant
 * (active or inactive) in the OccupantTracker.
 */
public class OccupantStatusCommand implements Command {
    private final String occupantName;
    private final boolean isActive;
    private final String fullyQualifiedRoomName;

    /**
     * Constructor for OccupantStatusCommand.
     * @param occupantName the name of the occupant
     * @param isActive the active status of the occupant
     */
    public OccupantStatusCommand(String fullyQualifiedRoomName, String occupantName, boolean isActive) {
        this.fullyQualifiedRoomName = fullyQualifiedRoomName;
        this.occupantName = occupantName;
        this.isActive = isActive;
    }

    /**
     * Execute the command to update the occupant's status.
     */
    @Override
    public String execute() {
        OccupantTracker tracker = OccupantTracker.getInstance();
        if (isActive) {
            tracker.makeOccupantActive(occupantName);
            return String.format("%s marked active", occupantName);
        } else {
            tracker.makeOccupantInactive(occupantName);
            if(tracker.getOccupantsInRoom(fullyQualifiedRoomName).size() <= 1) {
                ApplicationTypeCommand dimLightsCommand = new ApplicationTypeCommand(fullyQualifiedRoomName, "light", "dim", "TRUE");
                dimLightsCommand.execute();
            }
            return String.format("%s marked inactive", occupantName);
        }
    }
}
