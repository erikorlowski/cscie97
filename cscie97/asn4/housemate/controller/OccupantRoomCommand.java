package cscie97.asn3.housemate.controller;

/**
 * OccupantRoomCommand is a command that updates the room occupancy
 * status of an occupant (entering or leaving) in the OccupantTracker.
 */
public class OccupantRoomCommand implements Command {
    private final String occupantName;
    private final String fullyQualifiedRoomName;
    private final boolean isEntering;

    /**
     * Constructor for OccupantRoomCommand.
     * @param occupantName the name of the occupant
     * @param fullyQualifiedRoomName the fully-qualified room name
     * @param isEntering true if the occupant is entering the room, false if leaving
     */
    public OccupantRoomCommand(String occupantName, String fullyQualifiedRoomName, boolean isEntering) {
        this.occupantName = occupantName;
        this.fullyQualifiedRoomName = fullyQualifiedRoomName;
        this.isEntering = isEntering;
    }

    /**
     * Execute the command to update the occupant's room status.
     */
    @Override
    public String execute() {
        OccupantTracker tracker = OccupantTracker.getInstance();
        if (isEntering) {
            ApplicationTypeCommand turnOnLightsCommand = new ApplicationTypeCommand(fullyQualifiedRoomName, "light", "power", "on");
            System.out.println(turnOnLightsCommand.execute());
            ApplicationTypeCommand increaseThermostatCommand = new ApplicationTypeCommand(fullyQualifiedRoomName, "thermostat", "temperature", "72");
            System.out.println(increaseThermostatCommand.execute());
            tracker.addOccupantToRoom(occupantName, fullyQualifiedRoomName);
            return String.format("%s entered %s", occupantName, fullyQualifiedRoomName);
        } else {
            tracker.removeOccupantFromRoom(occupantName, fullyQualifiedRoomName);
            System.out.println("Occupant " + occupantName + " removed from room: " + fullyQualifiedRoomName);

            if(tracker.getOccupantsInRoom(fullyQualifiedRoomName).isEmpty()) {
                ApplicationTypeCommand turnOffLightsCommand = new ApplicationTypeCommand(fullyQualifiedRoomName, "light", "power", "off");
                System.out.println(turnOffLightsCommand.execute());
                ApplicationTypeCommand decreaseThermostatCommand = new ApplicationTypeCommand(fullyQualifiedRoomName, "thermostat", "temperature", "65");
                System.out.println(decreaseThermostatCommand.execute());
            }

            return String.format("%s left %s", occupantName, fullyQualifiedRoomName);
        }
    }
}
