package com.cisco.acvpj.kat_handler;

import javax.xml.bind.DatatypeConverter;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJCryptoModuleException;
import com.cisco.acvpj.exceptions.ACVPJCryptoTagException;
import com.cisco.acvpj.exceptions.ACVPJCryptoWrapException;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.SymCipherTestCase;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherDirection;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherTestType;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.cisco.acvpj.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * KATHandler for the AES Encryption algorithm. Currently supports AES in CBC mode, no Monte Carlo tests.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public class AESKATHandler implements KATHandler {
    /**
     * Sets up and runs the SymCipherTestCase for AES.
     * @param request input JsonObject specifying details of the test
     * @return JsonObject with the output results and details of the test
     */
    @Override
    public JsonObject handler(JsonObject request) {
        SymCipherTestCase testCase;

        // Handle an empty JSON
        if (request.size() == 0) {
            LoggerUtil.log(LogLevel.ERROR, " Json request was null.");
            throw new ACVPJUnsupportedOperationException();
        }

        // Handle JSON missing key information
        if (request.get("vsId") == null || request.get("algorithm") == null) {
            LoggerUtil.log(LogLevel.ERROR, " Unable to parse 'algorithm' from JSON");
            throw new ACVPJUnsupportedOperationException();
        }

        // Get the basic information about this test
        int vsId = request.get("vsId").getAsInt();
        String algString = request.get("algorithm").getAsString();

        // We know the test should be AES, but this will also tell us which type
        ACVPCipher algId = ACVPCipher.lookUpCipher(algString);

        // The only supported mode currently is AES-CBC; revisit this once other modes are added
        if (algId != ACVPCipher.ACVP_AES_CBC) {
            LoggerUtil.log(LogLevel.ERROR, String.format(" Unsupported algorithm (%s)", algString));
            throw new ACVPJUnsupportedOperationException();
        }

        // Begin forming the response JSON; each will have the vsId and algorithm at the top
        JsonObject response = new JsonObject();
        response.addProperty("vsId", vsId);
        response.addProperty("algorithm", algString);

        // The request JSON is first broken down into testGroups, which are JsonArrays of tests
        JsonArray groups = request.getAsJsonArray("testGroups");
        JsonArray returnArray = new JsonArray();

        // Iterate through all of the groups
        for (int i = 0; i < groups.size(); i++) {
            JsonObject groupObj = groups.get(i).getAsJsonObject();
            String dirStr = groupObj.get("direction").getAsString();

            // Convert the String to one of our test enums
            SymCipherDirection dir = SymCipherDirection.lookUpDirection(dirStr);

            // Only used by some modes of AES
            String kwCipher = null;
            if ((algId == ACVPCipher.ACVP_AES_KW) || (algId == ACVPCipher.ACVP_AES_KWP)
                    || (algId == ACVPCipher.ACVP_TDES_KW)) {
                kwCipher = groupObj.get("kwCipher").getAsString();
                if (kwCipher == null) {
                    LoggerUtil.log(LogLevel.ERROR, String.format("kwCipher field not present in request JSON"));
                    throw new ACVPJUnsupportedOperationException();
                }
            }

            // Get whether the test is an AFT test or MCT
            String testType = groupObj.get("testType").getAsString();

            LoggerUtil.log(LogLevel.INFO, String.format("    Test group: %d", i));
            LoggerUtil.log(LogLevel.INFO, String.format("           dir: %s", dirStr));
            LoggerUtil.log(LogLevel.INFO, String.format("            kw: %s", kwCipher));
            LoggerUtil.log(LogLevel.INFO, String.format("      testtype: %s", testType));

            JsonArray tests = groupObj.getAsJsonArray("tests");

            // Loop through all of the tests
            for (int j = 0; j < tests.size(); j++) {
                LoggerUtil.log(LogLevel.INFO, "Found new AES test vector...");
                JsonElement testVal = tests.get(j);
                JsonObject testObj = testVal.getAsJsonObject();

                // Get tcId and key from the JSON
                int testId = testObj.get("tcId").getAsInt();
                String key = testObj.get("key").getAsString();

                String plaintext = "";
                String iv = "";
                String ciphertext = "";
                String tag = "";
                String aad = "";

                // Get the input for the test here
                if (dir == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                    plaintext = testObj.get("pt").getAsString();
                    if (plaintext == null) {
                        plaintext = testObj.get("pt").getAsString();
                    }

                    // XTS may call it tweak value "i", but we treat is as an IV
                    if (algId == ACVPCipher.ACVP_AES_XTS) {
                        iv = testObj.get("i").getAsString();
                    } else {
                        iv = testObj.get("iv").getAsString();
                    }
                } else {
                    ciphertext = testObj.get("ct").getAsString();
                    if (ciphertext == null) {
                        ciphertext = testObj.get("ct").getAsString();
                    }

                    // XTS may call it tweak value "i", but we treat is as an IV
                    if (algId == ACVPCipher.ACVP_AES_XTS) {
                        iv = testObj.get("i").getAsString();
                    } else {
                        iv = testObj.get("iv").getAsString();
                    }
                    if (testObj.get("tag") != null) {
                        tag = testObj.get("tag").getAsString();
                    }
                }

                // Not all modes have this value
                if (testObj.get("aad") != null) {
                    aad = testObj.get("aad").getAsString();
                }

                LoggerUtil.log(LogLevel.INFO, String.format("        Test case: %d", j));
                LoggerUtil.log(LogLevel.INFO, String.format("             tcId: %d", testId));
                LoggerUtil.log(LogLevel.INFO, String.format("              key: %s", key));
                LoggerUtil.log(LogLevel.INFO, String.format("               pt: %s", plaintext));
                LoggerUtil.log(LogLevel.INFO, String.format("               ct: %s", ciphertext));
                LoggerUtil.log(LogLevel.INFO, String.format("               iv: %s", iv));
                LoggerUtil.log(LogLevel.INFO, String.format("              tag: %s", tag));
                LoggerUtil.log(LogLevel.INFO, String.format("              aad: %s", aad));

                // Setup JSON for the test that will ultimately be add to the response array
                JsonObject testCaseResponse = new JsonObject();
                testCaseResponse.addProperty("tcId", testId);

                // Initialize the test case with the input data from the JSON
                testCase = new SymCipherTestCase(testId, testType, key, plaintext, ciphertext,
                        iv, tag, aad, kwCipher != null ? kwCipher.toCharArray() : null, algId, dir);

                // If the test is an MCT test, then we will use the MCT handler
                if (testCase.getTestType() == SymCipherTestType.SYM_TEST_TYPE_MCT) {
                    testCaseResponse.add("resultsArray", testCase.mctHandler());
                } else {
                    // Catch exceptions thrown by the SymCipherTestCase that need to be noted in the JSON response
                    try {
                        testCase.testCaseHandler();
                    } catch (ACVPJCryptoTagException e) {
                        if ((testCase.getCipher() == ACVPCipher.ACVP_AES_GCM ||
                                testCase.getCipher() == ACVPCipher.ACVP_AES_CCM)) {
                            testCaseResponse.addProperty("decryptFail", true);
                        }
                    } catch (ACVPJCryptoWrapException e) {
                        if ((testCase.getCipher() == ACVPCipher.ACVP_AES_KW ||
                                testCase.getCipher() == ACVPCipher.ACVP_AES_KWP)) {
                            testCaseResponse.addProperty("decryptFail", true);
                        }
                    } catch (ACVPJCryptoModuleException e) {
                        LoggerUtil.log(LogLevel.ERROR, "crypto module failed the operation");
                        throw e;
                    }

                    // TODO: outputTestCase could probably be moved to SymCipherTestCase; would be more in line with OO design
                    testCaseResponse = outputTestCase(testCase, testCaseResponse);
                }

                // Add the response for the individual test to the JsonArray
                returnArray.add(testCaseResponse);
            }
        }

        // Finally, add the JsonArray to the JsonObject response
        response.add("testResults", returnArray);

        return response;
    }

    /**
     * Takes test output from the SymCipherTestCase and details from the JsonObject and returns it in another JsonObject.
     * @param testCase SymCipherTestCase from which to get the output
     * @param json JsonObject on which to base the return value from this method
     * @return JsonObject with test output
     *
     * Note: this should probably be a part of SymCipherTestCase rather than AESKATHandler
     */
    private JsonObject outputTestCase(SymCipherTestCase testCase, JsonObject json) {
        byte[] temp;

        if (testCase.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
            if (testCase.getCipher() == ACVPCipher.ACVP_AES_GCM) {
                temp = StringUtils.hexToBinary(testCase.getIvRet());
                json.addProperty("iv", DatatypeConverter.printHexBinary(temp));
            }

            String tempStr = "";
            if (testCase.getCipher() == ACVPCipher.ACVP_AES_CFB1) {
                tempStr = StringUtils.binaryToBit(DatatypeConverter.printHexBinary(testCase.getCipherText()));
                json.addProperty("ct", tempStr);
            } else {
                temp = testCase.getCipherText();
                json.addProperty("ct", DatatypeConverter.printHexBinary(temp));
            }

            if (testCase.getCipher() == ACVPCipher.ACVP_AES_GCM) {
                temp = StringUtils.hexToBinary(DatatypeConverter.printHexBinary(testCase.getTag()));
                json.addProperty("tag", DatatypeConverter.printHexBinary(temp));
            }
        } else {
            String tempStr = "";
            if (testCase.getCipher() == ACVPCipher.ACVP_AES_CFB1) {
                tempStr = StringUtils.binaryToBit(DatatypeConverter.printHexBinary(testCase.getPlainText()));
                json.addProperty("pt", tempStr);
            } else {
                json.addProperty("pt", DatatypeConverter.printHexBinary(testCase.getPlainText()));
            }
        }

        return json;
    }
}
