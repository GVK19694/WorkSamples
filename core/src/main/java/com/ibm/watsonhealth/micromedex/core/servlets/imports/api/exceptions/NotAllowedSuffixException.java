package com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions;

public class NotAllowedSuffixException extends Exception {

    public NotAllowedSuffixException(final String suffix) {
        super("suffix '" + suffix + "' is not allowed");
    }

}
