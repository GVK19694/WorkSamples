package com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility;

import java.util.HashMap;
import java.util.Map;

public enum CombinationType {
    DRUG_SOLUTION("Soln", "Drug-Solution"),
    Y_SITE("Ysite", "Y-Site"),
    ADMIXTURE("Admix", "Admixture"),
    SYRINGE("Syr", "Syringe"),
    NOT_SET("", "");

    private final String aemValue;
    private final String displayName;

    private static final Map<String, CombinationType> allCombinationTypes = new HashMap<>(values().length - 1);

    static {
        for (final CombinationType combinationType : values()) {
            allCombinationTypes.put(combinationType.getAemValue(), combinationType);
        }
        allCombinationTypes.remove(CompatibilityType.NOT_SET.getAemValue());
    }

    CombinationType(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public String getAemValue() {
        return this.aemValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static CombinationType getCombinationType(final String aemValue) {
        if (allCombinationTypes.containsKey(aemValue)) {
            return allCombinationTypes.get(aemValue);
        }
        return CombinationType.NOT_SET;
    }
}
