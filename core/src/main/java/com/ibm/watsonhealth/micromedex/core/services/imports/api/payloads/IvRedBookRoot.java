package com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
public class IvRedBookRoot {

    @Setter
    private LocalDateTime created;

    @Setter
    private String jcrPath;

    @JsonProperty("ivredbookdata")
    private List<IvRedBookData> data;

    @JsonCreator
    public IvRedBookRoot(@JsonProperty("ivredbookdata") final List<IvRedBookData> data) {
        this.data = data;
    }

}
