package cscie97.asn4.asn5.ngatc.common;

/**
 * Enumeration representing event severity levels in the NGATC system.
 * Used for alerts, warnings, and logging throughout the system.
 */
public enum Severity {
    INFO(0),
    WARNING(1),
    CRITICAL(2);

    private final int level;

    Severity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Severity fromLevel(int level) {
        for (Severity s : values()) {
            if (s.level == level) {
                return s;
            }
        }
        return INFO;
    }
}
