package cscie97.asn3.housemate.controller;

import cscie97.asn2.housemate.model.ModelServiceApi;
import cscie97.asn2.housemate.model.ModelServiceApiImpl;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;

import java.io.IOException;
import java.util.ArrayList;

public class ApplicationTypeCommand implements Command {

    private final String fullyQualifiedContainerName;
    private final String applianceType;
    private final String statusName;
    private final String newValue;

    public ApplicationTypeCommand(String fullyQualifiedContainerName, String applianceType, String statusName, String newValue) {
        this.fullyQualifiedContainerName = fullyQualifiedContainerName;
        this.applianceType = applianceType;
        this.statusName = statusName;
        this.newValue = newValue;
    }

    @Override
    public String execute() {
        if (fullyQualifiedContainerName == null) {
            return new ObjectNotFoundException("Object name not specified").getMessage();
        }

        ArrayList<String> appliances = getRelevantAppliances(fullyQualifiedContainerName, applianceType);
        if (appliances == null || appliances.isEmpty()) {
            return new ObjectNotFoundException("No appliances of type " + applianceType + " found in " + fullyQualifiedContainerName).getMessage();
        } else {
            ModelServiceApi modelService = ModelServiceApiImpl.getInstance();
            StringBuilder result = new StringBuilder();
            for (String appliance : appliances) {
                String command = String.format("set appliance %s status %s value %s", appliance, statusName, newValue);

                modelService.executeCommand(command, ControllerServiceApi.getInstance().getControllerAccessToken());
                result.append(String.format("Set %s status %s to %s%n", appliance, statusName, newValue));
            }
            return result.toString().trim();
        }
    }

    private ArrayList<String> getRelevantAppliances(String fullyQualifiedContainerName, String applianceType) {
        ArrayList<String> appliances = new ArrayList<>();
        
    ModelServiceApi modelService = ModelServiceApiImpl.getInstance();
    long token = ControllerServiceApi.getInstance().getControllerAccessToken();
    String containerConfiguration = modelService.executeCommand("show configuration " + fullyQualifiedContainerName, token);
        boolean containerIsHouse = true;
        String house = fullyQualifiedContainerName;
        String room = null;
        boolean readingApplianceConfiguration = false;

        String currentFullyQualifiedApplianceName = null;

        if (fullyQualifiedContainerName.contains(":")) {
            containerIsHouse = false;
            house = fullyQualifiedContainerName.substring(0, fullyQualifiedContainerName.indexOf(":"));
            room = fullyQualifiedContainerName.substring(fullyQualifiedContainerName.indexOf(":") + 1);
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.StringReader(containerConfiguration))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    readingApplianceConfiguration = false;
                    continue;
                }
                
                if(readingApplianceConfiguration) {
                    if(line.startsWith("Name: ")) {
                        String applianceName = line.substring(line.indexOf(':') + 1).trim();
                        currentFullyQualifiedApplianceName = house + ":" + room + ":" + applianceName;
                    } else if(line.startsWith("Type: ")) {
                        String type = line.substring(line.indexOf(':') + 1).trim();
                        if(type.equalsIgnoreCase(applianceType)) {
                            appliances.add(currentFullyQualifiedApplianceName);
                        }
                    }
                }

                // Update the room if we are processing a house container
                if(line.startsWith("Room: name=") && containerIsHouse) {
                    int eq = line.indexOf('=');
                    int comma = line.indexOf(',', eq + 1);
                    if (eq != -1) {
                        if (comma != -1) {
                            room = line.substring(eq + 1, comma).trim();
                        }
                    }
                }

                if(line.startsWith("Appliance Configuration:")) {
                    readingApplianceConfiguration = true;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return appliances;
    }
}
