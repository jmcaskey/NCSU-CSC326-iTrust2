package com.cisco.acvpj.test_case;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherDirection;

/**
 * JUnit test class for SymCipherTestCase. This class tests all major flows for
 * AES encryption.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public class SymCipherTestCaseTest {

    /**
     * Tests a basic AES encryption and decryption; 128 bit key
     */
    @Test
    void testAESCBCEncryption1() {
        int testCaseId = 1;
        String testType = "AFT";
        String jKey = "1ABD1B47A5D4CF9B4F80DFBB22A50114";
        String jPt = "8CE1AE17F02E6D39140171B9655C4046";
        String jCt = null;
        String jIv = "48C8B3027463B40373CF8B3E5C1B596F";
        String jTag = null;
        String jAad = null;
        char[] kwcipher = null;
        ACVPCipher algId = ACVPCipher.ACVP_AES_CBC;
        SymCipherDirection dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;

        runEncryption(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad, kwcipher, algId, dir);
    }

    /**
     * Tests a basic AES encryption and decryption; 128 bit key, longer
     * plaintext
     */
    @Test
    void testAESCBCEncryption2() {
        int testCaseId = 1;
        String testType = "AFT";
        String jKey = "1ABD1B47A5D4CF9B4F80DFBB22A50114";
        String jPt = "8CE1AE17F02E6D39140171B9655C40468CE1AE17F02E6D39140171B9655C4046";
        String jCt = null;
        String jIv = "4020C0D1A6942F6946B90454EE540151";
        String jTag = null;
        String jAad = null;
        char[] kwcipher = null;
        ACVPCipher algId = ACVPCipher.ACVP_AES_CBC;
        SymCipherDirection dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;

        runEncryption(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad, kwcipher, algId, dir);
    }

    /**
     * Tests a basic AES encryption and decryption; 128 bit key, empty plaintext
     */
    @Test
    void testAESCBCEncryption3() {
        int testCaseId = 1;
        String testType = "AFT";
        String jKey = "1ABD1B47A5D4CF9B4F80DFBB22A50114";
        String jPt = "";
        String jCt = null;
        String jIv = "4020C0D1A6942F6946B90454EE540151";
        String jTag = null;
        String jAad = null;
        char[] kwcipher = null;
        ACVPCipher algId = ACVPCipher.ACVP_AES_CBC;
        SymCipherDirection dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;

        runEncryption(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad, kwcipher, algId, dir);
    }

    /**
     * Tests invalid key length
     */
    @Test
    void testInvalidKeyLengthAESCBC() {
        int testCaseId = 1;
        String testType = "AFT";
        String jKey = "1ABD1B47A5D4CF";
        String jPt = "8CE1AE17F02E6D39140171B9655C4046";
        String jCt = null;
        String jIv = "48C8B3027463B40373CF8B3E5C1B596F";
        String jTag = null;
        String jAad = null;
        char[] kwcipher = null;
        ACVPCipher algId = ACVPCipher.ACVP_AES_CBC;
        SymCipherDirection dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;

        try {
            runEncryption(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad, kwcipher, algId, dir);
        } catch (Exception e) {
            assertEquals(ACVPJUnsupportedOperationException.class, e.getClass());
        }
    }

    /**
     * Tests invalid mode
     */
    @Test
    void testInvalidAESMode() {
        int testCaseId = 1;
        String testType = "AFT";
        String jKey = "1ABD1B47A5D4CF";
        String jPt = "8CE1AE17F02E6D39140171B9655C4046";
        String jCt = null;
        String jIv = "48C8B3027463B40373CF8B3E5C1B596F";
        String jTag = null;
        String jAad = null;
        char[] kwcipher = null;
        ACVPCipher algId = ACVPCipher.ACVP_AES_KWP;
        SymCipherDirection dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;

        try {
            runEncryption(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad, kwcipher, algId, dir);
        } catch (Exception e) {
            assertEquals(ACVPJUnsupportedOperationException.class, e.getClass());
        }
    }

    /**
     * Helper function that takes in all SymCipherTestCase constructor params to
     * run encryption and decryption with SymCipherTestCase.
     */
    private void runEncryption(int testCaseId, String testType, String jKey, String jPt, String jCt, String jIv,
            String jTag, String jAad, char[] kwcipher, ACVPCipher algId, SymCipherDirection dir) {
        SymCipherTestCase encrypt = new SymCipherTestCase(testCaseId, testType, jKey, jPt, jCt, jIv, jTag, jAad,
                kwcipher, algId, dir);

        encrypt.testCaseHandler();

        SymCipherTestCase decrypt = new SymCipherTestCase(testCaseId + 1, testType, jKey, null,
                DatatypeConverter.printHexBinary(encrypt.getCipherText()), jIv, jTag, jAad, kwcipher, algId,
                SymCipherDirection.SYM_DIRECTION_DECRYPT);

        decrypt.testCaseHandler();

        assertEquals(encrypt.getPlainText().length, decrypt.getPlainText().length);
        assertEquals(jPt, DatatypeConverter.printHexBinary(decrypt.getPlainText()));
    }

}
