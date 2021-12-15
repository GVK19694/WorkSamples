package com.ibm.watsonhealth.micromedex.core.utils;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.utils.exceptions.SessionNotAvailableException;

public final class ResourceResolverUtils {

    private ResourceResolverUtils() {
    }

    public static ResourceResolver getServiceResourceResolver(final ResourceResolverFactory resourceResolverFactory, final String subservice) throws LoginException {
        final Map<String, Object> param = new HashMap<>();
        param.put("sling.service.subservice", subservice);
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

    public static void close(final ResourceResolver serviceResourceResolver) {
        if (serviceResourceResolver != null && serviceResourceResolver.isLive()) {
            serviceResourceResolver.close();
        }

    }

    public static Session getSession(@NotNull final ResourceResolver resourceResolver) throws SessionNotAvailableException {
        final Session result = resourceResolver.adaptTo(Session.class);
        if (result == null) {
            throw new SessionNotAvailableException();
        }
        return result;
    }

}
