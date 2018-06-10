/**
 *
 */
package com.cisco.acvpj.session;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

/**
 * @author Michael Caskey
 *
 */
public class ACVPTestSessionTest {

    @Test
    public void test () throws FileNotFoundException {
        ACVPTestSession test1;
        try {
            test1 = new ACVPTestSession();
        } catch ( final Exception e ) {
            fail( "error in default setup of Test Session:" + e.getMessage() );
        }

        try {
            test1 = new ACVPTestSession( "config/sample_run.json" );
            assertTrue( test1.PrepareRegisterData().isJsonArray() );
        } catch ( final Exception e ) {
            fail( "error in default configuration setup of Test Session" );
        }

        try {
            test1 = new ACVPTestSession( "config/no_such_configuration_file.config" );
            fail( "Build test session with nonexistent configuration" );
        } catch ( final Exception e ) {
            // Pass test
        }
        test1 = new ACVPTestSession( "config/sample_run.json" );
        try {
            test1.setServerInfo( null, 443 );
            fail( "Error accepted null server" );
        } catch ( final Exception e ) {
            // Pass test
        }

        try {
            test1.setServerInfo( "", 443 );
            fail( "Error accepted empty server" );
        } catch ( final Exception e ) {
            // Pass test
        }

        try {
            test1.setServerInfo( "123.0.0.1", -1 );
            fail( "Error accepted negative port" );
        } catch ( final Exception e ) {
            // Pass test
        }

        try {
            test1.setPathSegment( null );
            fail( "Error accepted null path segment" );
        } catch ( final Exception e ) {
            // Pass test
        }

        try {
            test1.setPathSegment( "" );
            fail( "Error accepted empty path segment" );
        } catch ( final Exception e ) {
            // Pass test
        }

    }

}
