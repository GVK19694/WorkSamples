package com.ibm.watsonhealth.micromedex.core.auth.userinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attributes {

    @JsonProperty("cn")
    private String cn;

    @JsonProperty("emailAddress")
    private String email;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("blueGroups")
    private List<String> blueGroups;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonIgnore
    private final List<String> blueGroupsCommonNames = new ArrayList<>();

    public String getCn() {
        return this.cn;
    }

    public void setCn(final String cn) {
        this.cn = cn;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public List<String> getBlueGroups() {
        return this.blueGroups;
    }

    public void setBlueGroups(final List<String> blueGroups) {
        this.blueGroups = blueGroups;
        this.blueGroupsCommonNames.clear();
        Optional<String> blueGroupCommonName;
        for (final String blueGroup : blueGroups) {
            blueGroupCommonName = this.getCommonNameFromBlueGroup(blueGroup);
            blueGroupCommonName.ifPresent(this.blueGroupsCommonNames::add);
        }
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
        return CollectionUtils.containsAny(this.blueGroupsCommonNames, commonNames);
    }

    private Optional<String> getCommonNameFromBlueGroup(final String blueGroup) {
        Optional<String> result = Optional.empty();
        final String[] items = StringUtils.split(blueGroup, ",");
        final Optional<String> commonName = Arrays.stream(items).findFirst().filter(item -> StringUtils.startsWith(item, "cn="));
        if (commonName.isPresent()) {
            result = Optional.of(StringUtils.substring(commonName.get(), 3));
        }
        return result;
    }

}
