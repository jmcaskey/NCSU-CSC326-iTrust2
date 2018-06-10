/**
 *
 */
package com.cisco.acvpj.test_case;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherDirection;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherIVGenMode;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherIVGenSource;
import com.cisco.acvpj.test_case.enumeration.SymCipherTestEnums.SymCipherTestType;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.cisco.acvpj.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The SymCipherTestCase is initialized by AESKATHandler to run all tests dealing with AES encryption.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public class SymCipherTestCase extends ACVPTestCase {

    private static final int AES_MCT_OUTER_LOOP = 100;
    private static final int AES_MCT_INNER_LOOP = 1000;

    private SymCipherTestType testType;

    private SymCipherDirection direction;

    private SymCipherIVGenSource ivGenSource;

    private SymCipherIVGenMode ivGenMode;

    private byte[] key;

    private byte[] plainText;

    private byte[] additionalAuthenticatedData;

    private byte[] initializationVector;

    private byte[] cipherText;

    private byte[] tag;

    private String ivRet;

    private String ivRetAfter;

    private int kwCipher;

    private int mctIndex;

    /**
     * SymCipherTestCase constructor; all parameters are inputs for test cases.
     * Parameters only required for specific modes should be set to null for all
     * other modes that do not make use of that field. All parameters should be
     * passed in as a hexadecimal-formatted String that can be converted to a byte[].
     *
     * @param testCaseId id of the test within the vector set
     * @param testType Algorithm Functional Test or Monte Carlo Test
     * @param key input key for encryption/decryption
     * @param plainText input plaintext for encryption or output for decryption
     * @param cipherText input ciphertext for decryption or output for encryption
     * @param iv initialization vector for all AES modes except ECB
     * @param tag input used by some AES modes
     * @param aad input used by some AES modes
     * @param kwcipher input used by some AES modes
     * @param algId specifies the AES mode
     * @param dir encryption or decryption test
     */
    public SymCipherTestCase(int testCaseId, String testType, String key, String plainText, String cipherText,
            String iv, String tag, String aad, char[] kwcipher, ACVPCipher algId, SymCipherTestEnums.SymCipherDirection dir) {

        // Convert the testType String to an enum that we can use
        this.setTestType(SymCipherTestType.lookUpTestType(testType));

        // All AES modes will have a byte[] key passed in as a hexadecimal-formatted String
        this.setKey(StringUtils.hexToBinary(key));

        // All AES modes will have these fields as well
        this.setCipher(algId);
        this.setDirection(dir);

        // Plaintext will be null as input for decryption tests
        if (plainText != null) {
            if (algId == ACVPCipher.ACVP_AES_CFB1) {
                this.setPlainText(StringUtils.bitToBinary(plainText));
            } else {
                this.setPlainText(StringUtils.hexToBinary(plainText));
            }
        }

        // Ciphertext will be null as input for encryption tests
        if (cipherText != null) {
            if (algId == ACVPCipher.ACVP_AES_CFB1) {
                this.setCipherText(StringUtils.bitToBinary(cipherText));
            } else {
                this.setCipherText(StringUtils.hexToBinary(cipherText));
            }
        }

        // Initialization vector can be null as input for AES in ECB mode
        if (iv != null) {
            this.setInitializationVector(StringUtils.hexToBinary(iv));
        }

        // Only applicable for certain AES modes
        if (tag != null) {
            this.setTag(StringUtils.hexToBinary(tag));
        }

        // Only applicable for certain AES modes
        if (aad != null) {
            this.setAdditionalAuthenticatedData(StringUtils.hexToBinary(aad));
        }
    }

    /**
     * Hands off the encryption to the correct handler.
     */
    @Override
    public void testCaseHandler() {
        switch (this.getCipher()) {
            case ACVP_AES_CBC:
                runAESCBC(); break;
            case ACVP_AES_GCM:
            case ACVP_AES_CCM:
            case ACVP_AES_ECB:
            case ACVP_AES_CFB1:
            case ACVP_AES_CFB8:
            case ACVP_AES_CFB128:
            case ACVP_AES_OFB:
            case ACVP_AES_CTR:
            case ACVP_AES_XTS:
            case ACVP_AES_KW:
            case ACVP_AES_KWP:
            default:
                LoggerUtil.log(LogLevel.ERROR, "Server requested unsupported AES mode.");
                throw new ACVPJUnsupportedOperationException();
        }
    }

    /**
     * Handler for MCT tests
     * @return a JsonArray with the results from the MCT tests
     *
     * TODO: refactor MCT capabilities to its own class.
     */
    public JsonArray mctHandler() {
        switch (this.getCipher()) {
            case ACVP_AES_CBC:
                return runAESCBCMCT();
            default:
                break;
        }
        return null;
    }

    /**
     * Runs AES-CBC encryption on the plaintext or decrypts the ciphertext.
     */
    private void runAESCBC() {
        // Most of these JCA functions throw exceptions; this requires a single try/catch for readability.
        try {
            // Get the objects needed to setup the encryption/decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            Key aesKey = new SecretKeySpec(this.getKey(), "AES");
            AlgorithmParameters aesParms = AlgorithmParameters.getInstance("AES");
            aesParms.init(new IvParameterSpec(this.getInitializationVector()));

            // Encrypt or decrypt depending on which value is set
            switch (this.getDirection()) {
                case SYM_DIRECTION_ENCRYPT:
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey, aesParms);
                    this.setCipherText(cipher.doFinal(this.getPlainText()));
                    break;
                case SYM_DIRECTION_DECRYPT:
                    cipher.init(Cipher.DECRYPT_MODE, aesKey, aesParms);
                    this.setPlainText(cipher.doFinal(this.getCipherText()));
                    break;
                default:
                    LoggerUtil.log(LogLevel.ERROR, "Server requested unsupported AES direction.");
                    throw new ACVPJUnsupportedOperationException();
            }

            // For now, handle all exceptions by logging the error and throwing our own exception
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException |
                BadPaddingException e) {
            LoggerUtil.log(LogLevel.ERROR, e.getMessage());
            throw new ACVPJUnsupportedOperationException();
        }
    }

    /**
     * Runs a Monte Carlo test on the SymCipherTestCase
     * @return a JsonArray with the results of the Monte Carlo test
     *
     * TODO: refactor this to its own class
     * TODO: refactor into a encryption version and decryption version
     */
    private JsonArray runAESCBCMCT() {
        // Setup local variables for iterating over the test case
        byte[] key = Arrays.copyOf(this.getKey(), this.getKey().length);
        byte[] pt = null;
        if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
            pt = Arrays.copyOf(this.getPlainText(), this.getPlainText().length);
        }
        byte[] iv = Arrays.copyOf(this.getInitializationVector(), this.getInitializationVector().length);
        byte[] ct = null;
        if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_DECRYPT) {
            ct = Arrays.copyOf(this.getCipherText(), this.getCipherText().length);
        }

        // Initialize reponse array where all test iterations will be added
        JsonArray response = new JsonArray();

        try {
            // Setup an outer loop of 100; at the end of this block, the results will be added to the array
            for (int i = 0; i < AES_MCT_OUTER_LOOP; i++) {
                // Start creating the response object for this test case
                JsonObject testCaseResponse = new JsonObject();
                testCaseResponse.addProperty("key", StringUtils.byteToString(key));
                testCaseResponse.addProperty("iv", StringUtils.byteToString(iv));
                if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                    testCaseResponse.addProperty("pt", StringUtils.byteToString(pt));
                } else {
                    testCaseResponse.addProperty("ct", StringUtils.byteToString(ct));
                }

                // Initialize the Cipher and parameters for this iteration
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                Key aesKey = new SecretKeySpec(key, "AES");
                AlgorithmParameters aesParms = AlgorithmParameters.getInstance("AES");
                aesParms.init(new IvParameterSpec(iv));

                // For each iteration of the MCT test, we will encrypt/decrypt 1000 times
                for (int j = 0; j < AES_MCT_INNER_LOOP; j++) {
                    // During the first iteration, we have to setup the Cipher using Cipher.init()
                    if (j == 0) {
                        if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                            cipher.init(Cipher.ENCRYPT_MODE, aesKey, aesParms);
                            ct = cipher.update(pt);
                            pt = Arrays.copyOf(iv, iv.length);
                        } else {
                            cipher.init(Cipher.DECRYPT_MODE, aesKey, aesParms);
                            pt = cipher.update(ct);
                            ct = Arrays.copyOf(iv, iv.length);
                        }
                    }
                    // For all other iterations, we have to call the cipher.update() method to
                    // continue with the encryption/decryption operation.
                    else {
                        if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                            byte[] temp = Arrays.copyOf(ct, ct.length);
                            ct = cipher.update(pt);
                            pt = Arrays.copyOf(temp, temp.length);
                        } else {
                            byte[] temp = Arrays.copyOf(pt, pt.length);
                            pt = cipher.update(ct);
                            ct = Arrays.copyOf(temp, temp.length);
                        }
                    }
                }

                // Error occurred when assigning the output of the encryption/decryption
                if (ct == null || pt == null) {
                    throw new ACVPJUnsupportedOperationException();
                }

                // For the next round of the MCT tests, we modify the key by performing
                // the XOR bitwise operation with the current key and the output of the
                // encryption or decryption test.
                byte[] newKey = new byte[key.length];
                byte[] xor = null;
                if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                    xor = ct;
                } else {
                    xor = pt;
                }
                for (int k = 0; k < newKey.length; k++) {
                    newKey[k] = (byte) (key[k] ^ xor[k]);
                }
                key = Arrays.copyOf(newKey, newKey.length);
                iv = Arrays.copyOf(xor, xor.length);

                // Finally, report the output of the test by adding it to the response
                if (this.getDirection() == SymCipherDirection.SYM_DIRECTION_ENCRYPT) {
                    testCaseResponse.addProperty("ct", StringUtils.byteToString(ct));
                } else {
                    testCaseResponse.addProperty("pt", StringUtils.byteToString(pt));
                }

                response.add(testCaseResponse);

            }
        } catch (Exception e) {
            LoggerUtil.log(LogLevel.ERROR, e.getMessage());
            throw new ACVPJUnsupportedOperationException();
        }

        return response;
    }

    /**
     * @return the testType
     */
    public SymCipherTestType getTestType() {
        return testType;
    }

    /**
     * @param testType the testType to set
     */
    public void setTestType(SymCipherTestType testType) {
        this.testType = testType;
    }

    /**
     * @return the direction
     */
    public SymCipherDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(SymCipherDirection direction) {
        this.direction = direction;
    }

    /**
     * @return the ivGenSource
     */
    public SymCipherIVGenSource getIvGenSource() {
        return ivGenSource;
    }

    /**
     * @param ivGenSource the ivGenSource to set
     */
    public void setIvGenSource(SymCipherIVGenSource ivGenSource) {
        this.ivGenSource = ivGenSource;
    }

    /**
     * @return the ivGenMode
     */
    public SymCipherIVGenMode getIvGenMode() {
        return ivGenMode;
    }

    /**
     * @param ivGenMode the ivGenMode to set
     */
    public void setIvGenMode(SymCipherIVGenMode ivGenMode) {
        this.ivGenMode = ivGenMode;
    }

    /**
     * @return the key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * @return the plainText
     */
    public byte[] getPlainText() {
        return plainText;
    }

    /**
     * @param plainText the plainText to set
     */
    public void setPlainText(byte[] plainText) {
        this.plainText = plainText;
    }

    /**
     * @return the additionalAuthenticatedData
     */
    public byte[] getAdditionalAuthenticatedData() {
        return additionalAuthenticatedData;
    }

    /**
     * @param additionalAuthenticatedData the additionalAuthenticatedData to set
     */
    public void setAdditionalAuthenticatedData(byte[] additionalAuthenticatedData) {
        this.additionalAuthenticatedData = additionalAuthenticatedData;
    }

    /**
     * @return the initializationVector
     */
    public byte[] getInitializationVector() {
        return initializationVector;
    }

    /**
     * @param initializationVector the initializationVector to set
     */
    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }

    /**
     * @return the cipherText
     */
    public byte[] getCipherText() {
        return cipherText;
    }

    /**
     * @param cipherText the cipherText to set
     */
    public void setCipherText(byte[] cipherText) {
        this.cipherText = cipherText;
    }

    /**
     * @return the tag
     */
    public byte[] getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(byte[] tag) {
        this.tag = tag;
    }

    /**
     * @return the ivRet
     */
    public String getIvRet() {
        return ivRet;
    }

    /**
     * @param ivRet the ivRet to set
     */
    public void setIvRet(String ivRet) {
        this.ivRet = ivRet;
    }

    /**
     * @return the ivRetAfter
     */
    public String getIvRetAfter() {
        return ivRetAfter;
    }

    /**
     * @param ivRetAfter the ivRetAfter to set
     */
    public void setIvRetAfter(String ivRetAfter) {
        this.ivRetAfter = ivRetAfter;
    }

    /**
     * @return the kwCipher
     */
    public int getKwCipher() {
        return kwCipher;
    }

    /**
     * @param kwCipher the kwCipher to set
     */
    public void setKwCipher(int kwCipher) {
        this.kwCipher = kwCipher;
    }

    /**
     * @return the mctIndex
     */
    public int getMctIndex() {
        return mctIndex;
    }

    /**
     * @param mctIndex the mctIndex to set
     */
    public void setMctIndex(int mctIndex) {
        this.mctIndex = mctIndex;
    }

}
