package com.cisco.acvpj.enumerations;

/**
 * Global enum that is used to specify the algorithm and the mode in which it should be run.
 * This is copied over from LibACVP; ACVPJ does not support all of these algorithms/modes.
 * This enum is intended to be used to verify the current algorithm/mode in use; use lookUpCipher
 * to actually initialize this enum for a KATHandler.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public enum ACVPCipher {
    ACVP_CIPHER_NONE,
    ACVP_AES_GCM,
    ACVP_AES_CCM,
    ACVP_AES_ECB,
    ACVP_AES_CBC,
    ACVP_AES_CFB1,
    ACVP_AES_CFB8,
    ACVP_AES_CFB128,
    ACVP_AES_OFB,
    ACVP_AES_CTR,
    ACVP_AES_XTS,
    ACVP_AES_KW,
    ACVP_AES_KWP,
    ACVP_TDES_ECB,
    ACVP_TDES_CBC,
    ACVP_TDES_CBCI,
    ACVP_TDES_OFB,
    ACVP_TDES_OFBI,
    ACVP_TDES_CFB1,
    ACVP_TDES_CFB8,
    ACVP_TDES_CFB64,
    ACVP_TDES_CFBP1,
    ACVP_TDES_CFBP8,
    ACVP_TDES_CFBP64,
    ACVP_TDES_CTR,
    ACVP_TDES_KW,
    ACVP_SHA1,
    ACVP_SHA224,
    ACVP_SHA256,
    ACVP_SHA384,
    ACVP_SHA512,
    ACVP_HASHDRBG,
    ACVP_HMACDRBG,
    ACVP_CTRDRBG,
    ACVP_HMAC_SHA1,
    ACVP_HMAC_SHA2_224,
    ACVP_HMAC_SHA2_256,
    ACVP_HMAC_SHA2_384,
    ACVP_HMAC_SHA2_512,
    ACVP_HMAC_SHA2_512_224,
    ACVP_HMAC_SHA2_512_256,
    ACVP_HMAC_SHA3_224,
    ACVP_HMAC_SHA3_256,
    ACVP_HMAC_SHA3_384,
    ACVP_HMAC_SHA3_512,
    ACVP_CMAC_AES_128,
    ACVP_CMAC_AES_192,
    ACVP_CMAC_AES_256,
    ACVP_CMAC_TDES,
    ACVP_RSA,
    ACVP_DSA,
    ACVP_KDF135_TLS,
    ACVP_KDF135_SNMP,
    ACVP_KDF135_SSH;

    /**
     * A helper function that will get a value for ACVPCipher based on a String.
     * @param name of the algorithm
     * @return an instance of ACVPCipher corresponding to the algorithm
     *         if it exists; if not, return ACVP_CIPHER_NONE.
     */
    public static ACVPCipher lookUpCipher(String name) {
        ACVPCipher cipher;
        switch (name) {
            case "AES-CBC":
                cipher = ACVP_AES_CBC; break;
            case "SHA-1":
                cipher = ACVP_SHA1; break;
            case "SHA-256":
                cipher = ACVP_SHA256; break;
            case "SHA-512":
                cipher = ACVP_SHA512; break;
            case "HMAC-SHA-1":
                cipher = ACVP_HMAC_SHA1; break;
            case "HMAC-SHA2-256":
                cipher = ACVP_HMAC_SHA2_256; break;
            case "HMAC-SHA2-512":
                cipher = ACVP_HMAC_SHA2_512; break;
            default:
                cipher = ACVP_CIPHER_NONE; break;
        }
        return cipher;

    }
}
