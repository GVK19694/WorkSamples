package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Pagevalidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Pagevalidation.class, ValidationModel.class }, resourceType = {
  PagevalidationImpl.RESOURCE_TYPE_PAGEVALIDATION,
  PagevalidationImpl.RESOURCE_TYPE_PAGE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PagevalidationImpl extends AbstractValidationModel implements Pagevalidation {

    static final String RESOURCE_TYPE_PAGEVALIDATION = ResourceTypeConstants.PAGEVALIDATION;
    static final String RESOURCE_TYPE_PAGE = ResourceTypeConstants.IV_COMPATIBILITY_PAGE;

    private static final int MIN_ALLOWED_PRIMARY_DRUG_COMPONENTS = 1;
    private static final int MAX_ALLOWED_PRIMARY_DRUG_COMPONENTS = 1;
    private static final int MIN_ALLOWED_SUBSTANCE_COMPONENTS = 1;
    private static final int MAX_ALLOWED_SUBSTANCE_COMPONENTS = 1;

    @OSGiService
    private PageManagerFactory pageManagerFactory;

    @Self
    private SlingHttpServletRequest request;

    @Override
    public String getTitle() {
        final PageManager pageManager = this.pageManagerFactory.getPageManager(this.request.getResourceResolver());
        final Page currentPage = pageManager.getContainingPage(this.request.getResource());
        return currentPage.getTitle();
    }

    @Override
    protected void customValidation() {
        this.setValid(true);

        final String templatePath = this.getTemplatePath();
        if (StringUtils.equals(templatePath, TemplateConstants.IV_COMPATIBILITY_RECORD)) {
            final long numPrimaryDrugComponents = this.getNumChildComponents(ResourceTypeConstants.PRIMARY_DRUG);
            if (numPrimaryDrugComponents < MIN_ALLOWED_PRIMARY_DRUG_COMPONENTS || numPrimaryDrugComponents > MAX_ALLOWED_PRIMARY_DRUG_COMPONENTS) {
                this.setValid(false);
                this.addError(
                  "Number of primary drug components must be between " + MIN_ALLOWED_PRIMARY_DRUG_COMPONENTS + " and " + MAX_ALLOWED_PRIMARY_DRUG_COMPONENTS);
            }
        } else if (StringUtils.equals(templatePath, TemplateConstants.IV_COMPATIBILITY_PRODUCT)) {
            final long numSubstanceComponents = this.getNumChildComponents(ResourceTypeConstants.SUBSTANCE);
            if (numSubstanceComponents < MIN_ALLOWED_SUBSTANCE_COMPONENTS || numSubstanceComponents > MAX_ALLOWED_SUBSTANCE_COMPONENTS) {
                this.setValid(false);
                this.addError(
                  "Number of substance components must be between " + MIN_ALLOWED_SUBSTANCE_COMPONENTS + " and " + MAX_ALLOWED_SUBSTANCE_COMPONENTS);
            }
        }
    }

    private long getNumChildComponents(final String resourceType) {
        return this.childResources.stream().filter(childResource -> StringUtils.equals(childResource.getResourceType(), resourceType)).count();
    }

    @Override
    public void initBeforeValidation() {
        // nothing to do
    }

    @Override
    public void init() {
        // nothing to do
    }

    private String getTemplatePath() {
        final PageManager pageManager = this.pageManagerFactory.getPageManager(this.getCurrentResource().getResourceResolver());
        final Page currentPage = pageManager.getContainingPage(this.getCurrentResource());
        return currentPage.getTemplate().getPath();
    }

}
