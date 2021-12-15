package com.ibm.watsonhealth.micromedex.core.servlets.imports.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.services.MockSlingSettingService;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watsonhealth.micromedex.core.context.AppAemContextBuilder;
import com.ibm.watsonhealth.micromedex.core.context.utils.TestUtils;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadedItemStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.vo.ErrorMessagesVO;
import com.ibm.watsonhealth.micromedex.core.utils.ImportApiUtils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class RedBookImportServletTest {

    protected static final ObjectMapper jsonObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private AemContext context;

    private MockRequestPathInfo requestPathInfo;

    @InjectMocks
    private RedBookImportServlet servletUnderTest;

    AemContext createAemContext() {
        return new AppAemContextBuilder(ResourceResolverType.JCR_MOCK)
          .loadResources("jcr_root")
          .registerService(SlingSettingsService.class, new MockSlingSettingService())
          .registerInjectActivateService(this.servletUnderTest)
          .loadSlingModels("com.ibm.watsonhealth.micromedex.core.models")
          .build();
    }

    @BeforeEach
    void beforeEach() {
        this.servletUnderTest = new RedBookImportServlet();
        this.context = this.createAemContext();
        this.context.runMode("author");
        this.requestPathInfo = this.context.requestPathInfo();
        this.context.response().resetBuffer();
        this.context.response().reset();
    }

    @Test
    @DisplayName("GIVEN valid request WHEN using method 'GET' THEN it responds with status code 'method not allowed'")
    void testGet() {
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json");
        this.servletUnderTest.doGet(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, this.context.response().getStatus());
    }

    @Test
    @DisplayName("GIVEN valid start request WHEN using repo without start flag THEN it responds with status code 'ok'")
    void testStartRequestValid() throws IOException {
        this.context.requestPathInfo().setSuffix("/start");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json/start");
        final Resource redbookRootResource = this.context.resourceResolver().getResource("/etc/upload/redbook");

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_OK, this.context.response().getStatus());
    }

    @Test
    @DisplayName("GIVEN valid start request WHEN using repo with start flag THEN it responds with status code 'internal server error' and error code 102")
    void testStartRequestInvalid() throws IOException, RepositoryException {
        this.context.requestPathInfo().setSuffix("/start");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json/start");
        final Node redbookRootNode = this.getRedbookRootNode();
        this.setStartFlag(redbookRootNode);

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        final ErrorMessagesVO errorMessages = jsonObjectMapper.readValue(this.context.response().getOutputAsString(), ErrorMessagesVO.class);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, this.context.response().getStatus());
        assertEquals(1, CollectionUtils.size(errorMessages.getErrormessages()));
        assertEquals(102, errorMessages.getErrormessages().get(0).getCode());
    }

    @Test
    @DisplayName("GIVEN valid upload request WHEN using repo without start flag THEN it responds with status code 'internal server error' and error code 201")
    void testUploadRequestInvalid() throws IOException {
        this.context.requestPathInfo().setSuffix("");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json");
        final String uploadData = TestUtils.getExpectedResult(RedBookImportServlet.class, "Upload.json");
        this.context.request().setContent(uploadData.getBytes(StandardCharsets.UTF_8));

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        final ErrorMessagesVO errorMessages = jsonObjectMapper.readValue(this.context.response().getOutputAsString(), ErrorMessagesVO.class);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, this.context.response().getStatus());
        assertEquals(1, CollectionUtils.size(errorMessages.getErrormessages()));
        assertEquals(201, errorMessages.getErrormessages().get(0).getCode());
    }

    @Test
    @DisplayName("GIVEN valid upload request WHEN using repo with start flag THEN it responds with status code 'ok' and repository contains one more node with status 'uploaded'")
    void testUploadRequestValid() throws IOException, RepositoryException {
        this.context.requestPathInfo().setSuffix("");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json");
        final String uploadData = TestUtils.getExpectedResult(RedBookImportServlet.class, "Upload.json");
        this.context.request().setContent(uploadData.getBytes(StandardCharsets.UTF_8));

        final Node redbookRootNode = this.getRedbookRootNode();
        this.setStartFlag(redbookRootNode);

        NodeIterator nodeIterator = redbookRootNode.getNodes();
        int numUploadedBefore = 0;
        int numNodesBefore = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesBefore++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedBefore++;
            }
        }

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_OK, this.context.response().getStatus());
        nodeIterator = redbookRootNode.getNodes();
        int numUploadedAfter = 0;
        int numNodesAfter = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesAfter++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedAfter++;
            }
        }
        assertEquals(numUploadedBefore + 1, numUploadedAfter);
        assertEquals(numNodesBefore + 1, numNodesAfter);
    }

    private void setStartFlag(final Node redbookRootNode) throws RepositoryException {
        if (redbookRootNode != null) {
            redbookRootNode.setProperty(ImportApiUtils.UPLOAD_TYPE_STATUS_PROPERTY_NAME, UploadTypeStatusEnum.START.name());
        }
    }

    @Test
    @DisplayName("GIVEN valid done request WHEN using repo with start flag THEN it responds with status code 'ok' and repository contains only items with status 'done'")
    void testDoneRequest() throws IOException, RepositoryException {
        this.context.requestPathInfo().setSuffix("/done");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json/done");
        final String uploadData = TestUtils.getExpectedResult(RedBookImportServlet.class, "Upload.json");
        this.context.request().setContent(uploadData.getBytes(StandardCharsets.UTF_8));

        final Node redbookRootNode = this.getRedbookRootNode();
        this.setStartFlag(redbookRootNode);

        NodeIterator nodeIterator = redbookRootNode.getNodes();
        int numUploadedBefore = 0;
        int numDoneBefore = 0;
        int numNodesBefore = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesBefore++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedBefore++;
            }
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.DONE.name())) {
                numDoneBefore++;
            }
        }

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_OK, this.context.response().getStatus());
        nodeIterator = redbookRootNode.getNodes();
        int numUploadedAfter = 0;
        int numDoneAfter = 0;
        int numNodesAfter = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesAfter++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedAfter++;
            }
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.DONE.name())) {
                numDoneAfter++;
            }
        }
        assertEquals(numUploadedBefore + numDoneBefore, numDoneAfter);
        assertEquals(0, numUploadedAfter);
        assertEquals(numNodesBefore, numNodesAfter);
    }

    @Test
    @DisplayName("GIVEN valid reject request WHEN using repo with start flag THEN it responds with status code 'ok' and repository contains only items with status 'done' and all items which had status 'uploaded' got deleted")
    void testRejectRequest() throws IOException, RepositoryException {
        this.context.requestPathInfo().setSuffix("/reject");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json/reject");
        final String uploadData = TestUtils.getExpectedResult(RedBookImportServlet.class, "Upload.json");
        this.context.request().setContent(uploadData.getBytes(StandardCharsets.UTF_8));

        final Node redbookRootNode = this.getRedbookRootNode();
        this.setStartFlag(redbookRootNode);

        NodeIterator nodeIterator = redbookRootNode.getNodes();
        int numUploadedBefore = 0;
        int numDoneBefore = 0;
        int numNodesBefore = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesBefore++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedBefore++;
            }
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.DONE.name())) {
                numDoneBefore++;
            }
        }

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_OK, this.context.response().getStatus());
        nodeIterator = redbookRootNode.getNodes();
        int numUploadedAfter = 0;
        int numDoneAfter = 0;
        int numNodesAfter = 0;
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            numNodesAfter++;
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.UPLOADED.name())) {
                numUploadedAfter++;
            }
            if (StringUtils.equals(
              node.getProperty(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME).getString(), UploadedItemStatusEnum.DONE.name())) {
                numDoneAfter++;
            }
        }
        assertEquals(numDoneBefore, numDoneAfter);
        assertEquals(0, numUploadedAfter);
        assertEquals(numNodesBefore - numUploadedBefore, numNodesAfter);
    }

    @Test
    @DisplayName("GIVEN valid reject request WHEN using runmode 'publish' THEN it responds with status code 'not found'")
    void testRequestOnPublish() throws IOException {
        this.context.runMode("publish");
        this.context.requestPathInfo().setSuffix("/reject");
        this.context.requestPathInfo().setExtension("json");
        this.context.request().setPathInfo(RedBookImportServlet.SERVLET_PATH + ".json/reject");
        final String uploadData = TestUtils.getExpectedResult(RedBookImportServlet.class, "Upload.json");
        this.context.request().setContent(uploadData.getBytes(StandardCharsets.UTF_8));

        this.servletUnderTest.doPost(this.context.request(), this.context.response());
        assertEquals(HttpStatus.SC_NOT_FOUND, this.context.response().getStatus());
    }

    @NotNull
    private Node getRedbookRootNode() {
        final Resource redbookRootResource = this.context.resourceResolver().getResource("/etc/upload/redbook");
        if (redbookRootResource != null) {
            final Node result = redbookRootResource.adaptTo(Node.class);
            if (result == null) {
                throw new NullPointerException("redbook root node is null");
            }
            return result;
        }
        throw new NullPointerException("redbook root resource is null");
    }

}
