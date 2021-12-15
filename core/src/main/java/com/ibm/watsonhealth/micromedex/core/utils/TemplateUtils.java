package com.ibm.watsonhealth.micromedex.core.utils;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;

public final class TemplateUtils {

    private TemplateUtils() {
    }

    public static boolean isTemplateType(@NotNull final String pagePath, @NotNull final ResourceResolver resourceResolver, @NotNull final String template) {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return false;
        }
        final Page page = pageManager.getContainingPage(pagePath);
        if (page == null) {
            return false;
        }
        return isTemplateType(page, template);
    }

    public static boolean isTemplateType(@NotNull final Page page, @NotNull final String template) {
        return Optional
          .of(page)
          .map(Page::getTemplate)
          .map(Template::getPath)
          .map(pageTemplatePath -> StringUtils.isNotBlank(pageTemplatePath) && StringUtils.equals(pageTemplatePath, template))
          .orElse(false);
    }

    public static Optional<String> getTemplate(final @NotNull SlingHttpServletRequest request) {
        final ValueMap valueMap = request.getResource().getValueMap();
        if (valueMap.containsKey(JcrResourceConstants.NT_TEMPLATE)) {
            return Optional.ofNullable(valueMap.get(JcrResourceConstants.NT_TEMPLATE, String.class));
        }
        return Optional.empty();
    }

}
