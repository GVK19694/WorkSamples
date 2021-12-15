package com.ibm.watsonhealth.micromedex.core.servlets.imports.api;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;
import com.ibm.watsonhealth.micromedex.core.servlets.imports.api.exceptions.RootResourceInitializationException;
import com.ibm.watsonhealth.micromedex.core.utils.exceptions.SessionNotAvailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Servlet to upload redbook data into AEM",
  "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + RedBookImportServlet.SERVLET_PATH,
  "sling.servlet.extensions=" + AbstractImportServlet.SERVLET_EXTENSION })
public class RedBookImportServlet extends AbstractImportServlet {

    private static final String UPLOAD_SERVLET_SUBPATH = "redbook";
    protected static final String SERVLET_PATH = ServletConstants.IMPORT_API_PATH + UPLOAD_SERVLET_SUBPATH;

    public static final String IMPORT_PATH = IMPORT_ROOT_PATH + UPLOAD_SERVLET_SUBPATH;

    @Reference
    private transient SlingSettingsService slingSettingsService;

    @NotNull
    @Override
    protected Resource initRootPath(final @NotNull SlingHttpServletRequest request) throws SessionNotAvailableException, RepositoryException, RootResourceInitializationException {
        return this.initRootPath(request, IMPORT_PATH);
    }

    @Override
    protected SlingSettingsService getSlingSettingsService() {
        return this.slingSettingsService;
    }

}
