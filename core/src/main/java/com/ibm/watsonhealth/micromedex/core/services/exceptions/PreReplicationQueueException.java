package com.ibm.watsonhealth.micromedex.core.services.exceptions;

public class PreReplicationQueueException extends Exception {

    public PreReplicationQueueException(final String message) {
        super(message);
    }

    public PreReplicationQueueException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
