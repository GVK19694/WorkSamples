package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CompatibilityType;

@ConsumerType
public interface Compatibility extends Solution {

    long getRecordId();

    String getCompatible();

    CompatibilityType getCompatibleEnum();

    String getPhysicalCompatibility();

    String getChemicalStability();

    String getCombinationType();

    CombinationType getCombinationTypeEnum();

    String getRecordNotes();

    List<SecondaryDrug> getSecondaryDrugs();

}
