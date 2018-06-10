package com.cisco.acvpj.util;

import javax.xml.bind.DatatypeConverter;

import com.cisco.acvpj.exceptions.ACVPJConversionFailureException;

/**
 * Class that provides static methods for String operations required by ACVPJ.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public class StringUtils {

    /**
     * Convert a hexadecimal String to a byte array.
     * @param hexString the input String
     * @return a String formatted as bytes
     *
     * @throws ACVPJConversionFailureException if the input String does not match the format
     */
    public static byte[] hexToBinary(String hexString) {
        return DatatypeConverter.parseHexBinary(hexString);
    }

    /**
     * Convert a bit String to a byte[] with bit0 = 0x80.
     * @param bitString the input String
     * @return byte[] with bit0 = 0x80
     *
     * NOTE: This is largely taken from LibACVP; may need to revisit if it does not work 100%.
     * @throws ACVPJConversionFailureException if the input String does not match the format
     */
    public static byte[] bitToBinary(String bitString) {
        if (bitString.length() == 0) {
            throw new ACVPJConversionFailureException("Bit string has length 0.");
        }

        byte output[] = new byte[bitString.length()];

        for (int i = 0; i < bitString.length(); ++i) {
            if (bitString.charAt(i) == '1') {
                output[i/8] |= (0x80 >> (i % 8));
            }
        }

        return output;
    }

    /**
     * Convert characters from a hexidecimal formatted String to a binary bit string.
     * @param binaryString the hexadecimal String to convert to binary bit string
     * @return a String formatted as a binary bit string
     *
     * NOTE: This is largely taken from LibACVP; may need to revisit if it does not work 100%.
     * @throws ACVPJConversionFailureException if the input String does not match the format
     */
    public static String binaryToBit(String binaryString) {
        if (binaryString.length() == 0) {
            throw new ACVPJConversionFailureException("Binary string has length 0.");
        }

        char output[] = new char[binaryString.length()];

        for (int i = 0; i < binaryString.length(); ++i) {
            output[i] = (binaryString.charAt(i/8) & (0x80 >> (i % 8))) == 1 ? '1' : '0';
        }

        return new String(output);
    }

    /**
     * Converts a byte[] into a hexadecimal String.
     * @param byteString byte[] to convert
     * @return hexadecimal formatted String
     */
    public static String byteToString(byte[] byteString) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteString.length; i++) {
            String hex = Integer.toHexString(0xff & byteString[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}
