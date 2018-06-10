/**
 *
 */
package com.cisco.acvpj.environment;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.gson.JsonObject;

/**
 * Test the container class for operational environment.
 *
 * @author John-Michael Caskey
 */
public class OperationalEnvironmentTest {

    @Test
    public void test () {
        final OperationalEnvironment test = new OperationalEnvironment();

        try {
            test.setInfo( null );
            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModule( null );
            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendor( null );
            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setInfo( null );
            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( null, "test1", "test2", "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", null, "test2", "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", "test1", null, "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", "test1", "test2", null );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( null, "test1", "test2", "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", null, "test2", "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", "test1", null, "test3" );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", "test1", "test2", null );

            fail( "Accepted null argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "", "test1", "test2", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", "", "test2", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", "test1", "", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setModuleInfo( "test0", "test1", "test2", "" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "", "test1", "test2", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", "", "test2", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", "test1", "", "test3" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        try {
            test.setVendorInfo( "test0", "test1", "test2", "" );

            fail( "Accepted empty argument." );
        } catch ( final Exception e ) {
            // Pass
        }

        test.setModuleInfo( "test0", "test1", "test2", "test3" );
        test.setVendorInfo( "test0", "test1", "test2", "test3" );

        final JsonObject obj = test.toJsonObject();
        assertTrue( obj.isJsonObject() );

        try {
            new OperationalEnvironment( test.getVendor(), test.getModule(),
                    test.getInfo() );
        } catch ( final Exception e ) {
            fail( e.getMessage() );
        }
    }

}
