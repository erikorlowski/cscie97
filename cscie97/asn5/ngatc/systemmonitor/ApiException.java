package cscie97.asn4.asn5.ngatc.systemmonitor;

/**
 * Exception thrown when REST API operations fail in the System Monitor module.
 */
public class ApiException extends RuntimeException {
    private final String request;
    private final String message;

    public ApiException(String request, String message) {
        super(message);
        this.request = request;
        this.message = message;
    }

    public String getRequest() {
        return request;
    }

    @Override
    public String getMessage() {
        return String.format("API Error for request '%s': %s", request, message);
    }
}
