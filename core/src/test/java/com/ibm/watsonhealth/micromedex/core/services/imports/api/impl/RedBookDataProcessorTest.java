package com.ibm.watsonhealth.micromedex.core.services.imports.api.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ibm.watsonhealth.micromedex.core.context.AppAemContextBuilder;
import com.ibm.watsonhealth.micromedex.core.services.impl.PreReplicationQueueServiceImpl;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.DataProcessor;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.utils.ImportApiUtils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class RedBookDataProcessorTest {

    private AemContext context;

    private DataProcessor serviceUnderTest;

    AemContext createAemContext() {
        return new AppAemContextBuilder(ResourceResolverType.JCR_MOCK)
          .loadResources("jcr_root")
          .registerService(ResourceResolverFactory.class, new MockResourceResolverFactory())
          .registerInjectActivateService(new PreReplicationQueueServiceImpl())
          .registerInjectActivateService(new RedBookDataProcessor())
          .build();
    }

    @BeforeEach
    void beforeEach() throws PersistenceException {
        this.context = this.createAemContext();
        final Resource resource = this.context.resourceResolver().getResource("/etc/import-data/redbook");
        if (resource != null) {
            this.context.resourceResolver().delete(resource);
            this.context.resourceResolver().commit();
        }
        this.serviceUnderTest = this.context.getService(DataProcessor.class);
    }

    @Test
    @DisplayName("GIVEN data in upload folder WHEN running the import process THEN valid data in the import-data folder are available")
    void testProcess() {
        this.serviceUnderTest.doProcess(this.context.resourceResolver());
        final Resource rootResource = this.context.resourceResolver().getResource(RedBookDataProcessor.DATA_ROOT_PATH);
        final List<Resource> importedItemResources = new ArrayList<>();
        rootResource.listChildren().forEachRemaining(importedItemResources::add);

        assertEquals(1, importedItemResources.size());
        assertEquals("0920000", importedItemResources.get(0).getName());

        final Iterator<Resource> level2Iterator = importedItemResources.get(0).listChildren();
        assertTrue(level2Iterator.hasNext());
        final Resource level2Resource = level2Iterator.next();
        assertEquals("0928400", level2Resource.getName());

        final Iterator<Resource> level3Iterator = level2Resource.listChildren();
        assertTrue(level3Iterator.hasNext());
        final Resource level3Resource = level3Iterator.next();
        assertEquals("0928482", level3Resource.getName());

        ValueMap valueMap = level3Resource.getValueMap();
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GCR_CODE));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GCR_NAME));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_MODIFIED_COUNTER));
        assertEquals(928482, valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GCR_CODE, Long.class));
        assertEquals("ABATACEPT", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GCR_NAME, String.class));

        assertEquals(2, this.countChildResources(level3Resource));

        final Iterator<Resource> level4Iterator = level3Resource.listChildren();
        assertTrue(level4Iterator.hasNext());
        final Resource level4Resource = level4Iterator.next();
        assertEquals("129148", level4Resource.getName());

        valueMap = level4Resource.getValueMap();
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GFC_CODE));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GFC_ROA_CODE));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GFC_FORM));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GFC_STRENGTH));
        assertEquals(129148, valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_CODE, Long.class));
        assertEquals("IV", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_ROA_CODE, String.class));
        assertEquals("PDS", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_FORM, String.class));
        assertEquals("125 MG/1 ML", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_STRENGTH, String.class));

        assertEquals(3, this.countChildResources(level4Resource));

        final Iterator<Resource> level5Iterator = level4Resource.listChildren();
        assertTrue(level5Iterator.hasNext());
        final Resource level5Resource = level5Iterator.next();
        assertEquals("00003-2188-31", level5Resource.getName());

        valueMap = level5Resource.getValueMap();
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NDC_CODE));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NAME));
        assertTrue(valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_CATEGORY));
        assertEquals("00003-2188-31", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NDC_CODE, String.class));
        assertEquals("ORENCIA PRE-FILLED SYRINGE", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NAME, String.class));
        assertEquals("Rx-only/Trade or Brand Name", valueMap.get(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_CATEGORY, String.class));
    }

    @Test
    @DisplayName("GIVEN no data in upload folder WHEN running the import process THEN no data in the import-data folder are available")
    void testProcessWithoutData() throws PersistenceException {
        final Resource redBookUploadRootResource = this.getRedbookRootResource();
        final List<Resource> redBookUploads = new ArrayList<>();
        redBookUploadRootResource.listChildren().forEachRemaining(redBookUploads::add);
        for (final Resource redBookUpload : redBookUploads) {
            this.context.resourceResolver().delete(redBookUpload);
        }

        this.serviceUnderTest.doProcess(this.context.resourceResolver());
        final Resource rootResource = this.context.resourceResolver().getResource(RedBookDataProcessor.DATA_ROOT_PATH);
        assertNull(rootResource);
    }

    @Test
    @DisplayName("GIVEN start flag in upload folder is set WHEN running the import process THEN no data in the import-data folder are available")
    void testProcessWithStartflag() {
        final Resource redBookUploadRootResource = this.getRedbookRootResource();
        this.setStartFlag(redBookUploadRootResource);

        this.serviceUnderTest.doProcess(this.context.resourceResolver());
        final Resource rootResource = this.context.resourceResolver().getResource(RedBookDataProcessor.DATA_ROOT_PATH);
        assertNull(rootResource);
    }

    @NotNull
    private Resource getRedbookRootResource() {
        final Resource result = this.context.resourceResolver().getResource("/etc/upload/redbook");
        if (result == null) {
            throw new NullPointerException("redbook root node is null");
        }
        return result;
    }

    private void setStartFlag(final Resource redbookRootResource) {
        if (redbookRootResource != null) {
            final ModifiableValueMap valueMap = redbookRootResource.adaptTo(ModifiableValueMap.class);
            if (valueMap != null) {
                valueMap.put(ImportApiUtils.UPLOAD_TYPE_STATUS_PROPERTY_NAME, UploadTypeStatusEnum.START.name());
            }
        }
    }

    private int countChildResources(final Resource resource) {
        final Iterator<Resource> iterator = resource.listChildren();
        return IteratorUtils.size(iterator);
    }

}
