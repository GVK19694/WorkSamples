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
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
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
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.SolutionProductLookup;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.payloads.SolutionProductLookupRoot;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.AbstractImportServlet;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.SolutionProductLookupImportServlet;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadedItemStatusEnum;
import com.ibm.watsonhealth.micromedex.core.utils.DateTimeUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ImportApiUtils;
import com.ibm.watsonhealth.micromedex.core.utils.PropertyUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = { DataProcessor.class }, immediate = true)
public class SolutionProductLookupDataProcessor implements DataProcessor {

    protected static final ObjectMapper jsonObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    protected static final NumberFormat FIVE_DIGIT_FORMATTER = new DecimalFormat("#00000");

    @Reference
    private PreReplicationQueueService preReplicationQueueService;

    @SuppressWarnings("java:S1075")
    public static final String DATA_ROOT_PATH = "/etc/import-data/solution-product-lookup";

    public static final String PROPERTY_PRODUCT_ID = "solutionProductId";
    public static final String PROPERTY_NAME = "solutionName";
    public static final String PROPERTY_NAME_LOWERCASE = "solutionName-lowercase";
    public static final String PROPERTY_ABBREVIATION = "solutionAbbreviation";
    public static final String PROPERTY_GFCS = "solutionGfcs";
    public static final String PROPERTY_OTHER_NAMES = "solutionOtherNames";
    public static final String PROPERTY_OTHER_NAMES_LOWERCASE = "solutionOtherNames-lowercase";
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
                    final List<SolutionProductLookupRoot> solutions = this.getParsedSolutionsRootResources(resourcesToProcess);
                    if (!solutions.isEmpty()) {
                        this.processSolutionData(solutions, resourceResolver);
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
        return SolutionProductLookupImportServlet.IMPORT_PATH;
    }

    protected List<Resource> getResourcesToProcess(final ResourceResolver resourceResolver) {
        final List<Resource> result = new ArrayList<>();
        final Resource rootResource = resourceResolver.getResource(this.getImportPath());
        if (rootResource != null) {
            final Iterator<Resource> solutionsResources = rootResource.listChildren();
            while (solutionsResources.hasNext()) {
                final Resource solutionsResource = solutionsResources.next();
                final ValueMap valueMap = solutionsResource.getValueMap();
                if (valueMap.containsKey(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME) && StringUtils.equals(
                  valueMap.get(ImportApiUtils.UPLOADED_ITEM_STATUS_PROPERTY_NAME, String.class), UploadedItemStatusEnum.DONE.name())) {
                    result.add(solutionsResource);
                }
            }
        }
        return result;
    }

    private List<SolutionProductLookupRoot> getParsedSolutionsRootResources(final List<Resource> resources) throws DataParsingException {
        final List<SolutionProductLookupRoot> result = new ArrayList<>();
        SolutionProductLookupRoot solutions;
        for (final Resource resource : resources) {
            final ValueMap valueMap = resource.getValueMap();
            if (valueMap.containsKey(AbstractImportServlet.PAYLOAD_PROPERTY_NAME)) {
                log.debug("parse {}", resource.getPath());
                try {
                    solutions = jsonObjectMapper.readValue(
                      valueMap.get(AbstractImportServlet.PAYLOAD_PROPERTY_NAME, String.class), SolutionProductLookupRoot.class);
                } catch (final Exception ex) {
                    throw new DataParsingException(resource.getPath(), ex);
                }
                final Calendar created = DateTimeUtils.getCreatedDate(valueMap);
                if (created != null) {
                    solutions.setCreated(DateTimeUtils.convertCalendar2LocalDateTime(created));
                }
                solutions.setJcrPath(resource.getPath());
                result.add(solutions);
            }
        }
        result.sort(Comparator.comparing(SolutionProductLookupRoot::getCreated));
        return result;
    }

    private void processSolutionData(@NotNull final List<SolutionProductLookupRoot> solutionProductLookupRoot, @NotNull final ResourceResolver resourceResolver) throws PreReplicationQueueException, ProcessRedBookDataException, RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);
        if (session != null) {
            final ValueFactory valueFactory = session.getValueFactory();
            for (final SolutionProductLookupRoot solutionRootItem : solutionProductLookupRoot) {
                final String preReplicationQueueId = this.preReplicationQueueService.createQueue();
                try {
                    this.deleteExistingData(preReplicationQueueId, resourceResolver);
                    resourceResolver.commit();
                    this.processSolution(solutionRootItem, preReplicationQueueId, resourceResolver, session, valueFactory);
                    this.preReplicationQueueService.releaseQueue(preReplicationQueueId);
                    resourceResolver.commit();
                } catch (final Exception ex) {
                    this.preReplicationQueueService.cancel(preReplicationQueueId);
                    throw new ProcessRedBookDataException(ex);
                }
            }
        }
    }

    private void deleteExistingData(final String preReplicationQueueId, @NotNull final ResourceResolver resourceResolver) throws PreReplicationQueueException, PersistenceException {
        final Resource rootResource = resourceResolver.getResource(DATA_ROOT_PATH);
        if (rootResource != null) {
            final Iterator<Resource> childIterator = rootResource.listChildren();
            while (childIterator.hasNext()) {
                final Resource resource = childIterator.next();
                this.preReplicationQueueService.addToQueueDeactivate(resource.getPath(), preReplicationQueueId);
                resourceResolver.delete(resource);
            }
        }
    }

    private void processSolution(final SolutionProductLookupRoot solutionRootItem, final String preReplicationQueueId, final @NotNull ResourceResolver resourceResolver, final Session session, final ValueFactory valueFactory) throws RepositoryException, PreReplicationQueueException, PersistenceException {
        for (final SolutionProductLookup solutionProductLookup : solutionRootItem.getSolutionProductLookups()) {
            final Node solutionNode = JcrUtils.getOrCreateByPath(
              DATA_ROOT_PATH + "/" + FIVE_DIGIT_FORMATTER.format(solutionProductLookup.getProductId()), JcrConstants.NT_UNSTRUCTURED, session);
            if (solutionNode != null) {
                this.updateSolution(solutionProductLookup, solutionNode, preReplicationQueueId, valueFactory);
            }
        }

        final Resource resource = resourceResolver.getResource(solutionRootItem.getJcrPath());
        if (resource != null) {
            resourceResolver.delete(resource);
        }
    }

    protected void updateSolution(@NotNull final SolutionProductLookup solution, @NotNull final Node node, @NotNull final String preRepilicationQueueId, @NotNull final ValueFactory valueFactory) throws RepositoryException, PreReplicationQueueException {
        PropertyUtils.updateProperty(node, PROPERTY_PRODUCT_ID, solution.getProductId());
        PropertyUtils.updateProperty(node, PROPERTY_NAME, solution.getName());
        PropertyUtils.updateProperty(node, PROPERTY_NAME_LOWERCASE, StringUtils.lowerCase(solution.getName(), Locale.getDefault()));
        PropertyUtils.updateProperty(node, PROPERTY_ABBREVIATION, solution.getAbbreviation());
        PropertyUtils.updateProperty(node, PROPERTY_GFCS, solution.getGfcs(), valueFactory);
        PropertyUtils.updateProperty(node, PROPERTY_OTHER_NAMES, solution.getOtherNames());
        final List<String> otherNamesLowerCase = new ArrayList<>();
        solution.getOtherNames().forEach(otherName -> otherNamesLowerCase.add(StringUtils.lowerCase(otherName, Locale.getDefault())));
        PropertyUtils.updateProperty(node, PROPERTY_OTHER_NAMES_LOWERCASE, otherNamesLowerCase);

        if (!node.hasProperty(JcrConstants.JCR_CREATED)) {
            PropertyUtils.updateProperty(node, JcrConstants.JCR_CREATED, Calendar.getInstance());
        }
        if (node.hasProperty(PROPERTY_NAME_MODIFIED_COUNTER)) {
            PropertyUtils.updateProperty(node, PROPERTY_NAME_MODIFIED_COUNTER, node.getProperty(PROPERTY_NAME_MODIFIED_COUNTER).getLong() + 1);
        } else {
            PropertyUtils.updateProperty(node, PROPERTY_NAME_MODIFIED_COUNTER, 1);
        }
        PropertyUtils.updateProperty(node, JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());

        this.preReplicationQueueService.addToQueue(node.getPath(), preRepilicationQueueId);
    }

}
