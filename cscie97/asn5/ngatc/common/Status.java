package cscie97.asn4.asn5.ngatc.common;

/**
 * Enumeration representing module health status in the NGATC system.
 * Used by the System Monitor to track module health.
 */
public enum Status {
    OFFLINE(0),
    CRITICAL(1),
    DIMINISHED(2),
    HEALTHY(3);

    private final int level;

    Status(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Status fromLevel(int level) {
        for (Status s : values()) {
            if (s.level == level) {
                return s;
            }
        }
        return OFFLINE;
    }
}
