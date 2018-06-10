package com.cisco.acvpj.kat_handler;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.HashTestCase;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This is the KATHandler for the tested hashing algorithms.
 *
 * @author Chris Miller (cjmille7)
 */
public class HashKATHandler implements KATHandler {

    // variables to be used for monte carlo tests
    private static final int ACVP_HASH_MCT_OUTER = 100;
    private static final int ACVP_HASH_MCT_INNER = 1000;

    /**
     * Set up and run the HashTestCase 
     * @param request input JsonObject specifying details of the test
     * @return JsonObject with the output results and details of the test
     */
    @Override
    public JsonObject handler(JsonObject json) {
        String algorithm;
        try {
            algorithm = json.get("algorithm").getAsString();
        } catch (NullPointerException e) {
            LoggerUtil.log(LogLevel.ERROR, "unable to parse 'algorithm' from JSON");
            throw new ACVPJUnsupportedOperationException();
        }

        ACVPCipher algorithmId = ACVPCipher.lookUpCipher(algorithm);
        if (algorithmId == ACVPCipher.ACVP_CIPHER_NONE) {
            LoggerUtil.log(LogLevel.ERROR, String.format("Unsupported Algorithm: (%s)", algorithm));
            throw new ACVPJUnsupportedOperationException();
        }

        // Start to build the JSON response
        JsonObject regObj = new JsonObject();

        // TODO figure out how to get the vector set id
        regObj.addProperty("vsId", json.get("vsId").getAsNumber());
        regObj.addProperty("algorithm", algorithm);
        regObj.add("testResults", new JsonArray());

        JsonArray testResultsArray = regObj.get("testResults").getAsJsonArray();
        JsonArray groups = json.get("testGroups").getAsJsonArray();

        for (int i = 0; i < groups.size(); i++) {
            JsonElement groupVal = groups.get(i);
            JsonObject groupObj = groupVal.getAsJsonObject();

            LoggerUtil.log(String.format("    Test Group: %d", i));

            String testType = groupObj.get("testType").getAsString();
            JsonArray tests = groupObj.get("tests").getAsJsonArray();

            for (int j = 0; j < tests.size(); j++) {
                LoggerUtil.log("Found new hash test vector...");
                JsonElement testVal = tests.get(j);
                JsonObject testObj = testVal.getAsJsonObject();

                int testCaseId = testObj.get("tcId").getAsInt();
                String message = testObj.get("msg").getAsString();

                LoggerUtil.log(String.format("        Test case: %d", j));
                LoggerUtil.log(String.format("             tcId: %d", testCaseId));
                LoggerUtil.log(String.format("              msg: %s", message));
                LoggerUtil.log(String.format("         testtype: %s", testType));

                // Create new test case in the response
                JsonObject testResponse = new JsonObject();
                testResponse.addProperty("tcId", testCaseId);

                /*
                 * Setup the test case data that will be passed down to the crypto module.
                 */
                HashTestCase hash = new HashTestCase(testCaseId, testType, message, algorithmId);

                // If Monte Carlo, start that here
                if (hash.getTestType() == HashTestCase.HashTestType.HASH_TEST_TYPE_MCT) {
                    JsonArray resultsArray = acvpHashTestCaseMCT(hash);
                    testResponse.add("resultsArray", resultsArray);
                } else {
                    hash.testCaseHandler();
                    testResponse.addProperty("md", hash.getMd());
                }

                testResultsArray.add(testResponse);
            }
        }

        return regObj;
    }

    /**
     * Handle the monte carlo tests for HashTestCase
     * @param hash
     * @return
     */
    private JsonArray acvpHashTestCaseMCT(HashTestCase hash) {
        JsonArray resultsArray = new JsonArray();
        String m1 = hash.getMessage();
        String m2 = m1;
        String m3 = m1;
        for (int i = 0; i < ACVP_HASH_MCT_OUTER; i++ ) {
            JsonObject response = new JsonObject();
            response.addProperty("msg", m1+m2+m3);
            hash.setMessage(m1+m2+m3);
            for (int j = 0; j < ACVP_HASH_MCT_INNER; j++) {
                hash.testCaseHandler();
                m1=m2;
                m2=m3;
                m3=hash.getMd();
                hash.setMessage(m1+m2+m3);
            }
            response.addProperty("md", hash.getMd());
            m1 = m3;
            m2 = m3;
            resultsArray.add(response);
        }

        return resultsArray;

    }

}
