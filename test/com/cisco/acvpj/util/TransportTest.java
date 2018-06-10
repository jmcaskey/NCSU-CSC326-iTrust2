package com.cisco.acvpj.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * JUnit test class for Transport
 *
 * @author Brandon Nguyen
 */
public class TransportTest {

    /**
     * Mock server initialization
     */

    @Rule
    public MockServerRule mockServerRule = new MockServerRule( this, 8080 );

    private MockServerClient mockServerClient;

    @Test
    /**
     * Test login function with mock server
     */
    public void testLogin() {
    	Transport httpsClient = new Transport();
    	
    	String expectedResponse = "[{\"acvVersion\":\"0.4\"},{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0YTFjNTJmZC0wNTg2LTRhODAtYTFjZC0xNmNkNmI4Nzg2YTIiLCJpYXQiOjE1MjUyMTE5MzYsInN1YiI6IkVNQUlMQUREUkVTUz1iZnVzc2VsbEBjaXNjby5jb20sIENOPUFDVlBfTkNTVV9KQVZBLCBPVT1DU0MsIE89TkNTVSwgTD1SYWxlaWdoLCBTVD1OQywgQz1VUyIsImlzcyI6Ik5JU1QgQUNWVFMiLCJleHAiOjE1MjUyMTM3MzZ9.UkJIm8aIombu0b3vtNnlx8YEYrgRbxei3uVTTCVbJTA\"}]";
    	
    	// Two-Factor Authentication - TOTP Algorithm
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        
        long seconds = cal.getTimeInMillis() / 1000;
        
        long time = seconds / 30;
        
        String steps = "0";
        steps = Long.toHexString(time).toUpperCase();
        while (steps.length() < 16) steps = "0" + steps;
        
        String totp = httpsClient.generateTOTP("RGlzYWJsZWREaXNhYmxlZERpc2FibGVkRGlzYWJsZWQ=", steps, "8", "HmacSHA256");
        
        // Build login array to send with POST
        JsonArray loginArray = new JsonArray();

        JsonObject acvv = new JsonObject();
        acvv.addProperty( "acvVersion", "0.4" );

        loginArray.add( acvv );

        JsonObject pw = new JsonObject();
        pw.addProperty( "password", totp );
        
        loginArray.add( pw );
        
        // Set mock server behavior for test case
        mockServerClient.when( HttpRequest.request( "/acvp/validation/acvp/login" ).withMethod( "POST" ) )
                .respond( HttpResponse.response().withBody( expectedResponse ) );
        
        httpsClient.initAuthentication( true );
        
        final JsonArray response = httpsClient.login( loginArray,
                "https://localhost:8080/acvp/validation/acvp/login" );
        
        assertEquals( expectedResponse, response.toString() );
    }
    
    /**
     * Test registration function with mock server
     */
    @Test
    public void testRegister () {
        final String expectedResponse = "[{\"acvVersion\":\"0.4\"},{\"capabilityResponse\":{\"vectorSets\":[{\"vsId\":547},{\"vsId\":548},{\"vsId\":549}]},\"testSession\":{\"testId\":126},\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjMGQ0NWU3Yi0zNTZjLTRhMjctYjUyOS0yMmRiNTUwMzAzYWMiLCJpYXQiOjE1MjMzNzIwNDcsInN1YiI6IkVNQUlMQUREUkVTUz1iZnVzc2VsbEBjaXNjby5jb20sIENOPUFDVlBfTkNTVV9KQVZBLCBPVT1DU0MsIE89TkNTVSwgTD1SYWxlaWdoLCBTVD1OQywgQz1VUyIsImlzcyI6Ik5JU1QgQUNWVFMiLCJleHAiOjE1MjM2MzEyNDcsInZzSWQiOls1NDcsNTQ4LDU0OV19.MLfHN1adtHZcfL11pnuGuoiPtQw3I7XMyKYv9C0dOcY\"}]";

        String registrationString = "";
        try {
            registrationString = FileUtils.readFileToString( new File( "test\\files\\registration_input.txt" ),
                    "UTF-8" );
        } catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        validateJSON( registrationString );

        final JsonParser parser = new JsonParser();
        final JsonElement element = parser.parse( registrationString );
        final JsonArray registrationInfo = element.getAsJsonArray();

        // Set mock server behavior for test case
        mockServerClient.when( HttpRequest.request( "/acvp/validation/acvp/register" ).withMethod( "POST" ) )
                .respond( HttpResponse.response().withBody( expectedResponse ) );

        final Transport httpsClient = new Transport();
        httpsClient.initAuthentication( true );
        final JsonArray response = httpsClient.register( registrationInfo,
                "https://localhost:8080/acvp/validation/acvp/register" );

        assertEquals( expectedResponse, response.toString() );
    }

    /**
     * Test get vector set information with mock server
     */
    @Test
    public void testGetVectorSet () {
        String expectedResponse = "";

        try {
            expectedResponse = FileUtils.readFileToString( new File( "test\\files\\get_vector_544.txt" ), "UTF-8" );
        } catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Set mock server behavior for test case
        mockServerClient
                .when( HttpRequest.request( "/acvp/validation/acvp/vectors" ).withMethod( "GET" )
                        .withQueryStringParameter( "vsId", "544" ) )
                .respond( HttpResponse.response().withBody( expectedResponse ) );

        final Transport httpsClient = new Transport();
        httpsClient.initAuthentication( true );

        httpsClient.setAuthorizationToken(
                "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlODNhNWYyMy05YTg2LTQ1M2ItYTA2Ni1hZTU5ZTZiMjViN2IiLCJpYXQiOjE1MjMzNzAzMDEsInN1YiI6IkVNQUlMQUREUkVTUz1iZnVzc2VsbEBjaXNjby5jb20sIENOPUFDVlBfTkNTVV9KQVZBLCBPVT1DU0MsIE89TkNTVSwgTD1SYWxlaWdoLCBTVD1OQywgQz1VUyIsImlzcyI6Ik5JU1QgQUNWVFMiLCJleHAiOjE1MjM2Mjk1MDEsInZzSWQiOls1NDQsNTQ1LDU0Nl19.U55SgJ-wDEckLKyK65yS78m5T85K-nkSmKBwkNl2rA4" );

        final JsonArray response = httpsClient.getVectorSet( "544",
                "https://localhost:8080/acvp/validation/acvp/vectors?vsId=" );

        assertEquals( expectedResponse, response.toString() );

    }

    /**
     * Test vector set test results submission with mock server
     */
    @Test
    public void testSubmitVectorTestResults () {

        final String expectedResponse = "[{\"acv_version\":\"0.4\",\"vsId\":\"544\"}]";

        String testResults = "";

        try {
            testResults = FileUtils.readFileToString( new File( "test\\files\\vector_3637_test_results.txt" ),
                    "UTF-8" );
        } catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        validateJSON( testResults );

        final JsonParser parser = new JsonParser();
        final JsonElement element = parser.parse( testResults );
        final JsonArray testResultsJson = element.getAsJsonArray();

        // Set mock server behavior for test case
        mockServerClient
                .when( HttpRequest.request( "/acvp/validation/acvp/vectors" ).withMethod( "POST" )
                        .withQueryStringParameter( "vsId", "3637" ) )
                .respond( HttpResponse.response().withBody( expectedResponse ) );

        final Transport httpsClient = new Transport();
        httpsClient.initAuthentication( true );

        httpsClient.setAuthorizationToken(
                "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMjUxZGRmMC04ODIzLTRkNmQtODBjYS0zYjJiNGFmOTk4ODIiLCJpYXQiOjE1MjIwMjI1NjksInN1YiI6IkVNQUlMQUREUkVTUz1iZnVzc2VsbEBjaXNjby5jb20sIENOPUFDVlBfTkNTVV9KQVZBLCBPVT1DU0MsIE89TkNTVSwgTD1SYWxlaWdoLCBTVD1OQywgQz1VUyIsImlzcyI6Ik5JU1QgQUNWVFMiLCJ0ZXN0U2Vzc2lvbklkIjoiNDUwIiwiZXhwIjoxNTIyMjgxNzY5fQ.zaVl4vCEl0p402OLnwJbK0wvQrDZ5E519CWkA8RA0q8" );

        final JsonArray response = httpsClient.submitVectorTestResults( "3637", testResultsJson,
                "https://localhost:8080/acvp/validation/acvp/vectors?vsId=" );

        assertEquals( expectedResponse, response.toString() );
    }

    /**
     * Test vector set results retrieval with mock server
     */
    @Test
    public void testGetVectorSetResults () {
        String expectedResponse = "";

        try {
            expectedResponse = FileUtils.readFileToString( new File( "test\\files\\retrieve_vector_3637_results.txt" ),
                    "UTF-8" );
        } catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Set mock server behavior for test case
        mockServerClient
                .when( HttpRequest.request( "/acvp/validation/acvp/results" ).withMethod( "GET" )
                        .withQueryStringParameter( "vsId", "3637" ) )
                .respond( HttpResponse.response().withBody( expectedResponse ) );

        final Transport httpsClient = new Transport();
        httpsClient.initAuthentication( true );

        httpsClient.setAuthorizationToken(
                "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMjUxZGRmMC04ODIzLTRkNmQtODBjYS0zYjJiNGFmOTk4ODIiLCJpYXQiOjE1MjIwMjI1NjksInN1YiI6IkVNQUlMQUREUkVTUz1iZnVzc2VsbEBjaXNjby5jb20sIENOPUFDVlBfTkNTVV9KQVZBLCBPVT1DU0MsIE89TkNTVSwgTD1SYWxlaWdoLCBTVD1OQywgQz1VUyIsImlzcyI6Ik5JU1QgQUNWVFMiLCJ0ZXN0U2Vzc2lvbklkIjoiNDUwIiwiZXhwIjoxNTIyMjgxNzY5fQ.zaVl4vCEl0p402OLnwJbK0wvQrDZ5E519CWkA8RA0q8" );

        final JsonArray response = httpsClient.getVectorSetResults( "3637",
                "https://localhost:8080/acvp/validation/acvp/results?vsId=" );

        assertEquals( expectedResponse, response.toString() );
    }

    private void validateJSON ( final String s ) {
        try {
            final JsonParser parser = new JsonParser();
            parser.parse( s );
        } catch ( final JsonSyntaxException e ) {
            System.out.println( "Invalid JSON Format" );
            assert false;
        }
    }
}
