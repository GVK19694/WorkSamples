package com.ibm.watsonhealth.micromedex.core.services.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class GetDataException extends Exception {

    public GetDataException(@NotNull final Throwable throwable) {
        super(throwable);
    }

}
