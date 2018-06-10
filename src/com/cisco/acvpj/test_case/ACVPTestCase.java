/**
 *
 */
package com.cisco.acvpj.test_case;

import com.cisco.acvpj.enumerations.ACVPCipher;

/**
 * @author Chris Miller (cjmille7)
 *
 */
public abstract class ACVPTestCase {
    private ACVPCipher cipher;
    private int testId;


    public void testCaseHandler() {
        //TODO
    }

    /**
     * Get the cipher
     * @return
     */
    public ACVPCipher getCipher() {
        return cipher;
    }

    /**
     * Set the cipher
     * @param cipher
     */
    public void setCipher(ACVPCipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Get the test id
     * @return
     */
    public int getTestId() {
        return testId;
    }

    /**
     * Set the test id
     * @param testId
     */
    public void setTestId(int testId) {
        this.testId = testId;
    }
}
