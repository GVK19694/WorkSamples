package com.ibm.watsonhealth.micromedex.core.servlets.imports.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadTypeStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.enums.UploadedItemStatusEnum;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions.IllegalStatusException;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions.NotAllowedSuffixException;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions.RootResourceInitializationException;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.vo.ErrorMessageVO;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.vo.ErrorMessagesVO;
import com.ibm.watsonhealth.micromedex.core.utils.ImportApiUtils;
import com.ibm.watsonhealth.micromedex.core.utils.RequestUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;
import com.ibm.watsonhealth.micromedex.core.utils.RunModeUtils;
import com.ibm.watsonhealth.micromedex.core.utils.exceptions.SessionNotAvailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractImportServlet extends SlingAllMethodsServlet {

    protected static final String SERVLET_EXTENSION = "json";

    protected static final ObjectMapper jsonObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final String START_SUFFIX = "/start";
    private static final String DONE_SUFFIX = "/done";
    private static final String REJECT_SUFFIX = "/reject";

    @SuppressWarnings("java:S1075")
    protected static final String IMPORT_ROOT_PATH = "/etc/upload/";

    public static final String PAYLOAD_PROPERTY_NAME = "payload";

    protected static final String ERROR_MESSAGE_START_FLAG_NOT_SET = "the startflag is not set.";

    protected static final ErrorMessageVO ERROR_UNEXPECTED_START = new ErrorMessageVO(100, AbstractImportServlet.UNEXPECTED_EXCEPTION_MESSAGE);
    protected static final ErrorMessageVO ERROR_DATAPROCESSOR_IS_RUNNING = new ErrorMessageVO(101, "the data procoessor is running at the moment.");
    protected static final ErrorMessageVO ERROR_STARTFLAG_ALREADY_SET = new ErrorMessageVO(102, "the startflag is already set.");

    protected static final ErrorMessageVO ERROR_UNEXPECTED_UPLOAD = new ErrorMessageVO(200, AbstractImportServlet.UNEXPECTED_EXCEPTION_MESSAGE);
    protected static final ErrorMessageVO ERROR_STARTFLAG_NOT_SET_UPLOAD = new ErrorMessageVO(201, ERROR_MESSAGE_START_FLAG_NOT_SET);

    protected static final ErrorMessageVO ERROR_UNEXPECTED_DONE = new ErrorMessageVO(300, AbstractImportServlet.UNEXPECTED_EXCEPTION_MESSAGE);
    protected static final ErrorMessageVO ERROR_STARTFLAG_NOT_SET_DONE = new ErrorMessageVO(301, ERROR_MESSAGE_START_FLAG_NOT_SET);

    protected static final ErrorMessageVO ERROR_UNEXPECTED_REJECT = new ErrorMessageVO(400, AbstractImportServlet.UNEXPECTED_EXCEPTION_MESSAGE);
    protected static final ErrorMessageVO ERROR_STARTFLAG_NOT_SET_REJECT = new ErrorMessageVO(401, ERROR_MESSAGE_START_FLAG_NOT_SET);

    protected static final ErrorMessageVO ERROR_UNEXPECTED = new ErrorMessageVO(1000, AbstractImportServlet.UNEXPECTED_EXCEPTION_MESSAGE);

    private static final String UNEXPECTED_EXCEPTION_MESSAGE = "unexpected exception was thrown.";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) {
        response.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException {
        try {
            if (RunModeUtils.isAuthor(this.getSlingSettingsService())) {
                ResponseUtils.disableDispatcherAndBrowserCache(response);
                ResponseUtils.setContentTypeJson(response, StandardCharsets.UTF_8);
                this.runRequest(request, response);
            } else {
                response.setStatus(HttpStatus.SC_NOT_FOUND);
            }
        } catch (final NotAllowedSuffixException ex) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            log.error("not allowed suffix was used", ex);
        } catch (final Exception ex) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            final ErrorMessagesVO errorMessages = new ErrorMessagesVO();
            errorMessages.addMessage(ERROR_UNEXPECTED);
            this.addErrorMessagesToResponse(errorMessages, response);
            log.error(ERROR_UNEXPECTED.getMessage(), ex);
        }
    }

    protected void addErrorMessagesToResponse(@NotNull final ErrorMessagesVO errorMessages, @NotNull final SlingHttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(jsonObjectMapper.writeValueAsString(errorMessages));
    }

    protected void runRequest(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws NotAllowedSuffixException, IOException {
        final ErrorMessagesVO errorMessages;
        final String suffix = request.getRequestPathInfo().getSuffix();
        if (StringUtils.equalsIgnoreCase(suffix, START_SUFFIX)) {
            errorMessages = this.doStart(request);
        } else if (StringUtils.isBlank(suffix)) {
            errorMessages = this.doUpload(request);
        } else if (StringUtils.equalsIgnoreCase(suffix, DONE_SUFFIX)) {
            errorMessages = this.doDone(request);
        } else if (StringUtils.equalsIgnoreCase(suffix, REJECT_SUFFIX)) {
            errorMessages = this.doReject(request);
        } else {
            throw new NotAllowedSuffixException(suffix);
        }

        if (errorMessages != null && CollectionUtils.isNotEmpty(errorMessages.getErrormessages())) {
            this.addErrorMessagesToResponse(errorMessages, response);
        }
    }

    @NotNull
    protected abstract Resource initRootPath(final @NotNull SlingHttpServletRequest request) throws SessionNotAvailableException, RepositoryException, RootResourceInitializationException;

    @NotNull
    protected Resource initRootPath(final @NotNull SlingHttpServletRequest request, @NotNull final String path) throws SessionNotAvailableException, RepositoryException, RootResourceInitializationException {
        final Session session = RequestUtils.getSession(request);
        final Node node = JcrUtils.getOrCreateByPath(path, JcrConstants.NT_UNSTRUCTURED, session);
        if (node == null) {
            throw new RootResourceInitializationException(path);
        }
        final Resource result = request.getResourceResolver().getResource(node.getPath());
        if (result == null) {
            throw new RootResourceInitializationException(path);
        }
        return result;
    }

    protected ErrorMessagesVO doStart(@NotNull final SlingHttpServletRequest request) {
        final ErrorMessagesVO errorMessages = new ErrorMessagesVO();
        try {
            final Resource rootResource = this.initRootPath(request);
            final Optional<UploadTypeStatusEnum> status = ImportApiUtils.getUploadTypeStatusFlag(
              rootResource.getPath(), request.getResourceResolver());
            if (status.isPresent()) {
                if (status.get() == UploadTypeStatusEnum.START) {
                    errorMessages.addMessage(ERROR_STARTFLAG_ALREADY_SET);
                } else if (status.get() == UploadTypeStatusEnum.PROCESSING) {
                    errorMessages.addMessage(ERROR_DATAPROCESSOR_IS_RUNNING);
                } else {
                    throw new IllegalStatusException();
                }
            } else {
                ImportApiUtils.setUploadTypeStatusFlag(UploadTypeStatusEnum.START, rootResource.getPath(), request.getResourceResolver());
            }
        } catch (final Exception ex) {
            errorMessages.addMessage(ERROR_UNEXPECTED_START);
            log.error(ERROR_UNEXPECTED_START.getMessage(), ex);
        }
        return errorMessages;
    }

    protected ErrorMessagesVO doUpload(final @NotNull SlingHttpServletRequest request) {
        final ErrorMessagesVO errorMessages = new ErrorMessagesVO();
        try {
            final Resource rootResource = this.initRootPath(request);
            final Optional<UploadTypeStatusEnum> status = ImportApiUtils.getUploadTypeStatusFlag(
              rootResource.getPath(), request.getResourceResolver());
            if (!status.isPresent() || status.get() != UploadTypeStatusEnum.START) {
                errorMessages.addMessage(ERROR_STARTFLAG_NOT_SET_UPLOAD);
            } else {
                final String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                this.saveUpload(rootResource, payload);
            }
        } catch (final Exception ex) {
            errorMessages.addMessage(ERROR_UNEXPECTED_UPLOAD);
            log.error(ERROR_UNEXPECTED_UPLOAD.getMessage(), ex);
        }
        return errorMessages;
    }

    protected ErrorMessagesVO doDone(final @NotNull SlingHttpServletRequest request) {
        final ErrorMessagesVO errorMessages = new ErrorMessagesVO();
        try {
            final Resource rootResource = this.initRootPath(request);
            final Optional<UploadTypeStatusEnum> status = ImportApiUtils.getUploadTypeStatusFlag(
              rootResource.getPath(), rootResource.getResourceResolver());
            if (status.isPresent() && status.get() == UploadTypeStatusEnum.START) {
                this.setUploadDoneToAllUploads(rootResource);
                ImportApiUtils.setUploadTypeStatusFlag(null, rootResource.getPath(), request.getResourceResolver());
            } else {
                errorMessages.addMessage(ERROR_STARTFLAG_NOT_SET_DONE);
            }
        } catch (final Exception ex) {
            errorMessages.addMessage(ERROR_UNEXPECTED_DONE);
            log.error(ERROR_UNEXPECTED_DONE.getMessage(), ex);
        }
        return errorMessages;
    }

    protected ErrorMessagesVO doReject(final @NotNull SlingHttpServletRequest request) {
        final ErrorMessagesVO errorMessages = new ErrorMessagesVO();
        try {
            final Resource rootResource = this.initRootPath(request);
            final Optional<UploadTypeStatusEnum> status = ImportApiUtils.getUploadTypeStatusFlag(
              rootResource.getPath(), rootResource.getResourceResolver());
            if (status.isPresent() && status.get() == UploadTypeStatusEnum.START) {
                this.rejectAllUploads(rootResource);
                ImportApiUtils.setUploadTypeStatusFlag(null, rootResource.getPath(), request.getResourceResolver());
            } else {
                errorMessages.addMessage(ERROR_STARTFLAG_NOT_SET_REJECT);
            }
        } catch (final Exception ex) {
            errorMessages.addMessage(ERROR_UNEXPECTED_REJECT);
            log.error(ERROR_UNEXPECTED_REJECT.getMessage(), ex);
        }
        return errorMessages;
    }

    protected void saveUpload(final Resource resource, final String payload) throws RepositoryException, SessionNotAvailableException, PersistenceException {
        final LocalDateTime now = LocalDateTime.now();
        final String uploadNodeName = now.format(DATE_TIME_FORMATTER) + "-" + UUID.randomUUID();
        final Node uploadNode = JcrUtils.getOrCreateByPath(
          resource.getPath() + "/" + uploadNodeName, JcrConstants.NT_UNSTRUCTURED, ResourceResolverUtils.getSession(resource.getResourceResolver()));
        final Resource uploadResource = resource.getResourceResolver().getResource(uploadNode.getPath());
        final ModifiableValueMap valueMap = uploadResource.adaptTo(ModifiableValueMap.class);
        valueMap.put(PAYLOAD_PROPERTY_NAME, payload);
        valueMap.put(JcrConstants.JCR_CREATED, Calendar.getInstance());
        ImportApiUtils.setUploadItemStatusFlag(UploadedItemStatusEnum.UPLOADED, valueMap);
        uploadResource.getResourceResolver().commit();
    }

    protected void setUploadDoneToAllUploads(final Resource resource) {
        final Iterator<Resource> childResources = resource.listChildren();
        while (childResources.hasNext()) {
            final Resource childResource = childResources.next();
            final Optional<UploadedItemStatusEnum> status = ImportApiUtils.getUploadItemStatusFlag(childResource);
            if (status.isPresent() && status.get() == UploadedItemStatusEnum.UPLOADED) {
                final ModifiableValueMap valueMap = childResource.adaptTo(ModifiableValueMap.class);
                ImportApiUtils.setUploadItemStatusFlag(UploadedItemStatusEnum.DONE, valueMap);
            }
        }
    }

    protected void rejectAllUploads(final Resource resource) throws PersistenceException {
        final Iterator<Resource> childResources = resource.listChildren();
        while (childResources.hasNext()) {
            final Resource childResource = childResources.next();
            final Optional<UploadedItemStatusEnum> status = ImportApiUtils.getUploadItemStatusFlag(childResource);
            if (status.isPresent() && status.get() == UploadedItemStatusEnum.UPLOADED) {
                childResource.getResourceResolver().delete(childResource);
            }
        }
    }

    protected abstract SlingSettingsService getSlingSettingsService();

}
