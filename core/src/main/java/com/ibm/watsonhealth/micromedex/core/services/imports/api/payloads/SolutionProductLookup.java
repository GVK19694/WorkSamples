package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class SolutionProductLookup {

    @Getter
    @JsonProperty("product_id")
    private Long productId;

    @Getter
    @JsonProperty("name")
    private String name;

    @Getter
    @JsonProperty("abbreviation")
    private String abbreviation;

    @Getter
    @JsonProperty("gfc")
    private List<Long> gfcs;

    @Getter
    @JsonProperty("othername")
    private List<String> otherNames;

    @JsonCreator
    public SolutionProductLookup(@JsonProperty("product_id") final String productId, @JsonProperty("name") final String name, @JsonProperty("abbreviation") final String abbreviation, @JsonProperty("gfc") final List<String> gfcs, @JsonProperty("othername") final List<String> otherNames) {
        this.productId = Long.parseLong(productId);
        this.name = StringUtils.trim(name);
        this.abbreviation = StringUtils.trim(abbreviation);
        this.gfcs = new ArrayList<>();
        if (gfcs != null) {
            gfcs.forEach(gfc -> this.gfcs.add(Long.parseLong(gfc)));
        }
        this.otherNames = new ArrayList<>();
        if (otherNames != null) {
            otherNames.forEach(otherName -> this.otherNames.add(StringUtils.trim(otherName)));
        }
    }

}
