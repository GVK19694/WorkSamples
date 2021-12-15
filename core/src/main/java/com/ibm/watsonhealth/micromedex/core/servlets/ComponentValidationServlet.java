package com.ibm.watsonhealth.micromedex.core.servlets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.services.ValidationService;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;
import com.ibm.watsonhealth.micromedex.core.vo.validation.ValidationResultVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = { ResourceTypeConstants.PAGE, ResourceTypeConstants.PRIMARY_DRUG, ResourceTypeConstants.CONCENTRATION,
  ResourceTypeConstants.COMPATIBILITY, ResourceTypeConstants.SECONDARY_DRUG }, selectors = {
  ComponentValidationServlet.SERVLET_SELECTOR }, extensions = { ComponentValidationServlet.SERVLET_EXTENSION }, methods = HttpConstants.METHOD_GET)
public class ComponentValidationServlet extends SlingSafeMethodsServlet {

    static final String SERVLET_SELECTOR = "component-validation";
    static final String SERVLET_EXTENSION = "json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Reference
    private transient ValidationService validationService;

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException {
        try {
            ResponseUtils.setContentTypeJson(response, StandardCharsets.UTF_8);
            ResponseUtils.disableDispatcherAndBrowserCache(response);
            final List<ValidationModel> validationModels = this.validationService.validate(request.getResource());
            final ValidationResultVO validationResults = new ValidationResultVO(validationModels);
            response.getWriter().println(OBJECT_MAPPER.writeValueAsString(validationResults));
        } catch (final RuntimeException ex) {
            log.error("Exception in doGet method", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}