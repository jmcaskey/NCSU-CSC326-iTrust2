/**
 *
 */
package com.cisco.acvpj.test_case;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.util.StringUtils;

/**
 * @author Chris Miller (cjmille7)
 *
 */
public class HmacTestCase extends ACVPTestCase {

    /**
     * Hash test case constructor
     *
     * @param testCaseId
     *            test case id
     * @param testType
     *            AFT or MCT
     * @param message
     *            message to hash
     * @param algorithmId
     *            id of hashing algorithm
     */
    public HmacTestCase(int testCaseId, String testType, String message, ACVPCipher algorithmId, String key) {
        if (testType.equals("MCT")) {
            this.testType = HmacTestType.HMAC_TEST_TYPE_MCT;
        } else if (testType.equals("AFT")) {
            this.testType = HmacTestType.HMAC_TEST_TYPE_AFT;
        } else {
            throw new ACVPJUnsupportedOperationException();
        }

        this.setTestId(testCaseId);
        this.setCipher(algorithmId);
        this.setKey(StringUtils.hexToBinary(key));
        this.setMessage(StringUtils.hexToBinary(message));
    }

    @Override
    public void testCaseHandler() {
        switch (this.getCipher()) {
            case ACVP_HMAC_SHA1:
                hashMessage("HmacSHA1");
                break;
            case ACVP_HMAC_SHA2_256:
                hashMessage("HmacSHA256");
                break;
            case ACVP_HMAC_SHA2_512:
                hashMessage("HmacSHA512");
                break;
            case ACVP_SHA224:
            case ACVP_SHA384:
            default:
                throw new ACVPJUnsupportedOperationException();
        }
    }

    public enum HmacTestType {
        HMAC_TEST_TYPE_NONE, HMAC_TEST_TYPE_AFT, HMAC_TEST_TYPE_MCT;
    }

    /**
     * Hash the message using the passed method and put it into md
     */
    private void hashMessage(String method) {
        try {
            Mac hmac = Mac.getInstance(method);
            SecretKeySpec secret_key = new SecretKeySpec(getKey(), method);
            hmac.init(secret_key);
            byte[] hash = hmac.doFinal(getMessage());
            this.setMd(hash);
        } catch (NoSuchAlgorithmException e) {
            // TODO figure out what to do if failure
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private HmacTestType testType;
    private byte[] message;
    private byte[] md;
    private byte[] key;

    // Do we need getters and setters for these?
    /**
     * @return the testType
     */
    public HmacTestType getTestType() {
        return testType;
    }

    /**
     * @param testType
     *            the testType to set
     */
    public void setTestType(HmacTestType testType) {
        this.testType = testType;
    }

    /**
     * @return the message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }

    /**
     * @return the md
     */
    public byte[] getMd() {
        return md;
    }

    /**
     * @param md
     *            the md to set
     */
    public void setMd(byte[] md) {
        this.md = md;
    }

    /**
     * @return the key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(byte[] key) {
        this.key = key;
    }
}
