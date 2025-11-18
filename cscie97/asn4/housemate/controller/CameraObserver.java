package cscie97.asn4.housemate.controller;

public class CameraObserver implements StatusObserver {

    @Override
    public void onStatusUpdate(String device, String status, String newValue, String deviceType) {
        if (status == null || newValue == null || deviceType == null) return;

        if (!deviceType.equalsIgnoreCase("camera")) {
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

        if(status.equalsIgnoreCase("occupant_detected")) {
            System.out.println(new OccupantRoomCommand(newValue, fullyQualifiedRoomName, true).execute());
        } else if(status.equalsIgnoreCase("occupant_leaving")) {
            System.out.println(new OccupantRoomCommand(newValue, fullyQualifiedRoomName, false).execute());
        } else if(status.equalsIgnoreCase("occupant_waking")) {
            System.out.println(new OccupantStatusCommand(fullyQualifiedRoomName, newValue, true).execute());
        } else if(status.equalsIgnoreCase("occupant_sleeping")) {
            System.out.println(new OccupantStatusCommand(fullyQualifiedRoomName, newValue, false).execute());
        }
    }
    
}
