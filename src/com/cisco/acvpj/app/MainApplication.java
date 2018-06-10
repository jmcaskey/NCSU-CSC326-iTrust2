package com.cisco.acvpj.app;

import com.cisco.acvpj.session.ACVPTestSession;

/**
 * This is the application that controls the high-level flow of ACVPJ.
 *
 * @author Brandon Nguyen (btnguye3)
 */
public class MainApplication {

    public static final String DEFAULT_SERVER = "demo.acvts.nist.gov";
    public static final int DEFAULT_PORT = 443;
    public static final String DEFAULT_CA_CHAIN = "auth/mozzila-trust-anchors.pem";
    public static final String DEFAULT_CERT = "auth/ncsu_java.cer";
    public static final String DEFAULT_KEY = "auth/ncsu_java.pem";

    public static String server;
    public static int port;
    public static String ca_chain_file;
    public static String cert_file;
    public static String key_file;
    public static String path_segment;

    /**
     * Displays information on usage and possible arguments
     */
    private static void print_usage () {
        System.out.print( "\nInvalid usage...\n" );
        System.out.print( "acvp_app does not require any argument, however logging level can be\n" );
        System.out.print( "controlled using:\n" );
        System.out.print( "      -none\n" );
        System.out.print( "      -error\n" );
        System.out.print( "      -warn\n" );
        System.out.print( "      -status(default)\n" );
        System.out.print( "      -info\n" );
        System.out.print( "      -verbose\n" );
        System.out.print( "\n" );
        System.out.print( "In addition some options are passed to acvp_app using\n" );
        System.out.print( "environment variables.  The following variables can be set:\n\n" );
        System.out.print( "    ACV_SERVER (when not set, defaults to " + DEFAULT_SERVER + ")\n" );
        System.out.print( "    ACV_PORT (when not set, defaults to " + DEFAULT_PORT + ")\n" );
        System.out.print( "    ACV_URI_PREFIX (when not set, defaults to null)\n" );
        System.out.print( "    ACV_CA_FILE (when not set, defaults to " + DEFAULT_CA_CHAIN + ")\n" );
        System.out.print( "    ACV_CERT_FILE (when not set, defaults to " + DEFAULT_CERT + ")\n" );
        System.out.print( "    ACV_KEY_FILE (when not set, defaults to " + DEFAULT_KEY + ")\n\n" );
        System.out.print( "The CA certificates, cert and key should be PEM encoded. There should be no\n" );
        System.out.print( "password on the key file.\n" );
    }

    /**
     * ADVPJ main procedure
     */
    public static void main ( final String[] args ) {
        if ( args.length > 2 ) {
            print_usage();
            System.exit( 1 );
        }

        final ACVPTestSession session = new ACVPTestSession( "config/sample_run.json");
        session.loginWithServer();
        session.registerWithServer();
        session.processTests();
        session.checkTestResults();
    }

}
