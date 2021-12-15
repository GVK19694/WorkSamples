package com.ibm.watsonhealth.micromedex.core.enums.citation;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum CitationType {
    PRODUCT_INFO("product-info", "Product Info"),
    GENERIC("generic", "Generic"),
    BOOK("book", "Book"),
    JOURNAL("journal", "Journal"),
    ELECTRONIC("electronic", "Electronic");

    @Getter
    private final String aemValue;

    @Getter
    private final String displayName;

    private static final Map<String, CitationType> allCitationTypes = new HashMap<>(values().length - 1);

    static {
        for (final CitationType citationType : values()) {
            allCitationTypes.put(citationType.getAemValue(), citationType);
        }
    }

    CitationType(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public static CitationType getCitationType(String aemValue) {
        if (allCitationTypes.containsKey(aemValue)) {
            return allCitationTypes.get(aemValue);
        }
        throw new IllegalArgumentException("invalid aem value '" + aemValue + "'");
    }

}
