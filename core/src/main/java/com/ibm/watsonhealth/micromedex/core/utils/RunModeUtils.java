package com.ibm.watsonhealth.micromedex.core.utils;

import java.util.Set;

import org.apache.sling.settings.SlingSettingsService;

public final class RunModeUtils {

    private RunModeUtils() {
    }

    public static boolean isAuthor(final SlingSettingsService settings) {
        return isRunMode("author", settings);
    }

    public static boolean isPublish(final SlingSettingsService settings) {
        return isRunMode("publish", settings);
    }

    public static boolean isRunMode(final String runMode, final SlingSettingsService settings) {
        final Set<String> runModes = settings.getRunModes();
        return runModes.contains(runMode);
    }

}
