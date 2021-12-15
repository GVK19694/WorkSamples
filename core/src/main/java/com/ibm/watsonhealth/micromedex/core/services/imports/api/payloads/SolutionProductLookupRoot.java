package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SolutionProductLookupRoot {

    @Setter
    private LocalDateTime created;

    @Setter
    private String jcrPath;

    @JsonProperty("solutions")
    private List<SolutionProductLookup> solutionProductLookups;

    @JsonCreator
    public SolutionProductLookupRoot(@JsonProperty("solutions") final List<SolutionProductLookup> solutionProductLookups) {
        this.solutionProductLookups = solutionProductLookups;
    }

}
