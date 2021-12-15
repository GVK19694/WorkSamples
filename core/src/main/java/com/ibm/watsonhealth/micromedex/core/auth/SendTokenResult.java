package com.ibm.watsonhealth.micromedex.core.auth;

import org.apache.sling.auth.core.spi.AuthenticationInfo;

public class SendTokenResult {

    private final AuthenticationInfo authenticationInfo;
    private final String failureReson;

    public SendTokenResult(final AuthenticationInfo authenticationInfo, final String failureReson) {
        this.authenticationInfo = authenticationInfo;
        this.failureReson = failureReson;
    }

    public AuthenticationInfo getAuthenticationInfo() {
        return this.authenticationInfo;
    }

    public String getFailureReson() {
        return this.failureReson;
    }

}
