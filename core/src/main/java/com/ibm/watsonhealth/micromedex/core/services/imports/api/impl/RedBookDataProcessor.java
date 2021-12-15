package com.ibm.watsonhealth.micromedex.core.services.imports.api.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watsonhealth.micromedex.core.services.PreReplicationQueueService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.PreReplicationQueueException;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.DataProcessor;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.exceptions.DataParsingException;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.exceptions.ProcessRedBookDataException;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.IvRedBookData;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.IvRedBookGcr;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.IvRedBookGfc;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.IvRedBookProduct;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.IvRedBookRoot;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.AbstractImportServlet;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.RedBookImportServlet;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadedItemStatusEnum;
import com.ibm.watsonhealth.micromedex.core.utils.DateTimeUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ImportApiUtils;
import com.ibm.watsonhealth.micromedex.core.utils.PropertyUtils;
import com.ibm.watsonhealth.micromedex.core.utils.QueryUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;
import com.ibm.watsonhealth.micromedex.core.utils.exceptions.SessionNotAvailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = { DataProcessor.class }, immediate = true)
public class RedBookDataProcessor implements DataProcessor {

    protected static final ObjectMapper jsonObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    protected static final NumberFormat SEVEN_DIGIT_FORMATTER = new DecimalFormat("#0000000");

    @Reference
    private PreReplicationQueueService preReplicationQueueService;

    @SuppressWarnings("java:S1075")
    public static final String DATA_ROOT_PATH = "/etc/import-data/redbook";

    public static final String PROPERTY_NAME_TYPE = "type";
    public static final String PROPERTY_NAME_GCR_CODE = "gcrCode";
    public static final String PROPERTY_NAME_GCR_NAME = "name";
    public static final String PROPERTY_NAME_GCR_NAME_LOWERCASE = "name-lowercase";
    public static final String PROPERTY_NAME_GFC_CODE = "gfcCode";
    public static final String PROPERTY_NAME_GFC_ROA_CODE = "roaCode";
    public static final String PROPERTY_NAME_GFC_FORM = "form";
    public static final String PROPERTY_NAME_GFC_STRENGTH = "strength";
    public static final String PROPERTY_NAME_PRODUCT_NDC_CODE = "ndcCode";
    public static final String PROPERTY_NAME_PRODUCT_NAME = "productName";
    public static final String PROPERTY_NAME_PRODUCT_NAME_LOWERCASE = "productName-lowercase";
    public static final String PROPERTY_NAME_PRODUCT_CATEGORY = "productCategory";
    public static final String PROPERTY_NAME_MODIFIED_COUNTER = "modified-counter";

    static {
        jsonObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Override
    public void doProcess(@NotNull final ResourceResolver resourceResolver) {
        try {
            log.info("doProcess");
            final Optional<UploadTypeStatusEnum> status = ImportApiUtils.getUploadTypeStatusFlag(this.getImportPath(), resourceResolver);
            if (status.isPresent()) {
                log.warn("processing can't be started because status = " + status.get());
            } else {
                try {
                    ImportApiUtils.setUploadTypeStatusFlag(UploadTypeStatusEnum.PROCESSING, this.getImportPath(), resourceResolver);
                    final List<Resource> resourcesToProcess = this.getResourcesToProcess(resourceResolver);
                    final List<IvRedBookRoot> redBookRoot = this.getParsedRedBookRootResources(resourcesToProcess);
                    if (!redBookRoot.isEmpty()) {
                        this.processRedBookData(redBookRoot, resourceResolver);
                    }
                } finally {
                    ImportApiUtils.setUploadTypeStatusFlag(null, this.getImportPath(), resourceResolver);
                }
            }
        } catch (final Exception ex) {
            log.error("error while processing data", ex);
            resourceResolver.revert();
        }
    }

    @Override
    public String getDataRootPath() {
        return DATA_ROOT_PATH;
    }

    @Override
    public String getImportPath() {
        return RedBookImportServlet.IMPORT_PATH;
    }

    protected List<Resource> getResourcesToProcess(final ResourceResolver resourceResolver) {
        final List<Resource> result = new ArrayList<>();
        final Resource rootResource = resourceResolver.getResource(this.getImportPath());
        if (rootResource != null) {
            final Iterator<Resource> redBooksResources = rootResource.listChildren();
            while (redBooksResources.hasNext()) {
                final Resource redBooksResource = redBooksResources.next();
                final ValueMap valueMap = redBooksResource.getValueMap();
                if (valueMap.containsKey(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME) && StringUtils.equals(
                  valueMap.get(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME, String.class), UploadedItemStatusEnum.DONE.name())) {
                    result.add(redBooksResource);
                }
            }
        }
        return result;
    }

    private List<IvRedBookRoot> getParsedRedBookRootResources(final List<Resource> resources) throws DataParsingException {
        final List<IvRedBookRoot> result = new ArrayList<>();
        IvRedBookRoot redBookRoot;
        for (final Resource resource : resources) {
            final ValueMap valueMap = resource.getValueMap();
            if (valueMap.containsKey(AbstractImportServlet.PAYLOAD_PROPERTY_NAME)) {
                log.debug("parse {}", resource.getPath());
                try {
                    redBookRoot = jsonObjectMapper.readValue(
                      valueMap.get(AbstractImportServlet.PAYLOAD_PROPERTY_NAME, String.class), IvRedBookRoot.class);
                } catch (final Exception ex) {
                    throw new DataParsingException(resource.getPath(), ex);
                }
                final Calendar created = DateTimeUtils.getCreatedDate(valueMap);
                if (created != null) {
                    redBookRoot.setCreated(DateTimeUtils.convertCalendar2LocalDateTime(created));
                }
                redBookRoot.setJcrPath(resource.getPath());
                result.add(redBookRoot);
            }
        }
        result.sort(Comparator.comparing(IvRedBookRoot::getCreated));
        return result;
    }

    private void processRedBookData(@NotNull final List<IvRedBookRoot> redBookRoot, @NotNull final ResourceResolver resourceResolver) throws PreReplicationQueueException, ProcessRedBookDataException {
        for (final IvRedBookRoot redBookRootItem : redBookRoot) {
            final String preReplicationQueueId = this.preReplicationQueueService.createQueue();
            try {
                for (final IvRedBookData dataItem : redBookRootItem.getData()) {
                    for (final IvRedBookGcr gcr : dataItem.getGcrs()) {
                        final Optional<Resource> gcrResource = this.searchForRedBookGcr(gcr.getGcrCode(), resourceResolver);
                        if (gcrResource.isPresent()) {
                            this.updateRedBookGcr(gcr, gcrResource.get(), preReplicationQueueId);
                        } else {
                            final Resource resource = this.initPath(
                              this.getDataRootPath() + "/" + this.getGcrPath(gcr.getGcrCode()), resourceResolver);
                            this.updateRedBookGcr(gcr, resource, preReplicationQueueId);
                        }
                    }
                }
                final Resource resource = resourceResolver.getResource(redBookRootItem.getJcrPath());
                if (resource != null) {
                    resourceResolver.delete(resource);
                }
                this.preReplicationQueueService.releaseQueue(preReplicationQueueId);
                resourceResolver.commit();
            } catch (final Exception ex) {
                this.preReplicationQueueService.cancel(preReplicationQueueId);
                throw new ProcessRedBookDataException(ex);
            }
        }
    }

    private String getGcrPath(final long gcrCode) {
        final long pathItem1 = gcrCode / 10000 * 10000;
        final long pathItem2 = gcrCode / 100 * 100;
        return SEVEN_DIGIT_FORMATTER.format(pathItem1) + "/" + SEVEN_DIGIT_FORMATTER.format(pathItem2) + "/" + SEVEN_DIGIT_FORMATTER.format(gcrCode);
    }

    protected Resource initPath(@NotNull final String path, @NotNull final ResourceResolver resourceResolver) throws SessionNotAvailableException, RepositoryException {
        final Session session = ResourceResolverUtils.getSession(resourceResolver);
        final Node node = JcrUtils.getOrCreateByPath(path, JcrConstants.NT_UNSTRUCTURED, session);
        return resourceResolver.getResource(node.getPath());
    }

    protected Optional<Resource> searchForRedBookGcr(final long gcrCode, @NotNull final ResourceResolver resourceResolver) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session == null) {
            return Optional.empty();
        }

        final String queryString = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, [" + this.getDataRootPath() + "]) AND [s].[" + PROPERTY_NAME_GCR_CODE + "] = '" + gcrCode + "' AND [s].[" + PROPERTY_NAME_TYPE + "] = 'gcr'";
        final NodeIterator queryResultIterator = QueryUtils.getQueryNodes(session, queryString);
        if (queryResultIterator.hasNext()) {
            final Resource result = resourceResolver.getResource(queryResultIterator.nextNode().getPath());
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    protected void updateRedBookGcr(@NotNull final IvRedBookGcr gcr, @NotNull final Resource resource, @NotNull final String preRepilicationQueueId) throws RepositoryException, PreReplicationQueueException {
        final Node node = resource.adaptTo(Node.class);
        if (node != null) {
            PropertyUtils.updateProperty(node, PROPERTY_NAME_GCR_CODE, gcr.getGcrCode());
            PropertyUtils.updateProperty(node, PROPERTY_NAME_GCR_NAME, gcr.getName());
            PropertyUtils.updateProperty(node, PROPERTY_NAME_GCR_NAME_LOWERCASE, StringUtils.lowerCase(gcr.getName(), Locale.getDefault()));
            PropertyUtils.updateProperty(node, PROPERTY_NAME_TYPE, "gcr");
            if (!node.hasProperty(JcrConstants.JCR_CREATED)) {
                PropertyUtils.updateProperty(node, JcrConstants.JCR_CREATED, Calendar.getInstance());
            }
            if (node.hasProperty(PROPERTY_NAME_MODIFIED_COUNTER)) {
                PropertyUtils.updateProperty(node, PROPERTY_NAME_MODIFIED_COUNTER, node.getProperty(PROPERTY_NAME_MODIFIED_COUNTER).getLong() + 1);
            } else {
                PropertyUtils.updateProperty(node, PROPERTY_NAME_MODIFIED_COUNTER, 1);
            }
            PropertyUtils.updateProperty(node, JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());

            if (gcr.getGfcs() != null) {
                for (final IvRedBookGfc gfc : gcr.getGfcs()) {
                    this.updateRedBookGfc(gfc, node);
                }
            }
            this.preReplicationQueueService.addToQueue(node.getPath(), preRepilicationQueueId);
        }
    }

    protected void updateRedBookGfc(@NotNull final IvRedBookGfc gfc, @NotNull final Node gcrNode) throws RepositoryException {
        final String nodeName = Long.toString(gfc.getGfcCode());
        final Node node = JcrUtils.getOrAddNode(gcrNode, nodeName);

        PropertyUtils.updateProperty(node, PROPERTY_NAME_GFC_CODE, gfc.getGfcCode());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_GFC_ROA_CODE, gfc.getRoaCode());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_GFC_FORM, gfc.getForm());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_GFC_STRENGTH, gfc.getStrength());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_TYPE, "gfc");

        if (gfc.getProducts() != null) {
            for (final IvRedBookProduct product : gfc.getProducts()) {
                this.updateRedBookProduct(product, node);
            }
        }

        final NodeIterator productNodesIterator = node.getNodes();
        while (productNodesIterator.hasNext()) {
            final Node productNode = productNodesIterator.nextNode();
            final String productNodeName = productNode.getName();
            if (gfc.getProducts() == null || gfc
              .getProducts()
              .stream()
              .noneMatch(product -> StringUtils.equals(product.getNdcCode(), productNodeName))) {
                productNode.remove();
            }
        }
    }

    protected void updateRedBookProduct(@NotNull final IvRedBookProduct product, @NotNull final Node gfcNode) throws RepositoryException {
        final Node node = JcrUtils.getOrAddNode(gfcNode, product.getNdcCode());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_PRODUCT_NDC_CODE, product.getNdcCode());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_PRODUCT_NAME, product.getName());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_PRODUCT_NAME_LOWERCASE, StringUtils.lowerCase(product.getName(), Locale.getDefault()));
        PropertyUtils.updateProperty(node, PROPERTY_NAME_PRODUCT_CATEGORY, product.getCategory());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_TYPE, "product");
    }

}
