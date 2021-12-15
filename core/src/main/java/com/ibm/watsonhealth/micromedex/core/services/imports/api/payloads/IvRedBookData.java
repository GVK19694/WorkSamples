package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class IvRedBookData {

    @JsonProperty("gcr")
    private List<IvRedBookGcr> gcrs;

    @JsonCreator
    public IvRedBookData(@JsonProperty("gcr") final List<IvRedBookGcr> gcrs) {
        this.gcrs = gcrs;
    }

}
