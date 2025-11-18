package cscie97.asn4.housemate.controller;

/**
 * Observer that monitors beer count status updates and triggers notifications.
 */
public class BeerCountObserver implements StatusObserver {

    /**
     * Handle status updates for beer count.
     */
    @Override
    public void onStatusUpdate(String device, String status, String newValue, String deviceType) {
        if (status == null || newValue == null || deviceType == null) return;

        if (!deviceType.equalsIgnoreCase("refrigerator")) {
            return;
        }

        if ("beer_count".equalsIgnoreCase(status) || status.toLowerCase().contains("beer")) {
            int count;
            try { count = Integer.parseInt(newValue); } catch (NumberFormatException ignored) {return;}
            if(count < 3) {
                Command cmd = new BeerNotificationCommand(device, count);
                System.out.println(cmd.execute());
            }
        }
    }
}
