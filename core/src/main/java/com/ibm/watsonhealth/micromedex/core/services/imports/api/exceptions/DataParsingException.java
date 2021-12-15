package com.ibm.watsonhealth.micromedex.core.services.imports.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class DataParsingException extends Exception {

    public DataParsingException(@NotNull final String path, @NotNull final Throwable throwable) {
        super("exception while parsing data from " + path, throwable);
    }

}
