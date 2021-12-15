package com.ibm.watsonhealth.micromedex.core.services.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.RedBookDataProcessor;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.QueryUtils;

@Component(service = RedBookService.class, immediate = true)
public class RedBookServiceImpl implements RedBookService {

    @Override
    public Optional<RedBookData> getRedBookData(final @NotNull Substance substance) throws RepositoryException {
        Optional<RedBookData> result = Optional.empty();
        if (substance.getSubstanceType() == SubstanceType.DRUG || (substance.getSubstanceType() == SubstanceType.BOTH && substance.getLexiconSubstanceReferenceSource() == SubstanceType.DRUG)) {
            result = this.getRedBookDataByGcrName(substance.getLexiconSubstanceReferences(), substance.getCurrentResource().getResourceResolver());
        } else if (substance.getSubstanceType() == SubstanceType.SOLUTION || (substance.getSubstanceType() == SubstanceType.BOTH && substance.getLexiconSubstanceReferenceSource() == SubstanceType.SOLUTION)) {
            result = this.getRedBookDataBySolutionName(substance, substance.getCurrentResource().getResourceResolver());
        }
        return result;
    }

    @Override
    public Optional<RedBookData> getRedBookDataByGcrName(final @NotNull List<String> productNames, final @NotNull ResourceResolver resourceResolver) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session != null) {
            Collections.sort(productNames, String.CASE_INSENSITIVE_ORDER);
            final List<Resource> gcrResources = new ArrayList<>();
            final String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gcr' and [" + RedBookDataProcessor.PROPERTY_NAME_GCR_NAME_LOWERCASE + "]='" + StringUtils.lowerCase(
              String.join("/", productNames), Locale.getDefault()) + "'";
            final NodeIterator queryResult = QueryUtils.getQueryNodes(session, queryString);
            while (queryResult.hasNext()) {
                final Resource gcrResource = resourceResolver.getResource(queryResult.nextNode().getPath());
                if (gcrResource != null) {
                    gcrResources.add(gcrResource);
                }
            }
            if (!gcrResources.isEmpty()) {
                return Optional.of(new RedBookData(gcrResources, SubstanceType.DRUG));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RedBookData> getRedBookDataBySolutionName(final @NotNull Substance substance, final @NotNull ResourceResolver resourceResolver) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session != null) {
            final List<Long> gfcCodes = this.getSolutionGfcCodes(substance, resourceResolver);
            return this.getRedBookDataBySolutionGfcCodes(gfcCodes, resourceResolver);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RedBookData> getRedBookDataBySolutionPath(final @NotNull String solutionPath, final @NotNull ResourceResolver resourceResolver) throws RepositoryException {
        final Resource solutionResource = resourceResolver.getResource(solutionPath);
        if (solutionResource != null) {
            final ValueMap valueMap = solutionResource.getValueMap();
            if (valueMap.containsKey(SolutionProductLookupDataProcessor.PROPERTY_GFCS)) {
                final Long[] gfcCodes = valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_GFCS, Long[].class);
                return this.getRedBookDataBySolutionGfcCodes(Arrays.asList(gfcCodes), resourceResolver);
            }
        }
        return Optional.empty();
    }

    private Optional<RedBookData> getRedBookDataBySolutionGfcCodes(@NotNull final List<Long> gfcCodes, @NotNull final ResourceResolver resourceResolver) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session != null) {
            final List<Resource> gfcResources = new ArrayList<>();
            for (final Long gfcCode : gfcCodes) {
                gfcResources.addAll(this.getGfcResources(gfcCode, resourceResolver, session));
            }
            if (!gfcResources.isEmpty()) {
                return Optional.of(new RedBookData(gfcResources, SubstanceType.SOLUTION));
            }
        }
        return Optional.empty();
    }

    private List<Long> getSolutionGfcCodes(@NotNull final Substance substance, @NotNull final ResourceResolver resourceResolver) {

        final List<Long> result = new ArrayList<>();
        Resource solutionResource;
        ValueMap valueMap;
        for (final String solutionPath : substance.getLexiconSubstanceReferencePaths()) {
            solutionResource = resourceResolver.getResource(solutionPath);
            if (solutionResource != null) {
                valueMap = solutionResource.getValueMap();
                if (valueMap.containsKey(SolutionProductLookupDataProcessor.PROPERTY_GFCS)) {
                    result.addAll(Arrays.asList(valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_GFCS, Long[].class)));
                }
            }
        }
        return result;
    }

    private List<Resource> getGfcResources(@NotNull final Long gfcCode, final @NotNull ResourceResolver resourceResolver, @NotNull final Session session) throws RepositoryException {
        final List<Resource> result = new ArrayList<>();
        final String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=" + gfcCode;
        final NodeIterator queryResult = QueryUtils.getQueryNodes(session, queryString);
        while (queryResult.hasNext()) {
            final Resource gfcResource = resourceResolver.getResource(queryResult.nextNode().getPath());
            if (gfcResource != null) {
                result.add(gfcResource);
            }
        }
        return result;
    }

}
