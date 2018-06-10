package edu.ncsu.csc.itrust2.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * Utility class for generating random strings for help in the password
 * requirements.
 * @author Devan Issac
 */
public class RandomString {

    /**
     * Generate a random string.
     * @return a randomly generated string
     */
    public String nextString () {
        for ( int idx = 0; idx < buf.length; ++idx ) {
            buf[idx] = symbols[random.nextInt( symbols.length )];
        }
        return new String( buf );
    }

    /** String containing all uppercase alphabetical characters */
    public static final String UPPER    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** String containing all lowercase alphabetical characters */
    public static final String LOWER    = UPPER.toLowerCase( Locale.ROOT );
    /** String containing all base 10 digits */
    public static final String DIGITS   = "0123456789";
    /** Concatenation of all above strings */
    public static final String ALPHANUM = UPPER + LOWER + DIGITS;

    private final Random       random;

    private final char[]       symbols;

    private final char[]       buf;

    /**
     * Creates a new Random String creator with the given range of symbols and length.
     * @param length The length of the random strings this will generate
     * @param random The random object used in creating strings
     * @param symbols The range of symbols to create random strings from
     */
    public RandomString ( final int length, final Random random, final String symbols ) {
        if ( length < 1 ) {
            throw new IllegalArgumentException();
        }
        if ( symbols.length() < 2 ) {
            throw new IllegalArgumentException();
        }
        this.random = Objects.requireNonNull( random );
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

}
