package com.ibm.watsonhealth.micromedex.core.vo.validation;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class ValidationErrorVO {

    @Getter
    @JsonProperty
    private final String message;

    @Getter
    @JsonProperty
    private final String path;

    public ValidationErrorVO(@NotNull final String message, @NotNull final String path) {
        this.message = message;
        this.path = path;
    }

}
