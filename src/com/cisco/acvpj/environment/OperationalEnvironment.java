/**
 *
 */
package com.cisco.acvpj.environment;

import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.google.gson.JsonObject;

/**
 * Information Object containing relevant information about the running
 * environment. Information is used in communication with the ACVP server.
 *
 * @author John-Michael Caskey
 */
public class OperationalEnvironment {

    /** Information about the vendor and contact personnel. */
    private Vendor vendor;

    /** Information about the running module. */
    private Module module;

    /**
     * Information about the software and hardware of the running environment.
     */
    private OppEnvInformation info;

    public String impDescription = "FOM 6.2a";

    /**
     * Constructor to initialize object with provided information
     *
     * @param vendor
     *            Information about the vendor and contact personnel
     * @param module
     *            Information about the running module
     * @param info
     *            Information about the software and hardware of the running
     *            environment
     */
    public OperationalEnvironment ( final Vendor vendor, final Module module, final OppEnvInformation info ) {
        setVendor( vendor );
        setModule( module );
        setInfo( info );
    }

    public OperationalEnvironment () {
        info = new OppEnvInformation();
    }

    /**
     * Retrieve the vendor information from environment
     *
     * @return the vendor
     */
    public Vendor getVendor () {
        return vendor;
    }

    /**
     * Set the environment vendor information for use in communication
     *
     * @param vendor
     *            the vendor to set
     */
    public void setVendor ( final Vendor vendor ) {
        if ( vendor == null ) {
            throw new IllegalArgumentException( "Vendor information null." );
        }
        this.vendor = vendor;
    }

    /**
     * Retrieve Cryptographic module information from environment.
     *
     * @return the module
     */
    public Module getModule () {
        return module;
    }

    /**
     * Set the module information for the environment to send to the server
     *
     * @param module
     *            the module to set
     */
    public void setModule ( final Module module ) {
        if ( module == null ) {
            throw new IllegalArgumentException( "Module information null" );
        }
        this.module = module;
    }

    /**
     * Retrieve the Operational environment including software and hardware
     * specifications
     *
     * @return the info
     */
    public OppEnvInformation getInfo () {
        return info;
    }

    /**
     * Set the software and hardware specific information for the environment.
     *
     * @param info
     *            the info to set
     */
    public void setInfo ( final OppEnvInformation info ) {
        if ( info == null ) {
            throw new IllegalArgumentException( "Module information null" );
        }

        this.info = info;
    }

    /**
     * Set the test session vendor information for current run
     *
     * @param vendorName
     *            String Name of the vendor
     * @param vendorURL
     *            String containing url to vendor website
     * @param contactName
     *            String containing name of contact person attached to vendor
     * @param contactEmail
     *            String containing email of the vendor contact person
     */
    public void setVendorInfo ( final String vendorName, final String vendorUrl, final String contactName,
            final String contactEmail ) {
        if ( vendorName == null || vendorName.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid vendor name provided. Vendor name must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid vendor name provided. Vendor name must be string of length greater than 0." );
        }
        if ( vendorUrl == null || vendorUrl.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid vendor url provided. Vendor url must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid vendor url provided. Vendor url must be string of length greater than 0." );
        }
        if ( contactName == null || contactName.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid contact name provided. Contact name must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid contact name provided. Contact name must be string of length greater than 0." );
        }
        if ( contactEmail == null || contactEmail.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid contact email provided. Contact email must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid contact email provided. Contact email must be string of length greater than 0." );
        }

        LoggerUtil.log( LogLevel.INFO, "Vendor and Contact Information:" );

        LoggerUtil.log( LogLevel.INFO, "Vendor name set: " + vendorName );
        LoggerUtil.log( LogLevel.INFO, "Vendor type set: " + vendorUrl );
        LoggerUtil.log( LogLevel.INFO, "Contact name set: " + contactName );
        LoggerUtil.log( LogLevel.INFO, "Contact email set: " + contactEmail );

        this.vendor = new Vendor( vendorName, vendorUrl );
        this.vendor.addContact( contactName, contactEmail );
    }

    /**
     * Set The module information for the cryptomodule in the Test Session.
     *
     * @param moduleName
     *            String containing name of cryptomodule
     * @param moduleType
     *            String containing description of module, (Software, Hardware)
     * @param moduleVersion
     *            String of the module version number
     * @param moduleDescription
     *            brief description of module and capabilities
     */
    public void setModuleInfo ( final String moduleName, final String moduleType, final String moduleVersion,
            final String moduleDescription ) {
        if ( moduleName == null || moduleName.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid module name provided. Module name must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid module name provided. Module name must be string of length greater than 0." );
        }
        if ( moduleType == null || moduleType.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid module type provided. Module type must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid module type provided. Module type must be string of length greater than 0." );
        }
        if ( moduleVersion == null || moduleVersion.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid module version provided. Module version must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid Module version provided. Module version must be string of length greater than 0." );
        }
        if ( moduleDescription == null || moduleDescription.length() == 0 ) {
            LoggerUtil.log( LogLevel.ERROR,
                    "Invalid module description provided. Module description must be string of length greater than 0." );
            throw new IllegalArgumentException(
                    "Invalid Module description provided. Module description must be string of length greater than 0." );
        }

        LoggerUtil.log( LogLevel.INFO, "Module Information:" );

        LoggerUtil.log( LogLevel.INFO, "Module name set: " + moduleName );
        LoggerUtil.log( LogLevel.INFO, "Module type set: " + moduleType );
        LoggerUtil.log( LogLevel.INFO, "Module Description: " + moduleDescription );
        LoggerUtil.log( LogLevel.INFO, "Module version set: " + moduleVersion );
        this.module = new Module( moduleName, moduleVersion, moduleType );
    }

    /**
     * Utility function that creates GSON JsonObjects out of contained
     * information
     *
     * @return JsonObject of contained information
     */
    public JsonObject toJsonObject () {
        final JsonObject oe = new JsonObject();
        oe.add( "vendor", vendor.toJsonObject() );
        oe.add( "module", module.toJsonObject() );
        oe.add( "operationalEnvironment", info.toJsonObject() );
        oe.addProperty( "implementationDescription", impDescription );
        return oe;
    }
}
