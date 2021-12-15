package com.ibm.watsonhealth.micromedex.core.services;

import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

public interface ValidationService {

    List<Resource> searchForChildResources(final Resource resource);

    List<ValidationModel> validate(final Resource resource);

}
