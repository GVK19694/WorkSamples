package com.ibm.watsonhealth.micromedex.core.servlets;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;
import com.ibm.watsonhealth.micromedex.core.models.SolutionProductLookupModel;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { ServletResolverConstants.SLING_SERVLET_PATHS + "=" + SolutionProductLookupServlet.PATH,
  ServletResolverConstants.SLING_SERVLET_METHODS + "=GET", })
public class SolutionProductLookupServlet extends SlingSafeMethodsServlet {

    protected static final String PATH = ServletConstants.SECURE_SERVLETS_PATH + "solutionproductlookup";

    protected static final ObjectMapper jsonObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) {
        try {
            final List<SolutionProductLookupModel> solutionProductLookupModels = this.getSolutionProductLookupModels(request.getResourceResolver());
            ResponseUtils.setContentTypeJson(response, StandardCharsets.UTF_8);
            response.getWriter().print(jsonObjectMapper.writeValueAsString(solutionProductLookupModels));
        } catch (final Exception ex) {
            log.error("Exception in doGet method", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private List<SolutionProductLookupModel> getSolutionProductLookupModels(@NotNull final ResourceResolver resourceResolver) {
        final List<SolutionProductLookupModel> result = new ArrayList<>();
        final Resource solutionProductLookupRootResource = resourceResolver.getResource(SolutionProductLookupDataProcessor.DATA_ROOT_PATH);
        if (solutionProductLookupRootResource != null) {
            final Iterator<Resource> productLookupResourceIterator = solutionProductLookupRootResource.listChildren();
            while (productLookupResourceIterator.hasNext()) {
                result.add(productLookupResourceIterator.next().adaptTo(SolutionProductLookupModel.class));
            }
        }
        return result;
    }

}