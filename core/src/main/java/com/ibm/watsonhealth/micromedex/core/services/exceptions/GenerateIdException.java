package com.ibm.watsonhealth.micromedex.core.services.exceptions;

import org.jetbrains.annotations.NotNull;

public class GenerateIdException extends Exception {

    public GenerateIdException(@NotNull final String message) {
        super(message);
    }

    public GenerateIdException(@NotNull final Throwable throwable) {
        super(throwable);
    }

}
