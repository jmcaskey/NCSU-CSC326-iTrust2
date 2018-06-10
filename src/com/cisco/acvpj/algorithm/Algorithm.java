/**
 *
 */
package com.cisco.acvpj.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import com.cisco.acvpj.util.LogLevel;
import com.cisco.acvpj.util.LoggerUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Algorithm class contains utility function to register and retrieve algorithms
 *
 * @author John-Michael Caskey
 */
public class Algorithm {

    /**
     * Array to hold all of the available algorithms
     */
    public static JsonArray algorithms;

    /**
     * Gets all the algorithms and adds them to the json array
     * @param names array of algorithm names
     * @return the json array of algorithms
     */
    public static JsonArray getAlgorithms ( final String[] names ) {
        if ( algorithms == null ) {
            LoggerUtil.log( LogLevel.STATUS, "Getting algorithms from config file" );
            algorithms = getAvailableAlgorithmsConfig();
        }

        final JsonArray arr = new JsonArray();

        for ( int i = 0; i < names.length; i++ ) {
            final String name = names[i];
            for ( int j = 0; j < algorithms.size(); j++ ) {
                if ( name.equals( algorithms.get( j ).getAsJsonObject().get( "algorithm" ).getAsString() ) ) {
                    arr.add( algorithms.get( j ) );
                    break;
                }
            }

        }

        LoggerUtil.log( LogLevel.INFO, "algorithms: " + arr.toString() );

        return arr;
    }

    /**
     * Get all available algorithms from the config file
     * @return json array of the algoritms 
     */
    private static JsonArray getAvailableAlgorithmsConfig () {
        try {
            final JsonParser reader = new JsonParser();
            algorithms = reader.parse( new FileReader( "config/algorithm.json" ) ).getAsJsonArray();
        } catch ( final FileNotFoundException e ) {
            LoggerUtil.log(LogLevel.STATUS, "Could not find config file. Using default algorithms.");
            algorithms = generateAvailableAlgorithmsConfig();
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        return algorithms;
    }

    /**
     * If there is no config file found, this will generate one with the default algorithms
     * Right now the default algorithms are:
     *   AES-CBC
     *   SHA-1
     *   SHA-224
     *   SHA-256
     *   SHA-384
     *   SHA-512
     * @return the json array of default algorithms 
     */
    public static JsonArray generateAvailableAlgorithmsConfig () {
        LoggerUtil.log( LogLevel.STATUS, "Generating algorithms config file" );

        final JsonArray arr = new JsonArray();
        final GsonBuilder builder = new GsonBuilder();

        JsonObject jObj = new JsonObject();
        jObj.addProperty( "algorithm", "AES-CBC" );
        final String[] sArr = { "encrypt", "decrypt" };
        jObj.add( "direction", builder.create().toJsonTree( sArr ) );
        final int[] kArr = { 128 };
        jObj.add( "keyLen", builder.create().toJsonTree( kArr ) );

        final int[] pArr = { 1536 };
        jObj.add( "ptLen", builder.create().toJsonTree( pArr ) );
        arr.add( jObj );

        jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-1" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );
        arr.add( jObj );

        jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-224" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );
        arr.add( jObj );

        jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-256" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );
        arr.add( jObj );

        jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-384" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );
        arr.add( jObj );

        jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-512" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );
        arr.add( jObj );

        algorithms = arr;

        PrintStream os;
        try {
            os = new PrintStream( new File( "config/algorithm.json" ) );
            os.print( builder.setPrettyPrinting().create().toJson( arr ) );
            os.flush();
            os.close();
        } catch ( final FileNotFoundException e ) {
            e.printStackTrace();
        }

        LoggerUtil.log( LogLevel.STATUS, "Config file complete: config/algorithm.json" );
        return arr;
    }
}
