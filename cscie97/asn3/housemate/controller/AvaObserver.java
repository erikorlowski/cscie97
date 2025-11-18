package cscie97.asn3.housemate.controller;

import cscie97.asn2.housemate.model.ModelServiceApiImpl;
import cscie97.asn4.housemate.entitlement.EntitlementException;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;

/**
 * Observer for Ava device status updates
 */
public class AvaObserver implements StatusObserver {

    /** Handle Ava status updates */
    @Override
    public void onStatusUpdate(String device, String status, String newValue, String deviceType) {
        if (status == null || newValue == null || deviceType == null) return;

        if (!deviceType.equalsIgnoreCase("ava")) {
            return;
        }

        String fullyQualifiedRoomName;
        int firstColon = device.indexOf(':');
        if (firstColon != -1) {
            int secondColon = device.indexOf(':', firstColon + 1);
            if (secondColon != -1) {
                fullyQualifiedRoomName = device.substring(0, secondColon);
            } else {
                System.out.println(new InvalidCommandException(device, "Invalid device format"));
                return;
            }
        } else {
            System.out.println(new InvalidCommandException(device, "Invalid device format"));
            return;
        }

        if ("voice_in".equalsIgnoreCase(status)) {
            if(newValue.toLowerCase().contains("open door")) {
                Command cmd = new ApplicationTypeCommand(fullyQualifiedRoomName, "door", "opened", "OPEN");
                System.out.println(cmd.execute());
            } else if(newValue.toLowerCase().contains("close door")) {
                Command cmd = new ApplicationTypeCommand(fullyQualifiedRoomName, "door", "closed", "CLOSED");
                System.out.println(cmd.execute());
            } else if(newValue.toLowerCase().contains("lights on")) {
                Command cmd = new ApplicationTypeCommand(fullyQualifiedRoomName, "light", "power", "ON");
                System.out.println(cmd.execute());
            } else if(newValue.toLowerCase().contains("lights off")) {
                Command cmd = new ApplicationTypeCommand(fullyQualifiedRoomName, "light", "power", "OFF");
                System.out.println(cmd.execute());
            } else if(newValue.toLowerCase().startsWith("where is ")) {
                String occupantName = newValue.substring(9).trim();
                Command cmd = new FindOccupantCommand(occupantName);
                System.out.println(cmd.execute());
            } else {
                // try to parse input like: "appliance_type status_name value"
                String[] parts = newValue == null ? new String[0] : newValue.trim().split("\\s+");
                if (parts.length < 3) {
                    System.out.println(new InvalidCommandException(device, "Invalid command format"));
                    return;
                }

                String applianceType = parts[0].trim().toLowerCase();
                String statusName = parts[1].trim().toLowerCase();
                // join the rest as the value (to allow values containing spaces)
                StringBuilder valBuilder = new StringBuilder(parts[2].trim());
                for (int i = 3; i < parts.length; i++) {
                    valBuilder.append(' ').append(parts[i].trim());
                }
                String value = valBuilder.toString();

                Command cmd = new ApplicationTypeCommand(fullyQualifiedRoomName, applianceType, statusName, value);
                System.out.println(cmd.execute());
            }
        } else if ("voiceprint".equalsIgnoreCase(status)) {
            Command cmd = new VoiceprintCommand(newValue);
            System.out.println(cmd.execute());
        }
    }
    
}
