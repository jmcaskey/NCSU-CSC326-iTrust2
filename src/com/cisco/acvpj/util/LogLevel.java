/**
 *
 */
package com.cisco.acvpj.util;

/**
 * A LogLevel represents a level of specificity in which that logger in the
 * system should use to label log statements. This is used to determine which
 * statements are logged based off log level set by commandline on project run.
 *
 * @author John-Michael Caskey
 */
public enum LogLevel {
    NONE, ERROR, WARNING, STATUS, INFO, VERBOSE
}
