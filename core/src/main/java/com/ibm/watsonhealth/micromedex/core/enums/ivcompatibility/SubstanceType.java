package com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility;

import java.util.HashMap;
import java.util.Map;

public enum SubstanceType {
    DRUG("Drug", "Drug"),
    SOLUTION("Soln", "Solution"),
    BOTH("Drug-Soln", "Both"),
    NOT_SET("", "");

    private final String aemValue;
    private final String displayName;

    private static final Map<String, SubstanceType> allSubstanceTypes = new HashMap<>(values().length - 1);

    static {
        for (final SubstanceType substanceType : values()) {
            allSubstanceTypes.put(substanceType.getAemValue(), substanceType);
        }
        allSubstanceTypes.remove(SubstanceType.NOT_SET.aemValue);
    }

    SubstanceType(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public String getAemValue() {
        return this.aemValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static SubstanceType getSubstanceType(final String aemValue) {
        if (allSubstanceTypes.containsKey(aemValue)) {
            return allSubstanceTypes.get(aemValue);
        }
        return SubstanceType.NOT_SET;
    }
}
