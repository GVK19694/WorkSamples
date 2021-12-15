package com.ibm.watsonhealth.micromedex.core.models.impl;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.settings.SlingSettingsService;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.Page;
import com.ibm.watsonhealth.micromedex.core.utils.RunModeUtils;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Page.class, }, resourceType = PageImpl.RESOURCE_TYPE)
public class PageImpl implements Page {

    static final String RESOURCE_TYPE = ResourceTypeConstants.PAGE;

    @ScriptVariable
    private SlingHttpServletResponse response;

    @OSGiService
    private SlingSettingsService slingSettingsService;

    private boolean show = false;

    @Override
    public boolean isShow() {
        return this.show;
    }

    @PostConstruct
    public void init() {
        this.show = RunModeUtils.isAuthor(this.slingSettingsService);
        if (!this.show) {
            this.response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
