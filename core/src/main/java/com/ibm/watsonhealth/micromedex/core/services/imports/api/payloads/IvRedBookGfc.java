package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class IvRedBookGfc {

    @JsonProperty("gfc_code")
    private long gfcCode;

    @JsonProperty("roa_code")
    private String roaCode;

    @JsonProperty("form")
    private String form;

    @JsonProperty("strength")
    private String strength;

    @JsonProperty("product")
    private List<IvRedBookProduct> products;

    @JsonCreator
    public IvRedBookGfc(@JsonProperty("gfc_code") final long gfcCode, @JsonProperty("roa_code") final String roaCode, @JsonProperty("form") final String form, @JsonProperty("strength") final String strength, @JsonProperty("product") final List<IvRedBookProduct> products) {
        this.gfcCode = gfcCode;
        this.roaCode = roaCode;
        this.form = form;
        this.strength = strength;
        this.products = products;
    }

}
