package jfallback.java.lang;

// Added in java 9
public enum System$Logger$Level {
    ALL(Integer.MIN_VALUE), TRACE(400), DEBUG(500), INFO(800), WARNING(900), ERROR(1000), OFF(Integer.MAX_VALUE);

    private final int severity;

    System$Logger$Level(int severity) {
        this.severity = severity;
    }

    public final String getName() {
        return name();
    }

    public final int getSeverity() {
        return severity;
    }
}
