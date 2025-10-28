package cscie97.asn2.housemate.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an abstract device in the HouseMate model.
 * Devices have a fully qualified name, a type, and a name.
 * This class should be extended by specific device implementations.
 */
abstract class Device implements ModelObject {
    protected String name;
    protected String fullyQualifiedName;
    protected String type;
    protected Map<String, String> statuses = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Constructs a Device with the specified fully qualified name and type.
     *
     * @param fullyQualifiedName the fully qualified name of the device ("<appliance/sensor>_<house>:<room>:<device>")
     * @param type the type of the device
     */
    public Device(String fullyQualifiedName, String type) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.split(":", 3)[2].split("(sensor|appliance)_", 2)[1];
        this.type = type;
    }

    /**
     * Returns the name of the device.
     *
     * @return the name of the device
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the fully qualified name of the device.
     *
     * @return the fully qualified name of the device
     */
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    /**
     * Returns the type of the device.
     *
     * @return the type of the device
     */
    public String getType() {
        return type;
    }

    /**
     * Sets a status of the device.
     *
     * @param statusName  the name of the status
     * @param statusValue the value of the status
     */
    public void setStatus(String statusName, String statusValue) {
        statuses.put(statusName, statusValue);
        System.out.println("Device " + fullyQualifiedName + " status " + statusName + " set to " + statusValue);
        ModelServiceApiImpl.getInstance().notifyStatusObservers(fullyQualifiedName, statusName, statusValue, type);
    }

    /**
     * Returns the statuses of the device.
     *
     * @return a map of status names to status values
     */
    public Map<String, String> getStatuses() {
        return statuses;
    }

    /**
     * Gets a specific status of the device.
     * @param statusName The name of the status to retrieve.
     * @return The value of the status, or null if not found.
     */
    public String getStatus(String statusName) {
        return statuses.get(statusName);
    }
}