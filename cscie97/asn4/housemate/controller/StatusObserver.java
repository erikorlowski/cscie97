package cscie97.asn4.housemate.controller;

public interface StatusObserver {
    void onStatusUpdate(String device, String status, String newValue, String deviceType);
}
