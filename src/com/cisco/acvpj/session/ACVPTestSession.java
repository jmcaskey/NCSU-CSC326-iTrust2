package com.cisco.acvpj.session;

import java.io.FileReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

import com.cisco.acvpj.algorithm.Algorithm;
import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.environment.OperationalEnvironment;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.kat_handler.KATHandler;
import com.cisco.acvpj.kat_handler.KATHandlerFactory;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.cisco.acvpj.util.Transport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author John-Michael Caskey (jmcaskey)
 */
public class ACVPTestSession {

    /** ACV protocol version used by server in communication. */
    private static final String ACV_VERSION = "0.4";

    private OperationalEnvironment environment;

    /** List of test vector ids retrieved from the server */
    private LinkedList<Integer> vectorSetList;

    /** Access token returned by login */
    private String accessToken;

    /** Test sessions Java Web Token */
    private String jwtToken;

    /** List of cryptomodules capabilities to encrypt, mac and hash */
    private JsonArray capsList;

    /** Server response to registering test session */
    private JsonArray regResponse;

    /** Server response to registering test session */
    private JsonArray loginResponse;

    /** JSON object that holds crypto output that is sent to the server */
    private JsonArray katResponse;

    /** The server address of the server hosting ACVP testing. */
    private String server;
    /** The port to access the ACVP testing API. */
    private int port;
    /** The path segment to the API url substrings. */
    private String pathSegment;

    /**
     * Reference to the KAT Handler factory where handlers are retrieved to
     * handle testing
     */
    private KATHandlerFactory katHandlerFactory;

    /**
     *
     */
    private Transport httpsClient;

    /**
     * Constructor
     */
    public ACVPTestSession () {
        this( "127.0.0.1", 443, "/" );
    }

    public ACVPTestSession ( final String filepath ) {
        JsonObject config;
        try {
            final JsonParser reader = new JsonParser();
            config = reader.parse( new FileReader( filepath ) ).getAsJsonObject();

            final String level = config.get( "log_level" ).getAsString();
            final String server = config.get( "server" ).getAsString();
            final int port = config.get( "port" ).getAsInt();
            final String pathSeg = config.get( "path_segment" ).getAsString();
            final String certPath = config.get( "cert_path" ).getAsString();
            final String caPath = config.get( "ca_path" ).getAsString();
            final String keyPath = config.get( "key_path" ).getAsString();

            httpsClient = new Transport();

            // Set log level
            LoggerUtil.logLevel = LogLevel.valueOf( level );
            // set server information
            setServerInfo( server, port );
            // set path segment
            setPathSegment( pathSeg );
            // set path to CA file
            setCACerts( caPath );
            // Set path to certificate and key files
            setCertKey( certPath, keyPath );

            // Create a new operational environment
            environment = new OperationalEnvironment();

            final String vendorName = config.get( "vendor_name" ).getAsString();
            final String vendorURL = config.get( "vendor_url" ).getAsString();
            final String contactName = config.get( "contact_name" ).getAsString();
            final String contactEmail = config.get( "contact_email" ).getAsString();

            // Set default cisco vendor information
            environment.setVendorInfo( vendorName, vendorURL, contactName, contactEmail );

            final String moduleName = config.get( "module_name" ).getAsString();
            final String moduleType = config.get( "module_type" ).getAsString();
            final String moduleVersion = config.get( "module_version" ).getAsString();
            final String moduleDescription = config.get( "module_description" ).getAsString();

            // set module information and attributes
            environment.setModuleInfo( moduleName, moduleType, moduleVersion, moduleDescription );

            katHandlerFactory = KATHandlerFactory.getInstance();

            final JsonArray namesArray = config.get( "algorithms" ).getAsJsonArray();
            final String[] names = new String[namesArray.size()];
            for ( int i = 0; i < namesArray.size(); i++ ) {
                names[i] = namesArray.get( i ).getAsString();
            }

            registerCapabilities( names );

            LoggerUtil.log( LogLevel.STATUS, "Initializing https client" );
            httpsClient.initAuthentication( false );

            this.vectorSetList = new LinkedList<Integer>();

        } catch ( final Exception e ) {
            throw new IllegalArgumentException( e.getMessage() );
        }

    }

    /**
     *
     * @param server
     * @param port
     * @param pathSeg
     */
    public ACVPTestSession ( final String server, final int port, final String pathSeg ) {
        this( LogLevel.STATUS, server, port, pathSeg );
    }

    /**
     *
     *
     * @param level
     * @param server
     * @param port
     * @param pathSeg
     */
    public ACVPTestSession ( final LogLevel level, final String server, final int port, final String pathSeg ) {
        this( level, server, port, pathSeg, "certs/acvp-private-root-ca.crt.pem", "certs/sto-labsrv2-client-cert.pem",
                "certs/sto-labsrv2-client-key.pem" );
    }

    /**
     *
     * @param level
     * @param server
     * @param port
     * @param pathSeg
     * @param caPath
     * @param certPath
     * @param keyPath
     */
    public ACVPTestSession ( final LogLevel level, final String server, final int port, final String pathSeg,
            final String caPath, final String certPath, final String keyPath ) {

        httpsClient = new Transport();

        // Set log level
        LoggerUtil.logLevel = level;
        // set server information
        setServerInfo( server, port );
        // set path segment
        setPathSegment( pathSeg );
        // set path to CA file
        setCACerts( caPath );
        // Set path to certificate and key files
        setCertKey( certPath, keyPath );

        // Create a new operational environment
        environment = new OperationalEnvironment();

        // Set default cisco vendor information
        environment.setVendorInfo( "Cisco Systems", "www.cisco.com", "Barry Fussell", "bfussell@cisco.com" );

        // set module information and attributes
        environment.setModuleInfo( "OpenSSL", "software", "100020bf", "FOM 6.2a" );

        katHandlerFactory = KATHandlerFactory.getInstance();

        final String[] names = { "SHA-1" };
        registerCapabilities( names );

        LoggerUtil.log( LogLevel.STATUS, "Initializing https client" );
        httpsClient.initAuthentication( false );

        this.vectorSetList = new LinkedList<Integer>();
    }

    /**
     * Set the server information for the HTTPS client to specify The server
     * location and access port
     *
     * @param server
     *            String containing the network address
     * @param port
     *            Port number to use to access the server (Likely 443)
     */
    public void setServerInfo ( final String server, final int port ) {
        if ( server == null || server.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid server provided. Server must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid server provided. Server must be string of length greater than 0." );
        }
        if ( port < 0 ) {
            LoggerUtil.log( LogLevel.ERROR, "Invalid port provided. Port must be greater than 0." );
            throw new IllegalArgumentException( "Invalid port provided. Port must be greater than 0." );
        }

        LoggerUtil.log( LogLevel.NONE, "ACVP Server: " + server );
        this.server = server;
        LoggerUtil.log( LogLevel.NONE, "ACVP Port: " + port );
        this.port = port;
    }

    /**
     * Sets the new path string used to communicate to the server
     *
     * @param pathSegment
     *            String containing path within http call to use in next
     *            communication
     */
    public void setPathSegment ( final String pathSegment ) {
        if ( pathSegment == null || pathSegment.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid path segment provided. Path segment must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid path segment provided. Path segment must be string of length greater than 0." );
        }

        LoggerUtil.log( LogLevel.INFO, "Path segment set: " + pathSegment );
        this.pathSegment = pathSegment;
    }

    /**
     * Set the path to file for the Certificate Authority chain file.
     *
     * @param caChainFile
     *            String path to CA file
     */
    public void setCACerts ( final String caChainFile ) {
        httpsClient.setCACerts( caChainFile );
    }

    /**
     * Set the certificate and key location in project path
     *
     * @param certFile
     *            String path to certificate file
     * @param keyFile
     *            String path to key file
     */
    public void setCertKey ( final String certFile, final String keyFile ) {
        httpsClient.setCertKey( certFile, keyFile );
    }

    /**
     * Set the capabilities list based on passed string array
     * @param names string array of algorithms
     */
    private void registerCapabilities ( final String[] names ) {
        capsList = Algorithm.getAlgorithms( names );
    }

    /**
     * Put all the registration date into a json array and return it
     * @return json array with registration data
     */
    public JsonArray PrepareRegisterData () {
        final JsonArray regArray = new JsonArray();

        final JsonObject acvv = new JsonObject();
        acvv.addProperty( "acvVersion", ACV_VERSION );

        regArray.add( acvv );

        final JsonObject oe = new JsonObject();
        oe.addProperty( "operation", "register" );
        oe.addProperty( "certificateRequest", "yes" );
        oe.addProperty( "debugRequest", "no" );
        oe.addProperty( "production", "no" );
        oe.addProperty( "encryptAtRest", "yes" );

        oe.add( "oeInformation", environment.toJsonObject() );

        final JsonObject ce = new JsonObject();
        ce.add( "algorithms", capsList );

        oe.add( "capabilityExchange", ce );

        regArray.add( oe );
        return regArray;
    }
    
    /**
     * Put the login data into a json array and return it 
     * @param totp time-based one time password gotten from the nist sever
     * @return json array with login data
     */
    public JsonArray PrepareLoginData (String totp) {
        final JsonArray loginArray = new JsonArray();

        final JsonObject acvv = new JsonObject();
        acvv.addProperty( "acvVersion", ACV_VERSION );

        loginArray.add( acvv );

        final JsonObject pw = new JsonObject();
        pw.addProperty( "password", totp );

        loginArray.add( pw );

        return loginArray;
    }

    /**
     * Communicates with the server the relavent registration information set in
     * session
     */
    public void registerWithServer () {
        final JsonArray regArray = PrepareRegisterData();

        LoggerUtil.log( LogLevel.INFO, "Register session: " );
        LoggerUtil.log( LogLevel.INFO, "Register request: " + regArray.toString() );
        regResponse = httpsClient.register( regArray,
                "https://" + server + ":" + port + pathSegment + "acvp/validation/acvp/register" );

        jwtToken = regResponse.get( 1 ).getAsJsonObject().get( "accessToken" ).getAsString();
        LoggerUtil.log( LogLevel.INFO, "JWT: " + jwtToken );

        final JsonArray vsIDs = regResponse.get( 1 ).getAsJsonObject().get( "capabilityResponse" ).getAsJsonObject()
                .get( "vectorSets" ).getAsJsonArray();
        for ( int i = 0; i < vsIDs.size(); i++ ) {
            this.vectorSetList.add( vsIDs.get( i ).getAsJsonObject().get( "vsId" ).getAsInt() );
        }

    }

    /**
     * 2-Factor Authentication login with server
     */
    public void loginWithServer () {

        // Two-Factor Authentication - TOTP Algorithm

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        long seconds = cal.getTimeInMillis() / 1000;

        long time = seconds / 30;

        String steps = "0";
        steps = Long.toHexString(time).toUpperCase();
        while (steps.length() < 16) {
            steps = "0" + steps;
        }

        String totp = httpsClient.generateTOTP("RGlzYWJsZWREaXNhYmxlZERpc2FibGVkRGlzYWJsZWQ=", steps, "8", "HmacSHA256");

        // Login by sending TOTP to server

        final JsonArray loginArray = PrepareLoginData(totp);

        LoggerUtil.log( LogLevel.INFO, "Login session: " );
        LoggerUtil.log( LogLevel.INFO, "Login request: " + loginArray.toString() );
        loginResponse = httpsClient.login( loginArray,
                "https://" + server + ":" + port + pathSegment + "acvp/validation/acvp/login" );

        accessToken = loginResponse.get( 1 ).getAsJsonObject().get( "accessToken" ).getAsString();

        LoggerUtil.log( LogLevel.INFO, "Access Token: " + accessToken );

        httpsClient.setLoginToken(accessToken);
    }

    /**
     * Starts processing tests vectors retrieved from the server using the KAT
     * handlers
     */
    public void processTests () {
        this.setKatResponse(new JsonArray());
        httpsClient.setAuthorizationToken( jwtToken );

        for ( int i = 0; i < vectorSetList.size(); i++ ) {
            final JsonArray vsResponse = getVectorSet( "" + vectorSetList.get( i ) );
            final JsonObject test = vsResponse.get( 1 ).getAsJsonObject();

            final String algString = test.get( "algorithm" ).getAsString();

            if ( algString == null ) {
                // TODO: maybe add a ACVPJMalformedJsonException
                LoggerUtil.log( LogLevel.ERROR, "unable to parse 'algorithm' from JSON" );
                throw new ACVPJUnsupportedOperationException();
            }

            final ACVPCipher algId = ACVPCipher.lookUpCipher( algString );

            KATHandler handler = katHandlerFactory.getKATHandler(algId);

            JsonObject response = null;

            try {
                response = handler.handler(test);
            } catch (ACVPJUnsupportedOperationException e) {
                LoggerUtil.log(LogLevel.ERROR, "Algorithm not supported.");
                response = new JsonObject();
            }

            final JsonArray regArray = new JsonArray();

            final JsonObject acvv = new JsonObject();
            acvv.addProperty( "acvVersion", ACV_VERSION );

            regArray.add( acvv );

            regArray.add( response );

            sendResponse( "" + vsResponse.get( 1 ).getAsJsonObject().get( "vsId" ).getAsInt(), regArray );
        }
    }
    
    /**
     * Return a json array containing the vector set.
     * @param vsId vector set id to return
     * @return
     */
    private JsonArray getVectorSet ( final String vsId ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final JsonArray response = httpsClient.getVectorSet( vsId,
                "https://" + server + ":" + port + pathSegment + "acvp/validation/acvp/vectors?vsId=" );
        LoggerUtil.log( LogLevel.INFO, gson.toJson(response) );
        return response;
    }

    private JsonArray sendResponse ( final String vsId, final JsonArray body ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LoggerUtil.log( LogLevel.STATUS, "Sending Results:" );
        LoggerUtil.log( LogLevel.INFO, gson.toJson(body) );
        final JsonArray response = httpsClient.submitVectorTestResults( vsId, body,
                "https://" + server + ":" + port + pathSegment + "acvp/validation/acvp/vectors?vsId=" );
        LoggerUtil.log( LogLevel.INFO, gson.toJson(response) );
        return response;
    }

    /**
     * Communicate with server the processed test results and get back a report
     */
    public void checkTestResults () {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int numPassed = 0;
        LoggerUtil.log(LogLevel.STATUS, "======================TEST RESULTS======================");
        for ( int i = 0; i < vectorSetList.size(); i++ ) {
            LoggerUtil.log( LogLevel.STATUS, "Getting results vector id: " + vectorSetList.get( i ) );
            final JsonArray vsResponse = httpsClient.getVectorSetResults( "" + vectorSetList.get( i ),
                    "https://" + server + ":" + port + pathSegment + "acvp/validation/acvp/results?vsId=" );
            LoggerUtil.log( LogLevel.VERBOSE, gson.toJson(vsResponse) );
            JsonObject results =vsResponse.get(1).getAsJsonObject().get("results").getAsJsonObject();
            if (results.get("disposition").getAsString().equals("passed")) {
                numPassed++;
            }
        }
        LoggerUtil.log(LogLevel.STATUS, "Results: " + numPassed + "/" + vectorSetList.size() + " tests passed.");
    }

    public JsonArray getKatResponse() {
        return katResponse;
    }

    public void setKatResponse(JsonArray katResponse) {
        this.katResponse = katResponse;
    }
}
