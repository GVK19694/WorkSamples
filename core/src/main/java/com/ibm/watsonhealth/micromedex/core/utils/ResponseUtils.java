package com.ibm.watsonhealth.micromedex.core.utils;

import java.nio.charset.Charset;

import org.apache.sling.api.SlingHttpServletResponse;

public final class ResponseUtils {

    private ResponseUtils() {
    }

    public static void disableDispatcherAndBrowserCache(final SlingHttpServletResponse response) {
        disableBrowserCache(response);
        disableDispatcherCache(response);
    }

    public static void disableBrowserCache(final SlingHttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    public static void disableDispatcherCache(final SlingHttpServletResponse response) {
        response.setHeader("Dispatcher", "no-cache");
    }

    public static void setBrowserCachingInDays(final SlingHttpServletResponse response, final int cachingTimeInDays) {
        setBrowserCachingInHours(response, cachingTimeInDays * 24);
    }

    public static void setBrowserCachingInHours(final SlingHttpServletResponse response, final int cachingTimeInHours) {
        setBrowserCachingInMinutes(response, cachingTimeInHours * 60);
    }

    public static void setBrowserCachingInMinutes(final SlingHttpServletResponse response, final int cachingTimeInMinutes) {
        setBrowserCaching(response, cachingTimeInMinutes * 60);
    }

    public static void setBrowserCaching(final SlingHttpServletResponse response, final int cachingTimeInSeconds) {
        response.setHeader("Cache-Control", String.format("public, max-age=%s", cachingTimeInSeconds));
    }

    public static void setContentTypeJson(final SlingHttpServletResponse response, final Charset encoding) {
        response.setContentType("application/json;charset=" + encoding.name());
    }

    public static void setContentTypeHtml(final SlingHttpServletResponse response, final Charset encoding) {
        response.setContentType("text/html;charset=" + encoding.name());
    }

    public static void setContentTypeText(final SlingHttpServletResponse response, final Charset encoding) {
        response.setContentType("text/plain;charset=" + encoding.name());
    }

    public static void setContentTypeXml(final SlingHttpServletResponse response, final Charset encoding) {
        response.setContentType("text/xml;charset=" + encoding.name());
    }

}
