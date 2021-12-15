package com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions;

public class RootResourceInitializationException extends Exception {

    public RootResourceInitializationException(final String path) {
        super("Exception while initializing the root node '" + path + "'");
    }

}
