package com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility;

import java.util.HashMap;
import java.util.Map;

public enum CompatibilityType {
    COMPATIBLE("C", "Compatible"),
    INCOMPATIBLE("I", "Incompatible"),
    UNCERTAIN("U", "Uncertain"),
    NOT_SET("", "");

    private final String aemValue;
    private final String displayName;

    private static final Map<String, CompatibilityType> allCompatibilityTypes = new HashMap<>(values().length - 1);

    static {
        for (final CompatibilityType combinationType : values()) {
            allCompatibilityTypes.put(combinationType.getAemValue(), combinationType);
        }
        allCompatibilityTypes.remove(CompatibilityType.NOT_SET.aemValue);
    }

    CompatibilityType(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public String getAemValue() {
        return this.aemValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static CompatibilityType getCompatibilityType(final String aemValue) {
        if (allCompatibilityTypes.containsKey(aemValue)) {
            return allCompatibilityTypes.get(aemValue);
        }
        return CompatibilityType.NOT_SET;
    }
}
