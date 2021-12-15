package com.ibm.watsonhealth.micromedex.core.auth.userinfo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Identities {

    @JsonProperty("id")
    private String id;

    @JsonProperty("idpUserInfo")
    private IdpUserInfo idpUserInfo;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public IdpUserInfo getIdpUserInfo() {
        return this.idpUserInfo;
    }

    public void setIdpUserInfo(final IdpUserInfo idpUserInfo) {
        this.idpUserInfo = idpUserInfo;
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
