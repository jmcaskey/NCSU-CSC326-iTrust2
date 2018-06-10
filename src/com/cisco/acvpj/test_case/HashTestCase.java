/**
 *
 */
package com.cisco.acvpj.test_case;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.util.StringUtils;


/**
 * @author Chris Miller (cjmille7)
 *
 */
public class HashTestCase extends ACVPTestCase {

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
    public HashTestCase(int testCaseId, String testType, String message, ACVPCipher algorithmId) {
        if (testType.equals("MCT")) {
            this.testType = HashTestType.HASH_TEST_TYPE_MCT;
        } else if (testType.equals("AFT")) {
            this.testType = HashTestType.HASH_TEST_TYPE_AFT;
        } else {
            throw new ACVPJUnsupportedOperationException();
        }

        setTestId(testCaseId);
        setCipher(algorithmId);
        this.message = message;
    }

    @Override
    public void testCaseHandler() {
        switch (this.getCipher()) {
            case ACVP_SHA1:
                hashMessage("SHA-1");
                break;
            case ACVP_SHA256:
                hashMessage("SHA-256");
                break;
            case ACVP_SHA512:
                hashMessage("SHA-512");
                break;
            case ACVP_SHA224:
            case ACVP_SHA384:
            default:
                throw new ACVPJUnsupportedOperationException();
        }
    }

    public enum HashTestType {
        HASH_TEST_TYPE_NONE, HASH_TEST_TYPE_AFT, HASH_TEST_TYPE_MCT;
    }

    /**
     * Hash the message using the passed method and put it into md
     */
    private void hashMessage(String method) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(method);
            byte[] hash = digest.digest(StringUtils.hexToBinary(getMessage()));
            this.setMd(StringUtils.byteToString(hash));
        } catch (NoSuchAlgorithmException e) {
            // TODO figure out what to do if failure
            e.printStackTrace();
        }
    }

    private HashTestType testType;
    private String message;
    private String md;

    // Do we need getters and setters for these?
    /**
     * @return the testType
     */
    public HashTestType getTestType() {
        return testType;
    }

    /**
     * @param testType
     *            the testType to set
     */
    public void setTestType(HashTestType testType) {
        this.testType = testType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the md
     */
    public String getMd() {
        return md;
    }

    /**
     * @param md
     *            the md to set
     */
    public void setMd(String md) {
        this.md = md;
    }
}
