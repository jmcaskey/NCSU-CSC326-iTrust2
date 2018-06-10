/**
 *
 */
package com.cisco.acvpj.environment;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Vendor information containing references to vendor and contact
 * characteristics.
 *
 * @author John-Michael Caskey
 */
public class Vendor {

    /** Name of the vendor. */
    private String name;

    /** String referencing he website url. */
    private String website;

    /** List of contact personnel. */
    public List<JsonObject> contact;

    /**
     * Constructs a vendor object
     *
     * @param name
     *            the name of the vendor
     * @param website
     *            the website url
     */
    public Vendor ( final String name, final String website ) {
        setName( name );
        setWebsite( website );
        contact = new LinkedList<JsonObject>();
    }

    /**
     * Retrieve the vendors name
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Set the name of the vendor
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieve the website url.
     *
     * @return the website
     */
    public String getWebsite () {
        return website;
    }

    /**
     * Set the website url.
     *
     * @param website
     *            the website to set
     */
    public void setWebsite ( final String website ) {
        this.website = website;
    }

    /**
     * Add contract to the contact list
     * 
     * @param name
     *            the name of the contact
     * @param email
     *            the email of the contact
     */
    public void addContact ( final String name, final String email ) {
        final JsonObject jObj = new JsonObject();
        jObj.addProperty( "name", name );
        jObj.addProperty( "email", email );
        contact.add( jObj );
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
        jObj.addProperty( "website", website );

        final JsonArray arr = new JsonArray();
        for ( int i = 0; i < contact.size(); i++ ) {
            arr.add( contact.get( 0 ) );
        }
        jObj.add( "contact", arr );
        return jObj;
    }
}
