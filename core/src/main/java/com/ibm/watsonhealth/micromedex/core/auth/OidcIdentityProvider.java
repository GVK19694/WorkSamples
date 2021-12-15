package com.ibm.watsonhealth.micromedex.core.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.Credentials;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.spi.security.authentication.credentials.CredentialsSupport;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalGroup;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentity;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityException;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityRef;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

public class OidcIdentityProvider implements ExternalIdentityProvider, CredentialsSupport {

    private final String name;

    public OidcIdentityProvider(final String name) {
        this.name = name;
    }

    @Override
    public @NotNull
    Set<Class> getCredentialClasses() {
        return ImmutableSet.of(OidcCredentials.class);
    }

    @Override
    public @Nullable
    String getUserId(@NotNull final Credentials credentials) {
        if (credentials instanceof OidcCredentials) {
            return ((OidcCredentials) credentials).getSubject();
        }
        return null;
    }

    @Override
    public @NotNull
    Map<String, ?> getAttributes(@NotNull final Credentials credentials) {
        if (credentials instanceof OidcCredentials) {
            return ((OidcCredentials) credentials).getAttributes();
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean setAttributes(@NotNull final Credentials credentials, @NotNull final Map<String, ?> attributes) {
        if (credentials instanceof OidcCredentials) {
            final OidcCredentials creds = (OidcCredentials) credentials;
            for (final Map.Entry<String, ?> attr : attributes.entrySet()) {
                creds.setAttribute(attr.getKey(), attr.getValue());
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull
    String getName() {
        return this.name;
    }

    @Override
    public @Nullable
    ExternalIdentity getIdentity(@NotNull final ExternalIdentityRef ref) throws ExternalIdentityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable
    ExternalUser getUser(@NotNull final String s) throws ExternalIdentityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable
    ExternalUser authenticate(@NotNull final Credentials credentials) throws ExternalIdentityException, LoginException {
        if (credentials instanceof OidcCredentials && this.accepts((OidcCredentials) credentials)) {
            return new OidcUser((OidcCredentials) credentials, this);
        }
        return null;
    }

    @Override
    public @Nullable
    ExternalGroup getGroup(@NotNull final String s) throws ExternalIdentityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull
    Iterator<ExternalUser> listUsers() throws ExternalIdentityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull
    Iterator<ExternalGroup> listGroups() throws ExternalIdentityException {
        throw new UnsupportedOperationException();
    }

    private boolean accepts(@NotNull final OidcCredentials credentials) {
        return StringUtils.equals(this.getName(), credentials.getIdpName());
    }

    public static class OidcUser implements ExternalUser {

        private final OidcCredentials credentials;
        private final OidcIdentityProvider identityProvider;

        private final Map<String, Object> attributes = new HashMap<>();

        public OidcUser(final OidcCredentials credentials, final OidcIdentityProvider identityProvider) {
            this.credentials = credentials;
            this.identityProvider = identityProvider;
            this.attributes.putAll(credentials.getClaims());
        }

        @Override
        public @NotNull
        ExternalIdentityRef getExternalId() {
            return new ExternalIdentityRef(this.credentials.getSubject(), this.identityProvider.getName());
        }

        @Override
        public @NotNull
        String getId() {
            return this.credentials.getSubject();
        }

        @Override
        public @NotNull
        String getPrincipalName() {
            return this.credentials.getSubject();
        }

        @Override
        public @Nullable
        String getIntermediatePath() {
            return null;
        }

        @Override
        public @NotNull
        Iterable<ExternalIdentityRef> getDeclaredGroups() throws ExternalIdentityException {
            return Collections.emptyList();
        }

        @Override
        public @NotNull
        Map<String, ?> getProperties() {
            return this.attributes;
        }

    }

}
