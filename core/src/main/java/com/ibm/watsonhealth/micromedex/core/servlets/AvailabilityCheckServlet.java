package com.ibm.watsonhealth.micromedex.core.servlets;

import java.nio.charset.StandardCharsets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;
import com.ibm.watsonhealth.micromedex.core.servlets.configurations.HealthCheckExecutorServletConfiguration;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Availability Check Servlet",
  "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + AvailabilityCheckServlet.SERVLET_PATH })
@Designate(ocd = HealthCheckExecutorServletConfiguration.class)
public class AvailabilityCheckServlet extends SlingSafeMethodsServlet {

    protected static final String SERVLET_PATH = ServletConstants.DEFAULT_SERVLETS_PATH + "availability";

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws ServletException {
        try {
            ResponseUtils.setContentTypeText(response, StandardCharsets.UTF_8);
            ResponseUtils.disableDispatcherAndBrowserCache(response);
            response.getWriter().print("ok");
        } catch (final Exception ex) {
            log.error("error in servlet", ex);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException(ex);
        }
    }

}
