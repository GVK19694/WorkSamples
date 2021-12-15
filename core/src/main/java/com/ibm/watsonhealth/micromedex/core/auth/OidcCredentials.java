package com.ibm.watsonhealth.micromedex.core.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;

import org.jetbrains.annotations.NotNull;

import io.jsonwebtoken.Claims;

public class OidcCredentials implements Credentials {

    private static final long serialVersionUID = 334206366871711814L;

    private final String subject;
    private final Map<String, Object> claims = new HashMap<>();
    private final String idpName;

    private final Map<String, Object> attributes = new HashMap<>();

    public OidcCredentials(final String subject, final Claims claims, final String idpName) {
        this.subject = subject;
        if (claims != null) {
            this.claims.putAll(claims);
        }
        this.idpName = idpName;
    }

    public String getSubject() {
        return this.subject;
    }

    public Map<String, Object> getClaims() {
        return this.claims;
    }

    public String getIdpName() {
        return this.idpName;
    }

    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        this.attributes.put(name, value);
    }

    public @NotNull
    Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

}
