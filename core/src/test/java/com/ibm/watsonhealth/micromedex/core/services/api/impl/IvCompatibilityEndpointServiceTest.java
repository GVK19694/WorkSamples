package com.ibm.watsonhealth.micromedex.core.services.api.impl;

import java.io.IOException;
import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ibm.watsonhealth.micromedex.core.context.AppAemContextBuilder;
import com.ibm.watsonhealth.micromedex.core.context.mocks.MockRedBookService;
import com.ibm.watsonhealth.micromedex.core.context.utils.TestUtils;
import com.ibm.watsonhealth.micromedex.core.context.utils.XmlUtils;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.ResponseData;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.GetDataException;
import com.ibm.watsonhealth.micromedex.core.services.impl.ValidationServiceImpl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class IvCompatibilityEndpointServiceTest {

    private static final AemContext context = new AppAemContextBuilder(ResourceResolverType.JCR_MOCK)
      .loadResources("jcr_root")
      .registerService(RedBookService.class, new MockRedBookService())
      .registerInjectActivateService(new ValidationServiceImpl())
      .loadSlingModels("com.ibm.watsonhealth.micromedex.core.models")
      .build();

    private static final MockRequestPathInfo requestPathInfo = context.requestPathInfo();

    private IvCompatibilityEndpointService serviceUnderTest;

    @BeforeAll
    static void beforeAll() {
        TestUtils.loadProjectTemplates(context);
    }

    @BeforeEach
    void beforeEach() {
        this.serviceUnderTest = context.registerInjectActivateService(new IvCompatibilityEndpointService());
        context.response().resetBuffer();
    }

    @Test
    @DisplayName("GIVEN valid and invalid iv compatibility records WHEN requesting data for the iv compatibility records root THEN it returns valid xml response")
    void assignValidRecordsDataToResponse(final AemContext context) throws IOException, CantApplyToResponseException, GetDataException {
        requestPathInfo.setExtension("xml");
        requestPathInfo.setSelectorString("cem-api");
        final Resource resource = context.resourceResolver().getResource("/content/mdx-cem/iv-compatibility/records/jcr:content");
        context.request().setResource(resource);
        final Optional<ResponseData> responseData = this.serviceUnderTest.getData(context.request());
        assertTrue(responseData.isPresent());
        this.serviceUnderTest.applyToResponse(responseData.get(), context.response());

        final String expectedResult = XmlUtils.format(
          TestUtils.getExpectedResult(IvCompatibilityEndpointServiceTest.class, "IvCompatibilityResult1.xml"));
        final String result = XmlUtils.format(context.response().getOutputAsString());
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("GIVEN valid and invalid iv compatibility records WHEN requesting data for the iv compatibility root not THEN it returns an empty object because of an invalid template")
    void assignRecordDataToResponseWithWrongTemplate(final AemContext context) throws GetDataException {
        requestPathInfo.setExtension("xml");
        requestPathInfo.setSelectorString("cem-api");
        final Resource resource = context.resourceResolver().getResource("/content/mdx-cem/iv-compatibility/jcr:content");
        context.request().setResource(resource);
        final Optional<ResponseData> responseData = this.serviceUnderTest.getData(context.request());
        assertFalse(responseData.isPresent());
    }

    @Test
    @DisplayName("GIVEN valid and invalid iv compatibility product monographs WHEN requesting data for the iv compatibility product monographs root THEN it returns valid xml response")
    void assignValidProductMonographsDataToResponse(final AemContext context) throws IOException, CantApplyToResponseException, GetDataException {
        requestPathInfo.setExtension("xml");
        requestPathInfo.setSelectorString("cem-api");
        final Resource resource = context.resourceResolver().getResource("/content/mdx-cem/iv-compatibility/product-monographs/jcr:content");
        context.request().setResource(resource);
        final Optional<ResponseData> responseData = this.serviceUnderTest.getData(context.request());
        assertTrue(responseData.isPresent());
        this.serviceUnderTest.applyToResponse(responseData.get(), context.response());

        final String expectedResult = XmlUtils.format(
          TestUtils.getExpectedResult(IvCompatibilityEndpointServiceTest.class, "ProductMonographsResult1.xml"));
        final String result = XmlUtils.format(context.response().getOutputAsString());
        assertEquals(expectedResult, result);
    }

}
