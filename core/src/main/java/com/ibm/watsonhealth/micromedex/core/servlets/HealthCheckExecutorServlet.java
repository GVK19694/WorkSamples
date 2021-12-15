package com.ibm.watsonhealth.micromedex.core.servlets;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.hc.api.execution.HealthCheckExecutionResult;
import org.apache.sling.hc.api.execution.HealthCheckExecutor;
import org.apache.sling.hc.api.execution.HealthCheckSelector;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;
import com.ibm.watsonhealth.micromedex.core.servlets.configurations.HealthCheckExecutorServletConfiguration;
import com.ibm.watsonhealth.micromedex.core.utils.ResponseUtils;
import com.ibm.watsonhealth.micromedex.core.vo.healthchecks.HealthCheckResultsVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Health Check Executor Servlet",
  "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + HealthCheckExecutorServlet.SERVLET_PATH })
@Designate(ocd = HealthCheckExecutorServletConfiguration.class)
public class HealthCheckExecutorServlet extends SlingSafeMethodsServlet {

    protected static final String SERVLET_PATH = ServletConstants.SECURE_SERVLETS_PATH + "health";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.enable(SerializationFeature.WRAP_ROOT_VALUE);
    }

    @Reference
    private transient HealthCheckExecutor healthCheckExecutor;

    private transient HealthCheckExecutorServletConfiguration config;

    @Activate
    @Modified
    public void activate(final HealthCheckExecutorServletConfiguration config) {
        this.config = config;
    }

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws ServletException {
        try {
            ResponseUtils.setContentTypeJson(response, StandardCharsets.UTF_8);
            ResponseUtils.disableDispatcherAndBrowserCache(response);

            final List<HealthCheckExecutionResult> results = this.healthCheckExecutor.execute(
              HealthCheckSelector.names(this.config.enabledHealthCheckNames()));
            final HealthCheckResultsVO healthCheckResults = new HealthCheckResultsVO();
            results.forEach(healthCheckResults::addHealthCheckResult);

            response.getWriter().print(OBJECT_MAPPER.writeValueAsString(healthCheckResults));
        } catch (final Exception ex) {
            log.error("error in servlet", ex);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException(ex);
        }
    }

}
