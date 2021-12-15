package com.ibm.watsonhealth.micromedex.core.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.watsonhealth.micromedex.core.auth.OidcAuthenticationHandler;
import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Deletes all cached files in the dispatcher cache",
  "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + ServletConstants.DEFAULT_SERVLETS_PATH + "ibmsinglesignonservlet",
  "sling.servlet.extensions=html" })
public class SingleSignOnServlet extends SlingSafeMethodsServlet {

    @Reference(policyOption = ReferencePolicyOption.GREEDY, policy = ReferencePolicy.STATIC)
    private transient List<AuthenticationHandler> authenticationHandlers;

    private transient OidcAuthenticationHandler oidcAuthenticationHandler;

    @Activate
    @Modified
    public void activate() {
        try {
            log.info("activate");
            this.authenticationHandlers
              .stream()
              .filter(OidcAuthenticationHandler.class::isInstance)
              .findFirst()
              .ifPresent(handler -> this.oidcAuthenticationHandler = (OidcAuthenticationHandler) handler);
            log.info("oidcAuthenticationHandler: {}", this.oidcAuthenticationHandler);
        } catch (final RuntimeException ex) {
            log.error("exception on activating single sign on servlet", ex);
        }
    }

    @Deactivate
    public void deactivate() {
        log.info("deactivate");
        this.oidcAuthenticationHandler = null;
    }

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException {
        try {
            final String redirectUrl = this.oidcAuthenticationHandler.getSingleSignOnRedirectUrl(request, response);
            response.sendRedirect(redirectUrl);
        } catch (final RuntimeException ex) {
            log.error("exception on single sign on servlet", ex);
        }
    }

}
