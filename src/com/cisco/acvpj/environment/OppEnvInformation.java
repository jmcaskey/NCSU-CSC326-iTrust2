/**
 *
 */
package com.cisco.acvpj.environment;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Environment information containing references to software and hardware
 * characteristics.
 *
 * @author John-Michael Caskey
 */
public class OppEnvInformation {

    /** List of environment dependencies including both software and hardware */
    private final List<JsonObject> dependencies;

    // TODO implementation of generic information. Presently sample infomation
    public OppEnvInformation () {
        dependencies = new LinkedList<JsonObject>();

        final JsonObject soft = new JsonObject();
        soft.addProperty( "type", "software" );
        soft.addProperty( "name", "Linux 3.1" );
        soft.addProperty( "cpe", "cpe-2.3:o:ubuntu:linux:3.1" );

        dependencies.add( soft );

        final JsonObject hard = new JsonObject();
        hard.addProperty( "type", "processor" );
        hard.addProperty( "manufacturer", "Intel" );
        hard.addProperty( "family", "ARK" );
        hard.addProperty( "name", "Xeon" );
        hard.addProperty( "series", "5100" );

        dependencies.add( hard );
    }

    /**
     * Utility function that creates GSON JsonObjects out of contained
     * information
     *
     * @return JsonObject of contained information
     */
    public JsonObject toJsonObject () {
        final JsonObject jObj = new JsonObject();

        final JsonArray arr = new JsonArray();

        for ( int i = 0; i < dependencies.size(); i++ ) {
            arr.add( dependencies.get( i ) );
        }
        jObj.add( "dependencies", arr );
        return jObj;
    }
}
