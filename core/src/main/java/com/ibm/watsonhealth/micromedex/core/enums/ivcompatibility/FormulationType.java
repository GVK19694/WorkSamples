package com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility;

import java.util.HashMap;
import java.util.Map;

public enum FormulationType {
    SINGLE_SUBSTANCE("Single-Substance", "Single Substance"),
    COMBINATION_SUBSTANCE("Combination-Substance", "Combination Substance"),
    GROUPING_NAME("Grouping-Name", "Grouping Name"),
    NOT_SET("", "");

    private final String aemValue;
    private final String displayName;

    private static final Map<String, FormulationType> allFormulationTypes = new HashMap<>(values().length - 1);

    static {
        for (final FormulationType formulationType : values()) {
            allFormulationTypes.put(formulationType.getAemValue(), formulationType);
        }
        allFormulationTypes.remove(FormulationType.NOT_SET.aemValue);
    }

    FormulationType(final String aemValue, final String displayName) {
        this.aemValue = aemValue;
        this.displayName = displayName;
    }

    public String getAemValue() {
        return this.aemValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static FormulationType getFormulationType(final String aemValue) {
        if (allFormulationTypes.containsKey(aemValue)) {
            return allFormulationTypes.get(aemValue);
        }
        return FormulationType.NOT_SET;
    }
}
