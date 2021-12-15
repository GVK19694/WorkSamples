package com.ibm.watsonhealth.micromedex.core.auth.userinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {

    @JsonProperty("email")
    private String email;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("identities")
    private List<Identities> identities;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public List<Identities> getIdentities() {
        return this.identities;
    }

    public void setIdentities(final List<Identities> identities) {
        this.identities = identities;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(final String name, final Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isInGroup(final String[] commonNames) {
        return this.hasAttributes() && this.identities.get(0).getIdpUserInfo().getAttributes().isInGroup(commonNames);
    }

    private boolean hasAttributes() {
        return CollectionUtils.isNotEmpty(this.identities) && this.identities.get(0).getIdpUserInfo() != null && this.identities
          .get(0)
          .getIdpUserInfo()
          .getAttributes() != null;
    }

}
