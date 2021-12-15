package com.ibm.watsonhealth.micromedex.core.servlets;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.watsonhealth.micromedex.core.services.api.CemApiEndpointService;
import com.ibm.watsonhealth.micromedex.core.services.api.ResponseData;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.GetDataException;
import com.ibm.watsonhealth.micromedex.core.services.api.impl.IvCompatibilityEndpointService;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = { GenericCemApiEndpointServlet.SERVLET_RESOURCE_TYPE_IV_COMPATIBILITY }, selectors = {
  GenericCemApiEndpointServlet.SERVLET_SELECTOR }, extensions = {
  GenericCemApiEndpointServlet.SERVLET_EXTENSION }, methods = HttpConstants.METHOD_GET)
public class GenericCemApiEndpointServlet extends SlingSafeMethodsServlet {

    static final String SERVLET_RESOURCE_TYPE_IV_COMPATIBILITY = "contentauthoring/components/ivcompatibility/structure/page";
    static final String SERVLET_SELECTOR = "cem-api";
    static final String SERVLET_EXTENSION = "xml";

    public static final DateTimeFormatter API_ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Reference(target = "(" + CemApiEndpointService.ENDPOINT_TYPE_PROPERTY_NAME + "=" + IvCompatibilityEndpointService.ENDPOINT_TYPE + ")")
    private transient CemApiEndpointService ivCompatibilityEndpointService;

    private transient CemApiEndpointService currentEndpointService;

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) {
        try {
            ResponseUtils.setContentTypeXml(response, StandardCharsets.UTF_8);
            if (StringUtils.equals(request.getResource().getResourceType(), SERVLET_RESOURCE_TYPE_IV_COMPATIBILITY)) {
                this.currentEndpointService = this.ivCompatibilityEndpointService;
            } else {
                response.setStatus(HttpStatus.SC_NOT_FOUND);
            }

            if (this.currentEndpointService != null) {
                final Optional<ResponseData> responseData = this.currentEndpointService.getData(request);
                if (responseData.isPresent()) {
                    this.currentEndpointService.applyToResponse(responseData.get(), response);
                } else {
                    response.setStatus(HttpStatus.SC_NOT_FOUND);
                }
            }
        } catch (final RuntimeException | CantApplyToResponseException | GetDataException ex) {
            log.error("Exception in doGet method", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}