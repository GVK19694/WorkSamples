package com.ibm.watsonhealth.micromedex.core.servlets.imports.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessageVO {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    public ErrorMessageVO(@JsonProperty("code") final int code, @JsonProperty("message") final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
