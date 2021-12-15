package com.ibm.watsonhealth.micromedex.core.services;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;

public interface RandomIdService {

    int saveAndGetId(final int rangeFrom, final int rangeTo, @NotNull final String rootPath, @NotNull final String propertyName, @NotNull final String resourceType, @NotNull final Resource resource) throws GenerateIdException;

    int saveAndGetId(final int rangeFrom, final int rangeTo, @NotNull final String propertyName, @NotNull final String validateUniqueIdQueryString, @NotNull final Resource resource) throws GenerateIdException;

}
