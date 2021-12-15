package com.ibm.watsonhealth.micromedex.core.services.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import com.ibm.watsonhealth.micromedex.core.context.AppAemContextBuilder;
import com.ibm.watsonhealth.micromedex.core.context.utils.TestUtils;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.Gfc;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData;
import com.ibm.watsonhealth.micromedex.core.services.impl.ValidationServiceImpl;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.RedBookDataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.QueryUtils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class RedBookServiceImplTest {

    private static final AemContext context = new AppAemContextBuilder(ResourceResolverType.JCR_MOCK)
      .loadResources("jcr_root")
      .registerInjectActivateService(new ValidationServiceImpl())
      .loadSlingModels("com.ibm.watsonhealth.micromedex.core.models")
      .build();

    private RedBookServiceImpl serviceUnderTest;

    @Mock
    private NodeIterator nodeIterator1;

    @Mock
    private NodeIterator nodeIterator2;

    @Mock
    private NodeIterator nodeIterator3;

    @BeforeAll
    static void beforeAll() {
        TestUtils.loadProjectTemplates(context);
    }

    @BeforeEach
    void beforeEach() {
        this.serviceUnderTest = context.registerInjectActivateService(new RedBookServiceImpl());
    }

    @Test
    void getRedBookData(final AemContext context) throws RepositoryException {
        try (final MockedStatic<QueryUtils> queryUtils = Mockito.mockStatic(QueryUtils.class)) {
            final Session session = context.resourceResolver().adaptTo(Session.class);
            if (session != null) {
                String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=112912";
                List<String> resultPaths = Arrays.asList("/etc/import-data/redbook/0920000/0924600/0924649/112912");
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator1, resultPaths);

                queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=114451";
                resultPaths = new ArrayList<>();
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator2, resultPaths);
            }

            final Resource substanceResource = context
              .resourceResolver()
              .getResource("/content/mdx-cem/iv-compatibility/product-monographs/unit-tests/product-1/jcr:content/root/container/substance");
            final Substance substance = substanceResource.adaptTo(Substance.class);
            final Optional<RedBookData> result = this.serviceUnderTest.getRedBookData(substance);

            assertTrue(result.isPresent());
            assertNotNull(result.get().getGfcs());
            assertNotNull(result.get().getGfcs().getGfcList());
            assertEquals(1, result.get().getGfcs().getGfcList().size());
            final Gfc gfc = result.get().getGfcs().getGfcList().get(0);
            assertEquals(112912, gfc.getCode());
            assertNotNull(gfc.getProducts());
            assertNotNull(gfc.getProducts().getNdc());
            assertEquals(14, gfc.getProducts().getNdc().size());
            assertEquals("00074-7929-03", gfc.getProducts().getNdc().get(0));
        }
    }

    @Test
    void getRedBookDataByGcrName(final AemContext context) throws RepositoryException {
        try (final MockedStatic<QueryUtils> queryUtils = Mockito.mockStatic(QueryUtils.class)) {
            final Session session = context.resourceResolver().adaptTo(Session.class);
            if (session != null) {
                final String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gcr' and [" + RedBookDataProcessor.PROPERTY_NAME_GCR_NAME_LOWERCASE + "]='fat emulsion/fish oil/soybean oil'";
                final List<String> resultPaths = Arrays.asList("/etc/import-data/redbook/0930000/0933200/0933222");
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator1, resultPaths);
            }

            final List<String> gcrNames = Arrays.asList("Fat Emulsion", "Fish Oil", "Soybean Oil");
            final Optional<RedBookData> result = this.serviceUnderTest.getRedBookDataByGcrName(gcrNames, context.resourceResolver());

            assertTrue(result.isPresent());
            assertNotNull(result.get().getGfcs());
            assertNotNull(result.get().getGfcs().getGfcList());
            assertEquals(1, result.get().getGfcs().getGfcList().size());
            final Gfc gfc = result.get().getGfcs().getGfcList().get(0);
            assertEquals(136499, gfc.getCode());
            assertNotNull(gfc.getProducts());
            assertNotNull(gfc.getProducts().getNdc());
            assertEquals(8, gfc.getProducts().getNdc().size());
            assertEquals("63323-0820-00", gfc.getProducts().getNdc().get(0));
        }
    }

    @Test
    void getRedBookDataBySolutionName(final AemContext context) throws RepositoryException {
        try (final MockedStatic<QueryUtils> queryUtils = Mockito.mockStatic(QueryUtils.class)) {
            final Session session = context.resourceResolver().adaptTo(Session.class);
            if (session != null) {
                String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=112912";
                List<String> resultPaths = Arrays.asList("/etc/import-data/redbook/0920000/0924600/0924649/112912");
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator1, resultPaths);

                queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=114451";
                resultPaths = new ArrayList<>();
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator2, resultPaths);
            }

            final Resource substanceResource = context
              .resourceResolver()
              .getResource("/content/mdx-cem/iv-compatibility/product-monographs/unit-tests/product-1/jcr:content/root/container/substance");
            final Substance substance = substanceResource.adaptTo(Substance.class);
            final Optional<RedBookData> result = this.serviceUnderTest.getRedBookDataBySolutionName(substance, context.resourceResolver());

            assertTrue(result.isPresent());
            assertNotNull(result.get().getGfcs());
            assertNotNull(result.get().getGfcs().getGfcList());
            assertEquals(1, result.get().getGfcs().getGfcList().size());
            final Gfc gfc = result.get().getGfcs().getGfcList().get(0);
            assertEquals(112912, gfc.getCode());
            assertNotNull(gfc.getProducts());
            assertNotNull(gfc.getProducts().getNdc());
            assertEquals(14, gfc.getProducts().getNdc().size());
            assertEquals("00074-7929-03", gfc.getProducts().getNdc().get(0));
        }
    }

    @Test
    void getRedBookDataBySolutionPath(final AemContext context) throws RepositoryException {
        try (final MockedStatic<QueryUtils> queryUtils = Mockito.mockStatic(QueryUtils.class)) {
            final Session session = context.resourceResolver().adaptTo(Session.class);
            if (session != null) {
                final String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + RedBookDataProcessor.DATA_ROOT_PATH + "]) and type='gfc' and [" + RedBookDataProcessor.PROPERTY_NAME_GFC_CODE + "]=112996";
                final List<String> resultPaths = Arrays.asList("/etc/import-data/redbook/0920000/0924600/0924664/112996");
                this.mockQueryUtils(queryUtils, queryString, session, this.nodeIterator1, resultPaths);
            }

            final Optional<RedBookData> result = this.serviceUnderTest.getRedBookDataBySolutionPath(
              "/etc/import-data/solution-product-lookup/01390", context.resourceResolver());

            assertTrue(result.isPresent());
            assertNotNull(result.get().getGfcs());
            assertNotNull(result.get().getGfcs().getGfcList());
            assertEquals(1, result.get().getGfcs().getGfcList().size());
            final Gfc gfc = result.get().getGfcs().getGfcList().get(0);
            assertEquals(112996, gfc.getCode());
            assertNotNull(gfc.getProducts());
            assertNotNull(gfc.getProducts().getNdc());
            assertEquals(2, gfc.getProducts().getNdc().size());
            assertEquals("00264-1915-00", gfc.getProducts().getNdc().get(0));
        }
    }

    private void mockQueryUtils(@NotNull final MockedStatic<QueryUtils> queryUtils, @NotNull final String queryString, @NotNull final Session session, final NodeIterator nodeIterator, @NotNull final List<String> paths) {
        queryUtils.when(() -> QueryUtils.getQueryNodes(session, queryString)).thenReturn(nodeIterator);
        OngoingStubbing<Boolean> stubbingHasNext = Mockito.when(nodeIterator.hasNext());
        for (int i = 0; i < paths.size(); i++) {
            stubbingHasNext = stubbingHasNext.thenReturn(true);
        }
        stubbingHasNext.thenReturn(false);

        if (paths.size() > 0) {
            Node node;
            final OngoingStubbing<Node> stubbingNextNode = Mockito.when(nodeIterator.nextNode());
            for (final String path : paths) {
                node = context.resourceResolver().getResource(path).adaptTo(Node.class);
                stubbingNextNode.thenReturn(node);
            }
        }
    }

}
