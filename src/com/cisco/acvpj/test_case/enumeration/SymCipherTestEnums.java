package com.cisco.acvpj.test_case.enumeration;

import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;

public class SymCipherTestEnums {
    public enum SymCipherTestType {
        SYM_TEST_TYPE_NONE,
        SYM_TEST_TYPE_AFT,
        SYM_TEST_TYPE_MCT;

        /**
         * Converts a String into the corresponding value of SymCipherTestType
         * @param testType String representing a value of SymCipherTestType
         * @return the correct value of SymCipherTestType
         */
        public static SymCipherTestType lookUpTestType(String testType) {
            SymCipherTestType type;
            if (testType != null) {
                if (testType.equalsIgnoreCase("aft")) {
                    type = SymCipherTestType.SYM_TEST_TYPE_AFT;
                } else if (testType.equalsIgnoreCase("mct")) {
                    type = SymCipherTestType.SYM_TEST_TYPE_MCT;
                } else {
                    LoggerUtil.log(LogLevel.ERROR, String.format(
                            "unsupported test mode requested from server (%s)", testType));
                    throw new ACVPJUnsupportedOperationException();
                }
            } else {
                LoggerUtil.log(LogLevel.ERROR, String.format(
                        "unsupported test mode requested from server (%s)", testType));
                throw new ACVPJUnsupportedOperationException();
            }
            return type;
        }
    }

    public enum SymCipherDirection {
        SYM_DIRECTION_ENCRYPT,
        SYM_DIRECTION_DECRYPT;

        /**
         * Converts a String into the corresponding value of SymCipherDirection
         * @param direction String representing a value of SymCipherDirection
         * @return the correct value of SymCipherDirection
         */
        public static SymCipherDirection lookUpDirection(String direction) {
            SymCipherDirection dir;
            if (direction != null) {
                if (direction.equalsIgnoreCase("encrypt")) {
                    dir = SymCipherDirection.SYM_DIRECTION_ENCRYPT;
                } else if (direction.equalsIgnoreCase("decrypt")) {
                    dir = SymCipherDirection.SYM_DIRECTION_DECRYPT;
                } else {
                    LoggerUtil.log(LogLevel.ERROR, String.format(
                            "unsupported direction requested from server (%s)", direction));
                    throw new ACVPJUnsupportedOperationException();
                }
            } else {
                LoggerUtil.log(LogLevel.ERROR, String.format(
                        "unsupported direction requested from server (%s)", direction));
                throw new ACVPJUnsupportedOperationException();
            }
            return dir;
        }
    }

    public enum SymCipherIVGenMode {
        SYM_IVGEN_MODE_821,
        SYM_IVGEN_MODE_822,
        SYM_IVGEN_MODE_NA
    }

    public enum SymCipherIVGenSource {
        SYM_IVGEN_SRC_INT,
        SYM_IVGEN_SRC_EXT,
        SYM_IVGEN_SRC_NA
    }
}
