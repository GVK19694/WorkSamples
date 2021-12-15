package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class IvRedBookGcr {

    @JsonProperty("code")
    private final long gcrCode;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("gfcs")
    private final List<IvRedBookGfc> gfcs;

    @JsonCreator
    public IvRedBookGcr(@JsonProperty("code") final long gcrCode, @JsonProperty("name") final String name, @JsonProperty("gfcs") final List<IvRedBookGfc> gfcs) {
        this.gcrCode = gcrCode;
        this.name = name;
        this.gfcs = gfcs;
    }

}
