package cscie97.asn5.ngatc.common;

import java.time.Instant;

/**
 * Represents a system event to be logged.
 * Used across all modules for event logging to the System Monitor.
 */
public class LogEvent {
    private int id;
    private Severity severity;
    private String source;
    private String info;
    private Instant timestamp;

    public LogEvent() {
        this.timestamp = Instant.now();
    }

    public LogEvent(Severity severity, String source, String info, Instant timestamp) {
        this.severity = severity;
        this.source = source;
        this.info = info;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", timestamp, severity, source, info);
    }
}
