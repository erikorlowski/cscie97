package cscie97.asn4.housemate.model;

import java.util.Set;

/**
 * Represents a room in the house model. A Room is a ModelObject that is
 * configurable and exposes its energy consumption via EnergyReadable.
 * Rooms may own devices (appliances) and delegate energy calculations to them.
 */
class Room implements ModelObject, EnergyReadable, Configurable {
    private String name;
    private String fullyQualifiedName;
    private String type;
    private String floor;
    private int numWindows;

    /**
     * Create a new Room.
     *
     * @param fullyQualifiedName the unique fully qualified name for the room (e.g. "house_H:room_LivingRoom")
     * @param type the type of room (e.g. "bedroom", "kitchen")
     * @param floor the floor designation for the room
     * @param numWindows number of windows in the room
     */
    public Room(String fullyQualifiedName, String type, String floor, int numWindows) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.split(":", 2)[1].split("room_", 2)[1];
        this.type = type;
        this.floor = floor;
        this.numWindows = numWindows;
    }


    /**
     * Return a human-readable configuration description for this room.
     *
     * @return formatted configuration string for the room
     */
    @Override
    public String getConfiguration() {
        StringBuilder sb = new StringBuilder(String.format
        ("\nRoom Configuration:\nRoom: name=%s, type=%s, floor=%s, numWindows=%d\n", name, type, floor, numWindows));

        Set<String> devices = ModelServiceApiImpl.getInstance().getOwnedObjects(this);

        for (String deviceName : devices) {
            ModelObject obj = ModelServiceApiImpl.getInstance().getModelObject(deviceName);
            if (obj instanceof Appliance) {
                sb.append(((Appliance) obj).getConfiguration());
            }
        }

        return sb.toString();
    }

    /**
     * Compute the total energy consumption (in watts) for this room by
     * summing the energy consumption of all directly owned appliances.
     *
     * @return total energy consumption in watts
     */
    @Override
    public double getEnergyConsumptionWatts() {
        Set<String> devices = ModelServiceApiImpl.getInstance().getOwnedObjects(this);

        double energyConsumptionWatts = 0.0;

        for (String deviceName : devices) {
            ModelObject obj = ModelServiceApiImpl.getInstance().getModelObject(deviceName);
            if (obj instanceof Appliance) {
                energyConsumptionWatts += ((Appliance) obj).getEnergyConsumptionWatts();
            }
        }
        return energyConsumptionWatts;
    }

    /**
     * Get the room's short name (portion after the ':' in the fully qualified name).
     *
     * @return the room name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the fully qualified name of this room.
     *
     * @return fully qualified name used as the registry key
     */
    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
