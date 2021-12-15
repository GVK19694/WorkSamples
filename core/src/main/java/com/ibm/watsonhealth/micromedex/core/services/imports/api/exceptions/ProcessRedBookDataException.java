package com.ibm.watsonhealth.micromedex.core.services.imports.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class ProcessRedBookDataException extends Exception {

    public ProcessRedBookDataException(@NotNull final Throwable throwable) {
        super(throwable);
    }

}
