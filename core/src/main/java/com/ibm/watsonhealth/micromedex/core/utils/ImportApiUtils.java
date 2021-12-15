package com.ibm.watsonhealth.micromedex.core.utils;

import java.util.Optional;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadedItemStatusEnum;

public final class ImportApiUtils {

    public static final String UPLOAD_TYPE_STATUS_PROPERTY_NAME = "status-uploadType";
    public static final String UPLOADED_ITEM_STATUS_PROPERTY_NAME = "status-uploadedItem";

    private ImportApiUtils() {
    }

    public static void setUploadTypeStatusFlag(final UploadTypeStatusEnum status, final String path, final ResourceResolver resourceResolver) throws PersistenceException {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
            if (valueMap != null) {
                if (status == null) {
                    valueMap.remove(UPLOAD_TYPE_STATUS_PROPERTY_NAME);
                } else {
                    valueMap.put(UPLOAD_TYPE_STATUS_PROPERTY_NAME, status.name());
                }
            }
            resourceResolver.commit();
        }
    }

    public static Optional<UploadTypeStatusEnum> getUploadTypeStatusFlag(final String path, final ResourceResolver resourceResolver) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            final ValueMap valueMap = resource.getValueMap();
            if (valueMap.containsKey(UPLOAD_TYPE_STATUS_PROPERTY_NAME)) {
                return Optional.of(UploadTypeStatusEnum.valueOf(valueMap.get(UPLOAD_TYPE_STATUS_PROPERTY_NAME, String.class)));
            }
        }
        return Optional.empty();
    }

    public static void setUploadItemStatusFlag(final UploadedItemStatusEnum status, final ModifiableValueMap modifiableValueMap) {
        if (status == null) {
            modifiableValueMap.remove(UPLOADED_ITEM_STATUS_PROPERTY_NAME);
        } else {
            modifiableValueMap.put(UPLOADED_ITEM_STATUS_PROPERTY_NAME, status.name());
        }
    }

    public static Optional<UploadedItemStatusEnum> getUploadItemStatusFlag(final String path, final ResourceResolver resourceResolver) {
        final Resource resource = resourceResolver.getResource(path);
        if (resource == null) {
            return Optional.empty();
        }
        return getUploadItemStatusFlag(resource);
    }

    public static Optional<UploadedItemStatusEnum> getUploadItemStatusFlag(final Resource resource) {
        final ValueMap valueMap = resource.getValueMap();
        if (valueMap.containsKey(UPLOADED_ITEM_STATUS_PROPERTY_NAME)) {
            return Optional.of(UploadedItemStatusEnum.valueOf(valueMap.get(UPLOADED_ITEM_STATUS_PROPERTY_NAME, String.class)));
        }
        return Optional.empty();
    }

}
