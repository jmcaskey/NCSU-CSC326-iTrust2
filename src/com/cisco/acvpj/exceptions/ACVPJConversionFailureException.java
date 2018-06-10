/**
 *
 */
package com.cisco.acvpj.exceptions;

/**
 * @author Chris Miller
 *
 */
public class ACVPJConversionFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ACVPJConversionFailureException() {
        super();
    }

    public ACVPJConversionFailureException(String string) {
        super(string);
    }

}
