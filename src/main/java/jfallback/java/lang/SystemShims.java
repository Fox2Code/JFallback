package jfallback.java.lang;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public  final class SystemShims {
    // Added in java 9
    public static System$Logger getLogger(String name) {
        return new WrappedLogger(name, null);
    }

    // Added in java 9
    public static System$Logger getLogger(String name, ResourceBundle resourceBundle) {
        return new WrappedLogger(name, resourceBundle);
    }

    // JFallback implementation
    private static final class WrappedLogger implements System$Logger {
        private final Logger logger;
        private final String name;

        private static Level toJavaUtilLoggingLevel(System$Logger$Level level) {
            switch (level) {
                default:
                    throw new IllegalArgumentException(level.getName() + " unsupported");
                case ALL:
                    return Level.ALL;
                case TRACE:
                    return Level.FINER;
                case DEBUG:
                    return Level.FINE;
                case INFO:
                    return Level.INFO;
                case WARNING:
                    return Level.WARNING;
                case ERROR:
                    return Level.SEVERE;
                case OFF:
                    return Level.OFF;
            }
        }

        private WrappedLogger(String name, ResourceBundle resourceBundle) {
            this.logger = Logger.getLogger(name);
            this.name = name;
            if (resourceBundle != null) {
                this.logger.setResourceBundle(resourceBundle);
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isLoggable(System$Logger$Level level) {
            return this.logger.isLoggable(toJavaUtilLoggingLevel(level));
        }

        @Override
        public void log(System$Logger$Level level, ResourceBundle bundle, String msg, Throwable thrown) {
            this.logger.log(toJavaUtilLoggingLevel(level), msg, thrown);
        }

        @Override
        public void log(System$Logger$Level level, ResourceBundle bundle, String format, Object... params) {
            this.logger.log(toJavaUtilLoggingLevel(level), format, params);
        }
    }
}
