package cscie97.asn4.housemate.controller;

/** Observer for oven done status updates */
public class OvenDoneObserver implements StatusObserver {

    /** Handle oven status updates */
    @Override
    public void onStatusUpdate(String device, String status, String newValue, String deviceType) {
        if (status == null || newValue == null || deviceType == null) return;

        if (!deviceType.equalsIgnoreCase("oven")) {
            return;
        }

        if (status.trim().equals("TimeToCook") && newValue.trim().equals("0")) {
            Command cmd = new OvenDoneCommand(device);
            System.out.println(cmd.execute());
        }
    }
}
