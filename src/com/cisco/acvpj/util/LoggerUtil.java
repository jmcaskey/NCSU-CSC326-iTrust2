/**
 *
 */
package com.cisco.acvpj.util;

import java.io.PrintStream;

/**
 * Logging class to handle saving log-worthy events. All actions that need to be
 * logged (as defined in the iTrust Wiki) should be logged using one of the two
 * `Log` methods here.
 *
 * @author John-Michael Caskey
 */
public class LoggerUtil {

    /**
     * Log level set by the program. Log statements that are equal to or greater
     * than level are printed.
     */
    public static LogLevel logLevel = LogLevel.STATUS;

    /**
     * Output stream set by the program to control to output location of logs.
     */
    public static PrintStream output = System.out;

    /**
     * Creates a log entry to save at the given level and with provided message
     * provided that the global log level excedes the logs level.
     *
     * @param level
     *            LogLevel enumeration that specifies the individual log level
     * @param message
     *            String containing message to output/save in output logs
     */
    public static void log ( final LogLevel level, final String message ) {
        if ( logLevel.ordinal() >= level.ordinal() ) {
            final LogEntry entry = new LogEntry( level, message );
            entry.save();
        }

    }

    /**
     * Logs a message at a given level with a message taking note of the called
     * class and method name passed in as string. Class and method name are
     * added to the message before creating the entry.
     *
     * @param level
     *            enumeration that specifies the individual log level
     * @param className
     *            String the name of the class calling the log
     * @param methodName
     *            String the name of the method calling the log
     * @param message
     *            String containing message to output/save in output logs
     */
    public static void log ( final LogLevel level, final String className, final String methodName,
            final String message ) {
        log( level, "[" + className + "]" + "[" + methodName + "]" + message );
    }

    /**
     * Basic Logging statement that logs a provided message at the information
     * level.
     *
     * @param message
     *            String containing message to output/save in output logs
     */
    public static void log ( final String message ) {
        log( LogLevel.INFO, message );
    }

    /**
     * LogEntry class that handles output of logs at certain levels. Designed to
     * be modifiable for integration into database but presently outputs to
     * standard output.
     *
     * @author John-Michael Caskey
     */
    private static class LogEntry {
        /** enumeration that specifies the individual log level */
        private final LogLevel level;
        /** String containing message to output/save in output logs */
        private final String message;

        /**
         * Constructor to create log entry before saving to log location.
         *
         * @param level
         *            enumeration that specifies the individual log level
         * @param message
         *            String containing message to output/save in output logs
         */
        public LogEntry ( final LogLevel level, final String message ) {
            this.level = level;
            this.message = message;
        }

        /**
         * Saves the log entry by printing to standard output.
         */
        public void save () {
            String extraSpaces = " ";
            switch (level) {
                case INFO:
                case NONE:
                    extraSpaces = "    ";
                    break;
                case ERROR:
                    extraSpaces = "   ";
                    break;
                case STATUS:
                    extraSpaces = "  ";
                    break;
                case VERBOSE:
                case WARNING:
                default:
                    break;
            }
            output.println( "[" + level + "]" + extraSpaces + message );
        }
    }
}
