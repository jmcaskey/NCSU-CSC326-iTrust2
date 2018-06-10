package com.cisco.acvpj.kat_handler;
/**
 *
 */

//import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * JUnit test class for HashKATHandler
 *
 * All expected output is generated from the online hashing calculator found at
 * https://passwordsgenerator.net/sha256-hash-generator/ and
 * http://www.sha1-online.com/
 *
 * @author Chris Miller
 *
 */
public class HashKATHandlerTest {

    /**
     * Ensure that the exception is thrown when no algorithm is passed
     */
    @Test
    public void testHandlerNullAlgorithm() {
        HashKATHandler kat = new HashKATHandler();
        JsonObject json = new JsonObject();

        try {
            kat.handler(json);
            fail("ACVPJUnsupportedOperationException should have been thrown.");
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Ensure that the exception is thrown when a bad algorithm is passed
     */
    @Test
    public void testHandlerInvalidAlgorithm() {
        HashKATHandler kat = new HashKATHandler();
        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "invalid");
        try {
            kat.handler(json);
            fail("ACVPJUnsupportedOperationException should have been thrown.");
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Test AFT with good data
     */
    @Test
    public void testHandlerAFT() {
        HashKATHandler kat = new HashKATHandler();
        String inputString = "";
        String expectedHash = "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855";
        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "SHA-256");
        json.addProperty("vsId", 1);

        // create tests json array
        JsonArray tests = new JsonArray();
        JsonObject test = new JsonObject();
        test.addProperty("tcId", 1);
        test.addProperty("msg", inputString);
        tests.add(test);
        JsonObject testJson = new JsonObject();
        testJson.add("tests", tests);



        JsonArray testGroups = new JsonArray();
        testJson.addProperty("testType", "AFT");
        testGroups.add(testJson);
        json.add("testGroups", testGroups);

        JsonObject output = kat.handler(json);
        assertEquals(output.get("testResults").getAsJsonArray().get(0).getAsJsonObject().get("md").getAsString(), expectedHash);
    }

    /**
     * Test the MCT portion with good data
     */
    @Test
    public void testHandlerMCT() {
        HashKATHandler kat = new HashKATHandler();
        String inputString = "74C1114D0B84B5BF13FF337F3C0646125BB1A27E250F1DF37637C4CB972AFFAA0F419C26C91E540EE8F506CAB07826C57A2974B95E031710E7DB6B60939DB51A";
        String expectedHash = "0D22E7C1A560B2D82AF11827E341ACACE44159171E4A237BF9BA59F2C80F8256ED6575887A254FEE20355A843698004FA4E99DDE41E4EE71C6B2CD06E19CEE3B";

        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "SHA-512");
        json.addProperty("vsId", 1);

        // create tests json array
        JsonArray tests = new JsonArray();
        JsonObject test = new JsonObject();
        test.addProperty("tcId", 1);
        test.addProperty("msg", inputString);
        tests.add(test);
        JsonObject testJson = new JsonObject();
        testJson.add("tests", tests);



        JsonArray testGroups = new JsonArray();
        testJson.addProperty("testType", "MCT");
        testGroups.add(testJson);
        json.add("testGroups", testGroups);

        JsonObject output = kat.handler(json);
        JsonArray outputArr = output.get("testResults").getAsJsonArray().get(0).getAsJsonObject().get("resultsArray").getAsJsonArray();

        LoggerUtil.log(LogLevel.WARNING, outputArr.toString());
        assertEquals(outputArr.get(outputArr.size()-1).getAsJsonObject().get("md").getAsString(), expectedHash);
    }


}
