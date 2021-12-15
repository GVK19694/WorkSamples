package com.ibm.watsonhealth.micromedex.core.models.exceptions;

import org.jetbrains.annotations.NotNull;

public class InitException extends Exception {

    public InitException(@NotNull final Throwable throwable) {
        super(throwable);
    }

}
