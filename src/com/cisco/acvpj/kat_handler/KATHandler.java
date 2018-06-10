package com.cisco.acvpj.kat_handler;

import com.google.gson.JsonObject;

/**
 * This interface outlines the behavior of each KATHandler that will be
 * implemented. Each KATHandler must have a handler method which takes a JSON
 * object as a parameter, runs all test-related functions, then returns the
 * output of the tests.
 * 
 * @author Andrew Shryock (ajshryoc)
 */
public interface KATHandler {

    /**
     * Main method each KATHandler will implement
     * 
     * @param json
     *            input JSON object
     * @return output JSON object
     */
    public JsonObject handler(JsonObject json);

}
