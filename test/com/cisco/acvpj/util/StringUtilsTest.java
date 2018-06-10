/**
 *
 */
package com.cisco.acvpj.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Michael Caskey
 *
 */
public class StringUtilsTest {

    @Test
    public void HexToBinarytest () {
        new StringUtils();
        byte[] test = StringUtils.hexToBinary( "ff" );
        assertEquals( 1, test.length );
        assertEquals( -1, test[0] );

        test = StringUtils.hexToBinary( "0001" );
        assertEquals( 2, test.length );
        assertEquals( 0, test[0] );
        assertEquals( 1, test[1] );
    }

    @Test
    public void BitBinarytest () {
        try {
            StringUtils.bitToBinary( "" );
            fail( "Accepted empty string" );
        } catch ( final Exception e ) {
            // Pass
        }

        final byte[] test = StringUtils.bitToBinary( "00000001" );
        assertEquals( 8, test.length );
        assertEquals( 1, test[0] );
    }

    @Test
    public void BinaryToBittest () {
        try {
            StringUtils.binaryToBit( "" );
            fail( "Accepted empty string" );
        } catch ( final Exception e ) {
            // Pass
        }

        String test = StringUtils.binaryToBit( "00000001" );
        assertEquals( 8, test.length() );
        assertEquals( "00000000", test );

        test = StringUtils.binaryToBit( "10010001" );
        assertEquals( 8, test.length() );
        assertEquals( "00000001", test );
    }

    @Test
    public void byteToStringtest () {
        byte[] test = StringUtils.hexToBinary( "ff" );
        assertEquals( 1, test.length );
        assertEquals( -1, test[0] );
        assertEquals( "FF", StringUtils.byteToString( test ) );

        test = StringUtils.hexToBinary( "0001" );
        assertEquals( 2, test.length );
        assertEquals( 0, test[0] );
        assertEquals( 1, test[1] );
        assertEquals( "0001", StringUtils.byteToString( test ) );
    }

}
