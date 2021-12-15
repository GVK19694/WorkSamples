package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@ConsumerType
public interface Solution extends ValidationModel {

    boolean isSolutionEnabled();

    String getSolutionPath();

    String getSolutionName();

    String getSolutionManufacturer();

    String getSolutionTradename();

    String getSolutionVolume();

}
