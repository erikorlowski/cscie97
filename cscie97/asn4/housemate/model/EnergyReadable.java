package cscie97.asn2.housemate.model;

/**
 * Interface for devices that can report their energy consumption in watts.
 */
interface EnergyReadable {
    /**
     * Returns the current energy consumption of the ModelObject in watts.
     *
     * @return the energy consumption in watts
     */
    double getEnergyConsumptionWatts();
}
