package com.ibm.watsonhealth.micromedex.core.services.impl;

import java.util.Random;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.watsonhealth.micromedex.core.services.RandomIdService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;
import com.ibm.watsonhealth.micromedex.core.utils.QueryUtils;
import com.ibm.watsonhealth.micromedex.core.utils.RunModeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = RandomIdService.class)
public class RandomIdServiceImpl implements RandomIdService {

    private static final Random random = new Random();

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public synchronized int saveAndGetId(final int rangeFrom, final int rangeTo, @NotNull final String rootPath, @NotNull final String propertyName, @NotNull final String resourceType, @NotNull final Resource resource) throws GenerateIdException {
        final String validateUniqueIdQueryString = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, [" + rootPath + "]) AND [s].[" + propertyName + "] = %s AND [s].[" + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY + "] = '" + resourceType + "'";
        return this.saveAndGetId(rangeFrom, rangeTo, propertyName, validateUniqueIdQueryString, resource);
    }

    @Override
    public synchronized int saveAndGetId(final int rangeFrom, final int rangeTo, @NotNull final String propertyName, @NotNull final String validateUniqueIdQueryString, @NotNull final Resource resource) throws GenerateIdException {
        int result;

        if (!RunModeUtils.isAuthor(this.slingSettingsService)) {
            throw new GenerateIdException("Generating IDs is only allowed on author servers.");
        }
        
        try {
            final ResourceResolver resourceResolver = resource.getResourceResolver();
            do {
                result = random.nextInt(rangeTo - rangeFrom) + rangeFrom;
            } while (!this.isUnique(String.format(validateUniqueIdQueryString, result), resourceResolver));
            this.saveId(resource, propertyName, result);
        } catch (final RepositoryException | PersistenceException ex) {
            throw new GenerateIdException(ex);
        }
        return result;
    }

    private void saveId(final @NotNull Resource resource, final String propertyName, final int id) throws PersistenceException {
        final ResourceResolver resourceResolver = resource.getResourceResolver();
        resourceResolver.refresh();
        final ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        if (modifiableValueMap != null) {
            modifiableValueMap.put(propertyName, id);
            resourceResolver.commit();
        }

    }

    private boolean isUnique(@NotNull final String validateUniqueIdQueryString, @NotNull final ResourceResolver resourceResolver) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session == null) {
            return false;
        }

        final NodeIterator queryResultIterator = QueryUtils.getQueryNodes(session, validateUniqueIdQueryString);
        return !queryResultIterator.hasNext();
    }

}
