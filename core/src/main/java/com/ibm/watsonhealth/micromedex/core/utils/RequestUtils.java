package com.ibm.watsonhealth.micromedex.core.utils;

import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.utils.exceptions.SessionNotAvailableException;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static Session getSession(@NotNull final SlingHttpServletRequest request) throws SessionNotAvailableException {
        return ResourceResolverUtils.getSession(request.getResourceResolver());
    }

}
