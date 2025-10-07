package cscie97.asn2.housemate.model;

/**
 * Represents a house in the model. A House is a ModelObject that is
 * configurable and exposes its energy consumption via EnergyReadable.
 * It aggregates rooms and delegates energy calculations to them.
 */
class House implements ModelObject, Configurable, EnergyReadable {
    private String name;
    private String fullyQualifiedName;
    private String address;

    /**
     * Create a new House.
     *
     * @param fullyQualifiedName the unique fully qualified name for the house (e.g. "house_MyHouse")
     * @param address the postal address of the house
     */
    public House(String fullyQualifiedName, String address) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.split("house_", 2)[1];
        this.address = address;
    }

    /**
     * Return a human-readable configuration description for this house.
     *
     * @return formatted configuration string for the house
     */
    @Override
    public String getConfiguration() {
        StringBuilder sb = new StringBuilder(String.format("\nHouse Configuration:\nHouse: name=%s, address=%s\n", name, address));

        for (String roomName : ModelServiceApiImpl.getInstance().getOwnedObjects(this)) {
            ModelObject obj = ModelServiceApiImpl.getInstance().getModelObject(roomName);
            if (obj instanceof Room) {
                sb.append(((Room) obj).getConfiguration());
            }
        }

        return sb.toString();
    }

    /**
     * Compute the total energy consumption (in watts) for this house by
     * summing the energy consumption of all directly owned rooms.
     *
     * @return total energy consumption in watts
     */
    @Override
    public double getEnergyConsumptionWatts() {
        double energyConsumptionWatts = 0.0;

        for (String roomName : ModelServiceApiImpl.getInstance().getOwnedObjects(this)) {
            ModelObject obj = ModelServiceApiImpl.getInstance().getModelObject(roomName);
            if (obj instanceof Room) {
                energyConsumptionWatts += ((Room) obj).getEnergyConsumptionWatts();
            }
        }
        return energyConsumptionWatts;
    }

    /**
     * Get the short name of the house (the portion after "house_").
     *
     * @return the house name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the fully qualified name of this house.
     *
     * @return fully qualified name used as the registry key
     */
    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
    
}
