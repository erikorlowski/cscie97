package cscie97.asn2.housemate.model;

import java.util.Set;

public class Room implements ModelObject, EnergyReadable, Configurable {
    private String name;
    private String fullyQualifiedName;
    private String type;
    private String floor;
    private int numWindows;

    public Room(String fullyQualifiedName, String type, String floor, int numWindows) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.split(":", 2)[1];
        this.type = type;
        this.floor = floor;
        this.numWindows = numWindows;
    }


    @Override
    public String getConfiguration() {
        return String.format("Room: name=%s, type=%s, floor=%s, numWindows=%d]",
                name, type, floor, numWindows);
    }

    @Override
    public double getEnergyConsumptionWatts() {
        Set<String> devices = ModelServiceApi.getInstance().getOwnedObjects(this);

        double energyConsumptionWatts = 0.0;

        for (String deviceName : devices) {
            ModelObject obj = ModelServiceApi.getInstance().getModelObject(deviceName);
            if (obj instanceof Appliance) {
                energyConsumptionWatts += ((Appliance) obj).getEnergyConsumptionWatts();
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
