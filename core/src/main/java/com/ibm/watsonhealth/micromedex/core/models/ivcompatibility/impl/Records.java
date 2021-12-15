package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.ibm.watsonhealth.micromedex.core.filters.ivcompatibility.RecordsFilter;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Records {

    @Self
    private Resource resource;

    @OSGiService
    private PageManagerFactory pageManagerFactory;

    private List<Record> recordModels;

    @PostConstruct
    public void init() {
        try {
            final PageManager pageManager = this.pageManagerFactory.getPageManager(this.resource.getResourceResolver());
            final Page currentPage = pageManager.getContainingPage(this.resource);
            final List<Page> recordPages = IteratorUtils.toList(currentPage.listChildren(new RecordsFilter(), true));
            final List<Page> validPages = this.getValidPages(recordPages);
            this.recordModels = validPages.stream().map(validPage -> validPage.adaptTo(Record.class)).collect(Collectors.toList());
        } catch (final RuntimeException ex) {
            log.error("exception on initializing model", ex);
        }
    }

    private List<Page> getValidPages(final List<Page> recordPages) {
        final List<Page> result = new ArrayList<>();
        ValidationModel validationModel;
        for (final Page page : recordPages) {
            validationModel = page.getContentResource().adaptTo(ValidationModel.class);
            if (validationModel != null && validationModel.isValid()) {
                result.add(page);
            }
        }
        return result;
    }

    public List<Record> getRecordModels() {
        return this.recordModels;
    }

}
