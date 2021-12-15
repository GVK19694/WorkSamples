package com.ibm.watsonhealth.micromedex.core.constants;

public final class ServletConstants {

    private ServletConstants() {
    }

    @SuppressWarnings("java:S1075")
    public static final String CACHEABLE_SERVLETS_PATH = "/cachableservlets/"; // everything in /cachableservlets/.. is public available and will be cached by dispatcher/varnish/akamai/... (you have to configured them the right way to do this, this will not happen out of the box!)

    @SuppressWarnings("java:S1075")
    public static final String DEFAULT_SERVLETS_PATH = "/servlets/"; // everything in /servlets/.. is public available and will NOT be cached by dispatcher/varnish/akamai/... (you have to configured them the right way to do this, this will not happen out of the box!)

    @SuppressWarnings("java:S1075")
    public static final String SECURE_SERVLETS_PATH = "/secureservlets/"; // everything in /secureservlets/.. is NOT public available. access is restricted by dispatcher/varnish/akamai/... (you have to configured them the right way to do this, this will not happen out of the box!)

    public static final String IMPORT_API_PATH = DEFAULT_SERVLETS_PATH + "upload-api/";

}
