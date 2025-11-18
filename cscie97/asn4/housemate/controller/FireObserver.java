package cscie97.asn4.housemate.controller;

/** Observer for fire status updates */
public class FireObserver implements StatusObserver {

    /** Handle fire status updates */
    @Override
    public void onStatusUpdate(String device, String status, String newValue, String deviceType) {
        if (status == null || newValue == null || deviceType == null) return;

        if (!deviceType.equalsIgnoreCase("smoke_detector")) {
            return;
        }

        if (status.trim().equalsIgnoreCase("fire") && newValue.trim().equalsIgnoreCase("active")) {
            Command cmd = new FireCommand(device);
            System.out.println(cmd.execute());
        }
    }
}
