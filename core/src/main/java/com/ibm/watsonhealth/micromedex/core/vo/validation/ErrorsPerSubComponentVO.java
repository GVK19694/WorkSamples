package com.ibm.watsonhealth.micromedex.core.vo.validation;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class ErrorsPerSubComponentVO {

    @Getter
    @JsonProperty
    private String path;

    @JsonProperty
    private List<ValidationErrorVO> errors;

    public ErrorsPerSubComponentVO(@NotNull final String path) {
        this.path = path;
        this.errors = new ArrayList<>();
    }

    public void addError(@NotNull final ValidationErrorVO error) {
        this.errors.add(error);
    }

}
