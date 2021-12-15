package com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum DrugSource {
    EXISTING_PRODUCT_MONOGRAPH("monograph", "Choose existing Product Monograph"),
    NEW("new", "Create New");

    @Getter
    private final String aemValue;

    @Getter
    private final String displayName;

    private static final Map<String, DrugSource> allDrugSources = new HashMap<>(values().length - 1);

    static {
        for (final DrugSource drugSource : values()) {
            allDrugSources.put(drugSource.getAemValue(), drugSource);
        }
    }

    DrugSource(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public static DrugSource getDrugSource(final String aemValue) {
        if (allDrugSources.containsKey(aemValue)) {
            return allDrugSources.get(aemValue);
        }
        return DrugSource.EXISTING_PRODUCT_MONOGRAPH;
    }
}
