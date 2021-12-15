package com.ibm.watsonhealth.micromedex.core.services;

import java.util.List;
import java.util.Optional;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData;

public interface RedBookService {

    Optional<RedBookData> getRedBookData(final @NotNull Substance substance) throws RepositoryException;

    Optional<RedBookData> getRedBookDataByGcrName(@NotNull final List<String> productNames, @NotNull final ResourceResolver resourceResolver) throws RepositoryException;

    Optional<RedBookData> getRedBookDataBySolutionName(@NotNull final Substance substance, @NotNull final ResourceResolver resourceResolver) throws RepositoryException;

    Optional<RedBookData> getRedBookDataBySolutionPath(@NotNull final String solutionPath, @NotNull final ResourceResolver resourceResolver) throws RepositoryException;

}
