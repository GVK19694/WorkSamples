package com.ibm.watsonhealth.micromedex.core.context.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;

import io.wcm.testing.mock.aem.junit5.AemContext;

public final class TestUtils {

    private TestUtils() {}

    public static void loadProjectTemplates(final AemContext context) {
        final Map<String, Object> params = Collections.singletonMap(JcrConstants.JCR_PRIMARYTYPE, NameConstants.NT_TEMPLATE);

        context
          .build()
          .resource(TemplateConstants.TEMPLATE_ROOT_PATH, Collections.singletonMap(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED));
        context.build().resource(TemplateConstants.API_ROOT, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_ROOT, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_RECORDS_ROOT, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_RECORD_STRUCTURE, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_RECORD, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_PRODUCTS_ROOT, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_PRODUCT_STRUCTURE, params);
        context.build().resource(TemplateConstants.IV_COMPATIBILITY_PRODUCT, params);
        context.build().resource(TemplateConstants.CITATIONS_ROOT, params);
        context.build().resource(TemplateConstants.CITATION_STRUCTURE, params);
        context.build().resource(TemplateConstants.CITATION, params);

        context.build().commit();
    }

    public static String getExpectedResult(final Class clazz, final String filename) throws IOException {
        return IOUtils.toString(clazz.getResource(filename), StandardCharsets.UTF_8);
    }

}
