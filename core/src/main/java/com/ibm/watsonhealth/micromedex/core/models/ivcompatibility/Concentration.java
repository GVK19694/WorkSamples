package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@ConsumerType
public interface Concentration extends ValidationModel {

    String getConcentration();

    String getStudyNotes();

    List<Compatibility> getCompatibilities();

}
