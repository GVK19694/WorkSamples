package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class IvRedBookProduct {

    @JsonProperty("ndc_code")
    private String ndcCode;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    public IvRedBookProduct(@JsonProperty("ndc_code") final String ndcCode, @JsonProperty("name") final String name, @JsonProperty("category") final String category) {
        this.ndcCode = ndcCode;
        this.name = name;
        this.category = category;
    }

}
