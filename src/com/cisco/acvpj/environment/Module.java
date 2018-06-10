/**
 *
 */
package com.cisco.acvpj.environment;

import com.google.gson.JsonObject;

/**
 * Cryptographic module specific information used to communicate to the server.
 * Information is kept and is output as a JsonObject to construct POST body
 * statements.
 *
 * @author John-Michael Caskey
 */
public class Module {

    /** Name of the module. */
    private String name;

    /** String representing the current version of the module. */
    private String version;

    /** Description of the type of module represented by the object */
    private String type;

    /**
     * Constructor of object using information about module provided
     *
     * @param name
     *            Name of the module
     * @param version
     *            the current version of the module
     * @param type
     *            Description of the type of module represented by the object
     */
    public Module ( final String name, final String version, final String type ) {
        setName( name );
        setVersion( version );
        setType( type );
    }

    /**
     * Retrieve the module name.
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Set the module name.
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        if ( name == null || name.length() == 0 ) {
            throw new IllegalArgumentException( "Module name null or empty string." );
        }
        this.name = name;
    }

    /**
     * Retrieve the version string.
     *
     * @return the version
     */
    public String getVersion () {
        return version;
    }

    /**
     * Set the string representation of the version.
     *
     * @param version
     *            the version to set
     */
    public void setVersion ( final String version ) {
        if ( version == null || version.length() == 0 ) {
            throw new IllegalArgumentException( "Module version null or empty string." );
        }
        this.version = version;
    }

    /**
     * Retrieve the type of the module.
     *
     * @return the type
     */
    public String getType () {
        return type;
    }

    /**
     * Set the short description of the type of module
     *
     * @param type
     *            the type to set
     */
    public void setType ( final String type ) {
        if ( type == null || type.length() == 0 ) {
            throw new IllegalArgumentException( "Module version null or empty string." );
        }

        this.type = type;
    }

    /**
     * Utility function that creates GSON JsonObjects out of contained
     * information
     *
     * @return JsonObject of contained information
     */
    public JsonObject toJsonObject () {
        final JsonObject jObj = new JsonObject();
        jObj.addProperty( "name", name );
        jObj.addProperty( "version", version );
        jObj.addProperty( "type", type );
        return jObj;
    }

}
