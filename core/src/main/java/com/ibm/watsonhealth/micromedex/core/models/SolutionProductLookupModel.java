package com.ibm.watsonhealth.micromedex.core.models;

import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SolutionProductLookupModel {

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    @Named("solutionName")
    @JsonProperty("name")
    private String name;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    @Named("solutionAbbreviation")
    @JsonProperty("abbreviation")
    private String abbreviation;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    @Named("solutionProductId")
    @JsonProperty("productId")
    private String productId;

    @ValueMapValue
    @Named("solutionGfcs")
    @JsonProperty("gfcs")
    private List<Long> gfcs;

    @ValueMapValue
    @Named("solutionOtherNames")
    @JsonProperty("otherNames")
    private List<String> otherNames;

}
