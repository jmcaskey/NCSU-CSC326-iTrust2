package com.cisco.acvpj.test_case;
/**
 *
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.HmacTestCase.HmacTestType;
import com.cisco.acvpj.util.StringUtils;

/**
 * JUnit test class for HashTestCase
 *
 * All expected output is generated from the online hashing calculator found at
 * https://www.liavaag.org/English/SHA-Generator/HMAC/
 *
 * @author Chris Miller
 *
 */
public class HmacTestCaseTest {

    /**
     * Test HMAC-sha 1 with an empty string
     */
    @Test
    public void testHmacSha1HashingEmtpyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "4d992f518b98c713b4ad34af6e85c862bef47ce9";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA1;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }
    }

    /**
     * Test sha 1 with a non-empty string
     */
    @Test
    public void testHmacSha1Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "48B4FE9BBC6BC875";
        String expectedMd = "7990c13feb61f3a2e3550bbe6c4a7cb83642f6f5";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA1;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }
    }

    /**
     * Test sha 256 with an empty string
     */
    @Test
    public void testHmacSha256HashingEmptyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "f2f24bb00417d3d905c2fc9b659fbe5310af55be93eb00524fc2021e3cc29a88";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA2_256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }

    }

    /**
     * Test sha 256 with a non-empty string
     */
    @Test
    public void testHmacSha256Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "A6E339DC26A15D50";
        String expectedMd = "41911f8bfdc62801cc173a9108af1dffdbfa8917a82f3bdecfc8f64a414db342";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA2_256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }

    }

    /**
     * Test sha 512 with an empty string
     */
    @Test
    public void testHmacSha512HashingEmptyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "acae8450151bdbb810f41200da1bf26ef2756037bcdc930b014cbbc5fccb3b9ddc0cdcee6b05fa07d88e65af87d202e6dd8d0c5303a8a0866a2a5ce505779808";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA2_512;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }

    }

    /**
     * Test sha 512 with a non-empty string
     */
    @Test
    public void testHmacSha512Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "A6E339DC26A15D50";
        String expectedMd = "2f09e531c9b3e84d337ce9fe6061c124c65c3cb221a21c5a108c0e307fd0f0c4111eec68952d1b30b7021518a4ad635023a3fa39dee60334e4b99e94818a9d41";
        String key = "0123456789ABCDEF";

        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA2_512;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
        } catch (ACVPJUnsupportedOperationException e) {
            Assert.fail();
        }

    }

    /**
     * Test invalid test type
     */
    @Test
    public void testHmacInvalidMode() {
        int testCaseId = 1;
        String testType = "MonteCarlo";
        String message = "A6E339DC26A15D50";
        String expectedMd = "A63BB98E5B7A4A08E3ED0D7DD9243A85658A03AC6E90D50913315AC449C1D610";
        String key = "0123456789ABCDEF";
        ACVPCipher cipher = ACVPCipher.ACVP_HMAC_SHA2_256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
            Assert.fail();
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Test invalid cipher type
     */
    @Test
    public void testHmacInvalidCipher() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "A6E339DC26A15D50";
        String expectedMd = "A63BB98E5B7A4A08E3ED0D7DD9243A85658A03AC6E90D50913315AC449C1D610";
        String key = "0123456789ABCDEF";

        ACVPCipher cipher = ACVPCipher.ACVP_AES_CBC;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd, key);
            Assert.fail();
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Helper function to construct the hash test case
     */
    private void runHashing(int testCaseId, String testType, String message, ACVPCipher algorithmId,
            String expectedHash, String key) {
        HmacTestCase hash = new HmacTestCase(testCaseId, testType, message, algorithmId, key);
        hash.testCaseHandler();

        System.out.println("Expected: " + expectedHash);
        System.out.println("Actual: " + StringUtils.byteToString(hash.getMd()));
        assertEquals(expectedHash.toUpperCase(), StringUtils.byteToString(hash.getMd()));
    }

    /**
     * Test getters and setters
     */
    @Test
    public void testGettersAndSetters() {
        HmacTestCase tc = new HmacTestCase(0, "AFT", "0123456789ABCDEF", ACVPCipher.ACVP_SHA256, "0123456789ABCDEF");

        tc.setCipher(ACVPCipher.ACVP_SHA1);
        tc.setMd(StringUtils.hexToBinary("0123456789ABCDEF"));
        tc.setMessage(StringUtils.hexToBinary("0123456789ABCDEF"));
        tc.setTestId(1);
        tc.setTestType(HmacTestType.HMAC_TEST_TYPE_MCT);
        tc.setKey(StringUtils.hexToBinary("0123456789ABCDEF"));

        assertEquals(tc.getCipher(), ACVPCipher.ACVP_SHA1);
        assertArrayEquals(tc.getMd(), StringUtils.hexToBinary("0123456789ABCDEF"));
        assertArrayEquals(tc.getMessage(), StringUtils.hexToBinary("0123456789ABCDEF"));
        assertEquals(tc.getTestId(), 1);
        assertEquals(tc.getTestType(), HmacTestType.HMAC_TEST_TYPE_MCT);
        assertArrayEquals(tc.getKey(), StringUtils.hexToBinary("0123456789ABCDEF"));

    }

}
