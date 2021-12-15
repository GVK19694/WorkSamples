package com.ibm.watsonhealth.micromedex.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.services.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = ValidationService.class, immediate = true)
public class ValidationServiceImpl implements ValidationService {

    public static final String CONTAINER_NODE_NAME = "container";
    public static final String ROOT_NODE_NAME = "root";

    @Override
    public List<Resource> searchForChildResources(@NotNull final Resource resource) {
        final List<Resource> result = new ArrayList<>();
        if (StringUtils.equals(ResourceTypeConstants.PAGEVALIDATION, resource.getResourceType()) || StringUtils.equals(
          ResourceTypeConstants.PAGEVALIDATION_CITATION, resource.getResourceType())) {
            result.addAll(this.getChildResources(resource.getParent().getChild(CONTAINER_NODE_NAME)));
        } else if (StringUtils.equals(ResourceTypeConstants.IV_COMPATIBILITY_PAGE, resource.getResourceType()) || StringUtils.equals(
          ResourceTypeConstants.CITATION_PAGE, resource.getResourceType())) {
            result.addAll(this.getChildResources(resource.getChild(ROOT_NODE_NAME).getChild(CONTAINER_NODE_NAME)));
        } else {
            final Resource subcontainerResource = resource.getChild(CONTAINER_NODE_NAME);
            if (subcontainerResource != null) {
                result.addAll(this.getChildResources(subcontainerResource));
            }
        }
        return result;
    }

    @Override
    public List<ValidationModel> validate(@NotNull final Resource resource) {
        final List<ValidationModel> result = new ArrayList<>();

        final List<Resource> resourcesToValidate;
        if (StringUtils.equals(ResourceTypeConstants.PAGEVALIDATION, resource.getResourceType()) || StringUtils.equals(
          ResourceTypeConstants.PAGEVALIDATION_CITATION, resource.getResourceType())) {
            resourcesToValidate = this.searchForChildResources(resource);
        } else {
            resourcesToValidate = new ArrayList<>();
            resourcesToValidate.add(resource);
        }

        for (final Resource resourceToValidate : resourcesToValidate) {
            result.add(resourceToValidate.adaptTo(ValidationModel.class));
        }
        return result;
    }

    private List<Resource> getChildResources(@NotNull final Resource resource) {
        return IteratorUtils.toList(resource.listChildren());
    }

}
