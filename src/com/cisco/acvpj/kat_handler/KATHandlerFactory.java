package com.cisco.acvpj.kat_handler;

import com.cisco.acvpj.enumerations.ACVPCipher;
import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;

/**
 * KATHandlerFactory is a static singleton class which is used to get
 * KATHandlers for all of the algorithms.
 *
 * @author Andrew Shryock (ajshryoc)
 */
public class KATHandlerFactory {

    /**
     * This class should be a singleton because only one instance will ever be
     * needed
     */
    private static KATHandlerFactory instance = null;

    /**
     * Private constructor for this class; currently just sets up the list
     */
    private KATHandlerFactory() {}

    /**
     * Get the static instance of KATHandlerFactory
     *
     * @return the static instance of KATHandlerFactory
     */
    public static KATHandlerFactory getInstance() {
        if (instance == null) {
            instance = new KATHandlerFactory();
        }

        return instance;
    }

    /**
     * Return the KATHandler specified by the parameter
     *
     * @param type the type and mode of the KATHandler specified by an enum
     * @return the requested KATHandler
     */
    public KATHandler getKATHandler(ACVPCipher cipher) {
        KATHandler handler = null;

        switch (cipher) {
            case ACVP_AES_CBC:
                handler = new AESKATHandler(); break;
            case ACVP_SHA1:
                handler = new HashKATHandler(); break;
            case ACVP_SHA256:
                handler = new HashKATHandler(); break;
            case ACVP_SHA512:
                handler = new HashKATHandler(); break;
            case ACVP_HMAC_SHA1:
                handler = new HmacKATHandler(); break;
            case ACVP_HMAC_SHA2_256:
                handler = new HmacKATHandler(); break;
            case ACVP_HMAC_SHA2_512:
                handler = new HmacKATHandler(); break;
            default:
                LoggerUtil.log(LogLevel.ERROR, "Unsupported algorithm.");
                throw new ACVPJUnsupportedOperationException();
        }

        return handler;
    }

}
