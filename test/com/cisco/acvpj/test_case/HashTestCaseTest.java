package com.cisco.acvpj.test_case;
/**
 *
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.HashTestCase.HashTestType;

/**
 * JUnit test class for HashTestCase
 *
 * All expected output for SHA-1 is generated from the online hashing calculator found at
 * http://cnp-wireless.com/Tools/sha1-luhn.php
 *
 * Other test data is taken from LibACVP
 *
 * @author Chris Miller
 *
 */
public class HashTestCaseTest {

    /**
     * Test sha 1 with an empty string
     */
    @Test
    public void testSha1HashingEmtpyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709";

        ACVPCipher cipher = ACVPCipher.ACVP_SHA1;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }
    }

    /**
     * Test sha 1 with a non-empty string
     */
    @Test
    public void testSha1Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "48B4FE9BBC6BC875";
        String expectedMd = "95892b20416cfad7b56d34584c35118bdf6d23a3".toUpperCase();

        ACVPCipher cipher = ACVPCipher.ACVP_SHA1;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }
    }

    /**
     * Test sha 256 with an empty string
     */
    @Test
    public void testSha256HashingEmptyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855";

        ACVPCipher cipher = ACVPCipher.ACVP_SHA256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }

    }

    /**
     * Test sha 256 with a non-empty string
     */
    @Test
    public void testSha256Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "424B237C736D6D";
        String expectedMd = "D967DD1F3BD0C2EE20B0E06DF5EAF579107CCB0BDCDFAA6B06F95D2194810A5E";

        ACVPCipher cipher = ACVPCipher.ACVP_SHA256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }

    }

    /**
     * Test sha 512 with an empty string
     */
    @Test
    public void testSha512HashingEmptyMessage() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "";
        String expectedMd = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e".toUpperCase();

        ACVPCipher cipher = ACVPCipher.ACVP_SHA512;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }

    }

    /**
     * Test sha 512 with a non-empty string
     */
    @Test
    public void testSha512Hashing() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "667146A5E50C4F";
        String expectedMd = "EA637BB852716F7DC7CA97D57039BDD1F0AB05E83DB89461257329BC88D463E298F4E8904B456CDE9AD5286533E107CB9E5D61791E35E7289BF87EDBCD427686";

        ACVPCipher cipher = ACVPCipher.ACVP_SHA512;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
        } catch (ACVPJUnsupportedOperationException e) {
            assert false;
        }

    }

    /**
     * Test invalid test type
     */
    @Test
    public void testInvalidMode() {
        int testCaseId = 1;
        String testType = "MonteCarlo";
        String message = "A6E339DC26A15D50";
        String expectedMd = "A63BB98E5B7A4A08E3ED0D7DD9243A85658A03AC6E90D50913315AC449C1D610";

        ACVPCipher cipher = ACVPCipher.ACVP_SHA256;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
            assert false;
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Test invalid cipher type
     */
    @Test
    public void testInvalidCipher() {
        int testCaseId = 1;
        String testType = "AFT";
        String message = "A6E339DC26A15D50";
        String expectedMd = "A63BB98E5B7A4A08E3ED0D7DD9243A85658A03AC6E90D50913315AC449C1D610";

        ACVPCipher cipher = ACVPCipher.ACVP_AES_CBC;
        try {
            runHashing(testCaseId, testType, message, cipher, expectedMd);
            assert false;
        } catch (ACVPJUnsupportedOperationException e) {
            assert true;
        }
    }

    /**
     * Helper function to construct the hash test case
     */
    private void runHashing(int testCaseId, String testType, String message, ACVPCipher algorithmId,
            String expectedHash) {
        HashTestCase hash = new HashTestCase(testCaseId, testType, message, algorithmId);
        hash.testCaseHandler();

        try {
            assertEquals(expectedHash, hash.getMd());
        } catch (AssertionError e) {
            System.out.println("Expected: " + expectedHash);
            System.out.println("Actual: " + hash.getMd());
            assert false;
        }
    }

    /**
     * Test getters and setters
     */
    @Test
    public void testGettersAndSetters() {
        HashTestCase tc = new HashTestCase(0, "AFT", "message", ACVPCipher.ACVP_SHA256);

        tc.setCipher(ACVPCipher.ACVP_SHA1);
        tc.setMd("messageD");
        tc.setMessage("messageMessage");
        tc.setTestId(1);
        tc.setTestType(HashTestType.HASH_TEST_TYPE_MCT);

        assertEquals(tc.getCipher(), ACVPCipher.ACVP_SHA1);
        assertEquals(tc.getMd(), "messageD");
        assertEquals(tc.getMessage(), "messageMessage");
        assertEquals(tc.getTestId(), 1);
        assertEquals(tc.getTestType(), HashTestType.HASH_TEST_TYPE_MCT);

    }

}
