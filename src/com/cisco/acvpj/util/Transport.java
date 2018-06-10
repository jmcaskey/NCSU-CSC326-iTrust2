package com.cisco.acvpj.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Base64;

import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * This class is responsible for carrying out all HTTPS requests with the NIST
 * NIST ACVP server. The various HTTPS requests are: register, retrieve test
 * vectors, submit vector responses, and retrieve results
 *
 * @author Brandon Nguyen
 */
public class Transport {

    /** Default authentication files and values */
    private static String acvCAFile = null;
    private static String acvCertFile = null;
    private static String acvKeyFile = null;

    /** PKCS12 file generated from acv_cert_file and acv_key_file in OpenSSL */
    private static String acv_p12_file = "./auth/ncsu_java.p12";

    /** PKCS12 file generated from acv_cert_file and acv_key_file in OpenSSL */
    private static String acv_p12_password = "ncsujava";

    /** Authorization token from registration */
    private static String authorizationToken = "";

    /** Access token from login **/
    private static String loginToken = "";

    /** Used in 2-Factor Authentication to generate TOTP **/
    private static final int[] DIGITS_POWER = {1,10,100,1000,10000,100000,1000000,10000000,100000000};

    /**
     * Initialize certificate authentication required for HTTPS connection to
     * the server. The KeyStore object uses the generated PKCS12 file given by
     * acv_p12_file and password given by acv_p12_password. No TrustStore object
     * is passed to the SSLContext object so the default Java cacerts file is
     * used.
     */
    public void initAuthentication ( final Boolean trustAll ) {
        try {
            final KeyStore clientStore = KeyStore.getInstance( "PKCS12" );
            clientStore.load( new FileInputStream( acv_p12_file ), acv_p12_password.toCharArray() );

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
            kmf.init( clientStore, acv_p12_password.toCharArray() );

            final CustomX509KeyManager customKeyManager = new CustomX509KeyManager(
                    (X509KeyManager) kmf.getKeyManagers()[0] );

            final SSLContext sc = SSLContext.getInstance( "TLS" );

            if ( !trustAll ) {
                sc.init( new KeyManager[] { customKeyManager }, null, new java.security.SecureRandom() );
            } else {
                final TrustManager trm = new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers () {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted ( final X509Certificate[] certs, final String authType ) {

                    }

                    @Override
                    public void checkServerTrusted ( final X509Certificate[] certs, final String authType ) {
                    }
                };

                sc.init( new KeyManager[] { customKeyManager }, new TrustManager[] { trm },
                        new java.security.SecureRandom() );
            }

            HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error reading certificate: " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * This is the transport method used within ACVPJ to login with the NIST
     * ACVP server. This involves doing 2-Factor authentication and submitting a TOTP.
     *
     * @param loginInfo
     *            - JSON array containing login message that will be sent
     *            to the server.
     * @param urlString
     *            - URL to make request
     * @return Login response from the server - should contain access token
     */
    public JsonArray login ( final JsonArray loginInfo, final String urlString ) {

        JsonArray loginResult = null;

        try {

            final String loginInfoString = loginInfo.toString();

            final URL url = new URL( urlString );
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setDoOutput( true );
            con.setDoInput( true );

            con.setRequestMethod( "POST" );

            con.setRequestProperty( "Content-Type", "application/json; charset=UTF-8" );

            final byte[] outputInBytes = loginInfoString.getBytes( "UTF-8" );
            final OutputStream os = con.getOutputStream();
            os.write( outputInBytes );
            os.close();

            final BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );

            String temp = "";
            String rawResponse = "";

            while ( ( temp = br.readLine() ) != null ) {
                rawResponse += temp;
            }

            final JsonParser jsonParser = new JsonParser();
            loginResult = (JsonArray) jsonParser.parse( rawResponse );

            con.disconnect();
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error logging in: " + e.getMessage());
            System.exit(0);
        }

        return loginResult;
    }

    /**
     * This is the transport method used within ACVPJ to register with the NIST
     * ACVP server.
     *
     * @param registrationInfo
     *            - JSON array containing registration message that will be sent
     *            to the server.
     * @param urlString
     *            - URL to make request
     * @return Registration response from the server
     */
    public JsonArray register ( final JsonArray registrationInfo, final String urlString ) {

        JsonArray registrationResult = null;

        try {

            final String registrationInfoString = registrationInfo.toString();

            final URL url = new URL( urlString );
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setDoOutput( true );
            con.setDoInput( true );

            con.setRequestMethod( "POST" );

            con.setRequestProperty( "authorization", "Bearer " + loginToken );
            con.setRequestProperty( "Content-Type", "application/json; charset=UTF-8" );

            final byte[] outputInBytes = registrationInfoString.getBytes( "UTF-8" );
            final OutputStream os = con.getOutputStream();
            os.write( outputInBytes );
            os.close();

            final BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );

            String temp = "";
            String rawResponse = "";

            while ( ( temp = br.readLine() ) != null ) {
                rawResponse += temp;
            }

            final JsonParser jsonParser = new JsonParser();
            registrationResult = (JsonArray) jsonParser.parse( rawResponse );

            con.disconnect();
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error registering with NIST server: " + e.getMessage());
            System.exit(0);
        }

        return registrationResult;
    }

    /**
     * This is the transport method used within ACVPJ to retrieve vector
     * information from the NIST ACVP server.
     *
     * @param vectorId
     *            - ID of the vector to retrieve
     * @param urlString
     *            - URL to make request
     * @return Vector information from the server
     */
    public JsonArray getVectorSet ( final String vectorId, final String urlString ) {

        LoggerUtil.log( LogLevel.STATUS, "VectorId: " + vectorId );

        JsonArray getVectorResult = null;

        try {

            final URL url = new URL( urlString + vectorId );
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod( "GET" );
            con.setRequestProperty( "authorization", "Bearer " + authorizationToken );

            final BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );

            String temp = "";
            String rawResponse = "";

            while ( ( temp = br.readLine() ) != null ) {
                rawResponse += temp;
            }

            final JsonParser jsonParser = new JsonParser();
            getVectorResult = (JsonArray) jsonParser.parse( rawResponse );

            if ( getVectorResult.toString().contains( "retry" ) ) {
                LoggerUtil.log( LogLevel.STATUS, "retrying..." );
                con.disconnect();
                Thread.sleep( getVectorResult.get( 1 ).getAsJsonObject().get( "retry" ).getAsInt() * 1000 );
                return this.getVectorSet( vectorId, urlString );
            }

            if ( getVectorResult.toString().contains( "error" ) ) {
                LoggerUtil.log( LogLevel.ERROR, "Error during vector generation." );
                throw new ACVPJUnsupportedOperationException();
            }

            con.disconnect();
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error getting vector set: " + e.getMessage());
            System.exit(0);
        }

        return getVectorResult;
    }

    /**
     * This is the transport method used within ACVPJ to submit the client's
     * vector test results to the NIST ACVP server.
     *
     * @param vectorId
     *            - ID of the vector to submit test results for
     * @param testResults
     *            - Test results in JSON format
     * @param urlString
     *            - URL to make request
     * @return confirmation of test result submission
     */
    public JsonArray submitVectorTestResults ( final String vectorId, final JsonArray testResults,
            final String urlString ) {

        JsonArray getVectorResult = null;

        try {

            final String testResultsString = testResults.toString();

            final URL url = new URL( urlString + vectorId );
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setDoOutput( true );
            con.setDoInput( true );

            con.setRequestMethod( "POST" );

            con.setRequestProperty( "authorization", "Bearer " + authorizationToken );
            con.setRequestProperty( "Content-Type", "application/json; charset=UTF-8" );

            final byte[] outputInBytes = testResultsString.getBytes( "UTF-8" );
            final OutputStream os = con.getOutputStream();
            os.write( outputInBytes );
            os.close();

            final BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );

            String temp = "";
            String rawResponse = "";

            while ( ( temp = br.readLine() ) != null ) {
                rawResponse += temp;
            }

            final JsonParser jsonParser = new JsonParser();
            getVectorResult = (JsonArray) jsonParser.parse( rawResponse );

            if ( getVectorResult.toString().contains( "retry" ) ) {
                Thread.sleep( getVectorResult.get( 1 ).getAsJsonObject().get( "retry" ).getAsInt() * 1000 );
                return this.submitVectorTestResults( vectorId, testResults, urlString );
            }

            con.disconnect();
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error submitting test results: " + e.getMessage());
            System.exit(0);
        }

        return getVectorResult;
    }

    /**
     * This is the transport method used within ACVPJ to retrieve vector set
     * results from the NIST ACVP server.
     *
     * @param vectorId
     *            - ID of the vector set to retrieve results for
     * @param urlString
     *            - URL to make request
     * @return Vector set results from server
     */
    public JsonArray getVectorSetResults ( final String vectorId, final String urlString ) {

        JsonArray getVectorSetResult = null;

        try {

            final URL url = new URL( urlString + vectorId );
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod( "GET" );
            con.setRequestProperty( "authorization", "Bearer " + authorizationToken );

            final BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );

            String temp = "";
            String rawResponse = "";

            while ( ( temp = br.readLine() ) != null ) {
                rawResponse += temp;
            }

            final JsonParser jsonParser = new JsonParser();
            getVectorSetResult = (JsonArray) jsonParser.parse( rawResponse );

            if ( getVectorSetResult.toString().contains( "retry" ) ) {
                LoggerUtil.log( LogLevel.STATUS, "retrying..." );
                Thread.sleep( 30 * 1000 );
                return this.getVectorSetResults( vectorId, urlString );
            }

            con.disconnect();
        } catch ( final Exception e ) {
            LoggerUtil.log(LogLevel.ERROR, "Error getting vector set results: " + e.getMessage());
            System.exit(0);
        }

        return getVectorSetResult;
    }

    /**
     * Setter method for authorization token variable MUST BE CALLED BEFORE
     * getVector(), submitVectorTestResults(), and getVectorSetResults()
     *
     * @param authorizationToken
     *            authorization token from registration
     */
    public void setAuthorizationToken ( final String authorizationToken ) {
        Transport.authorizationToken = authorizationToken;
    }

    /**
     * Set the path to file for the Certificate Authority chain file.
     *
     * @param caChainFile
     *            String path to CA file
     */
    public void setCACerts ( final String caChainFile ) {
        if ( caChainFile == null || caChainFile.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid CA chain filepath provided. CA chain filepath must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid CA chain filepath provided. CA chain filepath must be string of length greater than 0." );
        }

        LoggerUtil.log( LogLevel.INFO, "CA chain file path set: " + caChainFile );
        setAcvCAFile(caChainFile);
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
        if ( certFile == null || certFile.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid Certificate chain filepath provided. CA certificate filepath must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid Certificate chain filepath provided. CA certificate filepath must be string of length greater than 0." );
        }

        if ( keyFile == null || keyFile.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid key filepath provided. Key filepath must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid key filepath provided. Key filepath must be string of length greater than 0." );
        }

        LoggerUtil.log( LogLevel.INFO, "Certificate path set: " + certFile );
        LoggerUtil.log( LogLevel.INFO, "key path set: " + keyFile );
        setAcvCertFile(certFile);
        setAcvKeyFile(keyFile);
    }

    /**
     * Setter method for login token variable. This should be set using the token returned from login().
     *
     * @param token from login with server
     */
    public void setLoginToken(String token) {
        Transport.loginToken = token;
    }

    /**
     * Found at: https://tools.ietf.org/html/rfc6238
     *
     * This method uses the JCE to provide the crypto algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
     * HmacSHA512)
     * @param keyBytes: the bytes to use for the HMAC key
     * @param text: the message or text to be authenticated
     *
     * @return processed byte array
     */
    private byte[] hmac_sha(String crypto, String key, byte[] text) {
        try {
            Charset asciiCs = Charset.forName("US-ASCII");
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                    new SecretKeySpec(asciiCs.encode(key).array(), crypto);
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    /**
     * Found at: https://tools.ietf.org/html/rfc6238
     *
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     * @param crypto: the crypto function to use
     *
     * @return: a numeric String in base 10 that includes
     * {@link truncationDigits} digits
     */
    public String generateTOTP(String key, String time, String returnDigits, String crypto) {

        // Base 64 decode of key
        byte[] decoded = Base64.decode(key);

        String decodedString = "";

        try {
            decodedString = new String(decoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int codeDigits = Integer.decode(returnDigits).intValue();
        String result = null;

        // Get the HEX in a Byte[]
        byte[] msg = DatatypeConverter.parseHexBinary(time);

        // HMAC-SHA256
        byte[] hash = hmac_sha(crypto, decodedString, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary =
                ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }

        return result;
    }


    public static String getAcvCAFile() {
        return acvCAFile;
    }

    public static void setAcvCAFile(String acvCAFile) {
        Transport.acvCAFile = acvCAFile;
    }


    public static String getAcvCertFile() {
        return acvCertFile;
    }

    public static void setAcvCertFile(String acvCertFile) {
        Transport.acvCertFile = acvCertFile;
    }


    public static String getAcvKeyFile() {
        return acvKeyFile;
    }

    public static void setAcvKeyFile(String acvKeyFile) {
        Transport.acvKeyFile = acvKeyFile;
    }


    /**
     * Custom X509KeyManager class that automatically returns the ACVPJ client
     * certificate in chooseClientAlias()
     */
    private class CustomX509KeyManager extends X509ExtendedKeyManager {
        X509KeyManager defaultKeyManager;

        public CustomX509KeyManager ( final X509KeyManager inKeyManager ) {
            defaultKeyManager = inKeyManager;
        }

        @Override
        public String chooseClientAlias ( final String[] strings, final Principal[] prncpls, final Socket socket ) {
            return "1";
        }

        @Override
        public String[] getClientAliases ( final String string, final Principal[] prncpls ) {
            return defaultKeyManager.getClientAliases( string, prncpls );
        }

        @Override
        public String[] getServerAliases ( final String string, final Principal[] prncpls ) {
            return defaultKeyManager.getServerAliases( string, prncpls );
        }

        @Override
        public String chooseServerAlias ( final String keyType, final Principal[] issuers, final Socket socket ) {
            return defaultKeyManager.chooseServerAlias( keyType, issuers, socket );
        }

        @Override
        public X509Certificate[] getCertificateChain ( final String alias ) {
            return defaultKeyManager.getCertificateChain( alias );
        }

        @Override
        public PrivateKey getPrivateKey ( final String alias ) {
            return defaultKeyManager.getPrivateKey( alias );
        }

    }
}
