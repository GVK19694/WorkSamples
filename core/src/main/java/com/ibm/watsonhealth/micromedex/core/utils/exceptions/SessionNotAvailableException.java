package com.ibm.watsonhealth.micromedex.core.utils.exceptions;

public class SessionNotAvailableException extends Exception {

    public SessionNotAvailableException() {
        super("no valid session found in current request.");
    }

}
