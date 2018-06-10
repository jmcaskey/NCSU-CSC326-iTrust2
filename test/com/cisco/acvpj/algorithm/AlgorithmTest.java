/**
 *
 */
package com.cisco.acvpj.algorithm;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Testing Algorithm classes.
 *
 * @author John-Michael Caskey
 */
public class AlgorithmTest {

    @Test
    public void algortihmTest () {

        final File file = new File( "config/algorithm.json" );

        if ( file.delete() ) {
            System.out.println( "File deleted successfully" );
        } else {
            System.out.println( "File does not exist" );
        }
        final JsonObject jObj = new JsonObject();

        jObj.addProperty( "algorithm", "SHA-1" );
        jObj.addProperty( "inBit", false );
        jObj.addProperty( "inEmpty", true );

        final GsonBuilder builder = new GsonBuilder();

        final JsonObject aObj = new JsonObject();
        aObj.addProperty( "algorithm", "AES-CBC" );
        final String[] sArr = { "encrypt", "decrypt" };
        aObj.add( "direction", builder.create().toJsonTree( sArr ) );
        final int[] kArr = { 128 };
        aObj.add( "keyLen", builder.create().toJsonTree( kArr ) );
        final int[] pArr = { 1536 };
        aObj.add( "ptLen", builder.create().toJsonTree( pArr ) );

        final String[] test = { "SHA-1", "AES-CBC" };
        Algorithm.algorithms = null;
        JsonArray result = Algorithm.getAlgorithms( test );
        assertTrue( "SHA-1 not returned in sample getAlgorithms method.", result.contains( jObj ) );
        assertTrue( "AES_CBC not returned in sample getAlgorithms method.", result.contains( aObj ) );

        Algorithm.algorithms = null;
        result = Algorithm.getAlgorithms( test );
        assertTrue( "SHA-1 not returned in sample getAlgorithms method.", result.contains( jObj ) );
        assertTrue( "AES_CBC not returned in sample getAlgorithms method.", result.contains( aObj ) );

    }

}
