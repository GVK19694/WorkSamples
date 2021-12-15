package com.ibm.watsonhealth.micromedex.core.auth.userinfo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdpUserInfo {

    @JsonProperty("name_id")
    private String nameId;

    @JsonProperty("attributes")
    private Attributes attributes;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getNameId() {
        return this.nameId;
    }

    public void setNameId(final String nameId) {
        this.nameId = nameId;
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public void setAttributes(final Attributes attributes) {
        this.attributes = attributes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(final String name, final Object value) {
        this.additionalProperties.put(name, value);
    }

}
