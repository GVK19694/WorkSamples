package com.ibm.watsonhealth.micromedex.core.servlets.datasources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ibm.watsonhealth.micromedex.core.constants.ServletConstants;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.RunModeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
  "sling.servlet.paths=" + SolutionLookupDataSourceServlet.SERVLET_PATH })
public class SolutionLookupDataSourceServlet extends SlingSafeMethodsServlet {

    static final String SERVLET_PATH = ServletConstants.DEFAULT_SERVLETS_PATH + "solutionlookupdatasource";

    @Reference
    private transient SlingSettingsService slingSettingsService;

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) {
        try {
            if (RunModeUtils.isAuthor(this.slingSettingsService)) {
                final Resource solutionLookupRootResource = request
                  .getResourceResolver()
                  .getResource(SolutionProductLookupDataProcessor.DATA_ROOT_PATH);
                if (solutionLookupRootResource != null) {
                    final Map<String, String> dataMap = this.getSolutionDataMap(solutionLookupRootResource);
                    final List<String> keys = new ArrayList<>(dataMap.keySet());
                    Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

                    final List<Resource> datasourceList = new ArrayList<>();
                    ValueMap valueMapDecorator;
                    for (final String key : keys) {
                        valueMapDecorator = new ValueMapDecorator(new HashMap<>());
                        valueMapDecorator.put("text", key);
                        valueMapDecorator.put("value", dataMap.get(key));
                        datasourceList.add(
                          new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), "nt:unstructured", valueMapDecorator));
                    }

                    final DataSource dataSource = new SimpleDataSource(datasourceList.iterator());
                    request.setAttribute(DataSource.class.getName(), dataSource);
                }
            } else {
                response.setStatus(HttpStatus.SC_NOT_FOUND);
            }
        } catch (final RuntimeException ex) {
            log.error("Exception in doGet method", ex);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    private Map<String, String> getSolutionDataMap(@NotNull final Resource solutionLookupRootResource) {
        final Iterator<Resource> childrenResourcesIterator = solutionLookupRootResource.listChildren();

        Resource childResource;
        ValueMap valueMap;
        String text;
        final Map<String, String> dataMap = new HashMap<>();
        while (childrenResourcesIterator.hasNext()) {
            childResource = childrenResourcesIterator.next();
            valueMap = childResource.getValueMap();
            if (valueMap.containsKey(SolutionProductLookupDataProcessor.PROPERTY_NAME)) {
                text = valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_NAME, "");
                if (StringUtils.isNotBlank(text)) {
                    dataMap.put(text, childResource.getPath());
                }
            }
        }
        return dataMap;
    }

}