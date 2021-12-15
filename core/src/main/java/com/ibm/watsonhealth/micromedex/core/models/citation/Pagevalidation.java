package com.ibm.watsonhealth.micromedex.core.models.citation;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@ConsumerType
public interface Pagevalidation extends ValidationModel {

    String getTitle();

}
