package cscie97.asn2.housemate.model;

public class House implements ModelObject, Configurable, EnergyReadable {
    private String name;
    private String fullyQualifiedName;
    private String address;

    public House(String fullyQualifiedName, String address) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.split("house_", 2)[1];
        this.address = address;
    }

    @Override
    public String getConfiguration() {
        return String.format("House: name=%s, address=%s]", name, address);
    }

    @Override
    public double getEnergyConsumptionWatts() {
        double energyConsumptionWatts = 0.0;

        for (String roomName : ModelServiceApi.getInstance().getOwnedObjects(this)) {
            ModelObject obj = ModelServiceApi.getInstance().getModelObject(roomName);
            if (obj instanceof Room) {
                energyConsumptionWatts += ((Room) obj).getEnergyConsumptionWatts();
            }
        }
        return energyConsumptionWatts;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
    
}
