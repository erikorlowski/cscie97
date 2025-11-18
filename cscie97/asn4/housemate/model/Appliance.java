package cscie97.asn2.housemate.model;

import java.util.Map;

/**
 * The Appliance class represents an appliance device in the housemate system.
 * It extends the Device class and implements the Configurable and EnergyReadable interfaces.
 */
class Appliance extends Device implements Configurable, EnergyReadable {
    private double energyConsumptionWhenOnWatts;
    
    /**
     * Constructs an Appliance with the specified fully qualified name, type, and energy consumption when on.
     * @param fullyQualifiedName the fully qualified name of the appliance
     * @param type the type of the appliance
     * @param energyConsumptionWhenOnWatts the energy consumption of the appliance when it is on, in watts
     */
    public Appliance(String fullyQualifiedName, String type, double energyConsumptionWhenOnWatts) {
        super(fullyQualifiedName, type);
        this.energyConsumptionWhenOnWatts = energyConsumptionWhenOnWatts;
    }

    /**
     * Returns the current energy consumption of the appliance in watts.
     * If the appliance is off, it returns 0.0 watts.
     *
     * @return the energy consumption in watts
     */
    @Override
    public double getEnergyConsumptionWatts() {
        if(isOn()) {
            return energyConsumptionWhenOnWatts;
        } else {
            return 0.0;
        }
    }

    /**
     * Checks if the appliance is currently on based on its power status.
     * @return true if the appliance is on, false otherwise
     */
    public boolean isOn() {
        String status = super.statuses.get("power");
        return status != null && status.equalsIgnoreCase("on");
    }

    /**
     * Returns a string representation of the appliance's configuration.
     * @return the configuration string
     */
    @Override
    public String getConfiguration() {
        StringBuilder config = new StringBuilder();
        config.append("\nAppliance Configuration:\n");
        config.append("Name: ").append(getName()).append("\n");
        config.append("Type: ").append(getType()).append("\n");
        config.append("Energy Consumption When On (Watts): ").append(energyConsumptionWhenOnWatts).append("\n");
        config.append("Current Statuses:\n");
        for (Map.Entry<String, String> entry : super.statuses.entrySet()) {
            config.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        config.append("\n");
        return config.toString();
    }
}
