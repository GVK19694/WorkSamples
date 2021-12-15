package com.ibm.watsonhealth.micromedex.core.services.imports.api;

import org.apache.sling.api.resource.ResourceResolver;

public interface DataProcessor {

    String UPLOAD_PATH_PROPERTY_NAME = "uploadPath";

    void doProcess(final ResourceResolver resourceResolver);

    String getDataRootPath();

    String getImportPath();

}
