package com.ibm.watsonhealth.micromedex.core.auth;

import org.apache.commons.lang3.StringUtils;

public enum Reason {

    INVALID_OIDC_SECURITY_TOKEN("Invalid OIDC Security Token"),
    REQUESTING_OIDC_TOKEN_FAILED("Failed requesting OIDC ID Token"),
    INVALID_AUTHORIZATION("Invalid Authorization");

    public final String text;

    Reason(final String text) {
        this.text = text;
    }

    public static boolean contains(final String name) {
        for (final Reason reason : Reason.values()) {
            if (StringUtils.equals(name, reason.name())) {
                return true;
            }
        }
        return false;
    }
}
