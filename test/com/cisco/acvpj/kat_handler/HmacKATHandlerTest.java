package com.cisco.acvpj.kat_handler;
/**
 *
 */

//import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * JUnit test class for HmacKatHandler
 *
 * @author Chris Miller
 *
 */
public class HmacKATHandlerTest {

    /**
     * Ensure that the exception is thrown when no algorithm is passed
     */
    @Test
    public void testHandlerNullAlgorithm() {
        HmacKATHandler kat = new HmacKATHandler();
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
        HmacKATHandler kat = new HmacKATHandler();
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
     * Test AFT with good data and hmac sha 512
     */
    @Test
    public void testHandlerAFT512() {
        HmacKATHandler kat = new HmacKATHandler();
        String inputString = "";
        String expectedHash = "acae8450151bdbb810f41200da1bf26ef2756037bcdc930b014cbbc5fccb3b9ddc0cdcee6b05fa07d88e65af87d202e6dd8d0c5303a8a0866a2a5ce505779808".toUpperCase();
        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "HMAC-SHA2-512");
        json.addProperty("vsId", 1);

        // create tests json array
        JsonArray tests = new JsonArray();
        JsonObject test = new JsonObject();
        test.addProperty("tcId", 1);
        test.addProperty("msg", inputString);
        test.addProperty("key", "0123456789ABCDEF");
        tests.add(test);
        JsonObject testJson = new JsonObject();
        testJson.add("tests", tests);



        JsonArray testGroups = new JsonArray();
        testJson.addProperty("testType", "AFT");
        testGroups.add(testJson);
        json.add("testGroups", testGroups);

        JsonObject output = kat.handler(json);
        assertEquals(output.get("testResults").getAsJsonArray().get(0).getAsJsonObject().get("mac").getAsString(), expectedHash);
    }

    /**
     * Test AFT with good data and hmac sha 1
     */
    @Test
    public void testHandlerAFT1() {
        HmacKATHandler kat = new HmacKATHandler();
        String inputString = "48B4FE9BBC6BC875";
        String expectedHash = "7990c13feb61f3a2e3550bbe6c4a7cb83642f6f5";
        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "HMAC-SHA-1");
        json.addProperty("vsId", 1);

        // create tests json array
        JsonArray tests = new JsonArray();
        JsonObject test = new JsonObject();
        test.addProperty("tcId", 1);
        test.addProperty("msg", inputString);
        test.addProperty("key", "0123456789ABCDEF");
        tests.add(test);
        JsonObject testJson = new JsonObject();
        testJson.add("tests", tests);

        JsonArray testGroups = new JsonArray();
        testJson.addProperty("testType", "AFT");
        testGroups.add(testJson);
        json.add("testGroups", testGroups);

        JsonObject output = kat.handler(json);
        assertEquals(output.get("testResults").getAsJsonArray().get(0).getAsJsonObject().get("mac").getAsString(), expectedHash.toUpperCase());
    }

    /**
     * Test AFT with good data and hmac sha 256
     */
    @Test
    public void testHandlerAFT256() {
        HmacKATHandler kat = new HmacKATHandler();
        String inputString = "A6E339DC26A15D50";
        String expectedHash = "41911f8bfdc62801cc173a9108af1dffdbfa8917a82f3bdecfc8f64a414db342".toUpperCase();
        JsonObject json = new JsonObject();
        json.addProperty("algorithm", "HMAC-SHA2-256");
        json.addProperty("vsId", 1);

        // create tests json array
        JsonArray tests = new JsonArray();
        JsonObject test = new JsonObject();
        test.addProperty("tcId", 1);
        test.addProperty("msg", inputString);
        test.addProperty("key", "0123456789ABCDEF");
        tests.add(test);
        JsonObject testJson = new JsonObject();
        testJson.add("tests", tests);



        JsonArray testGroups = new JsonArray();
        testJson.addProperty("testType", "AFT");
        testGroups.add(testJson);
        json.add("testGroups", testGroups);

        JsonObject output = kat.handler(json);
        assertEquals(output.get("testResults").getAsJsonArray().get(0).getAsJsonObject().get("mac").getAsString(), expectedHash);
    }


}
