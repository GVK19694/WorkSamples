package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.PrimaryDrug;
import com.ibm.watsonhealth.micromedex.core.services.impl.ValidationServiceImpl;
import com.ibm.watsonhealth.micromedex.core.utils.DateTimeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Record {

    @Self
    private Resource resource;

    @Self
    private Page page;

    private PrimaryDrug primaryDrug;

    @ValueMapValue
    @Named(NameConstants.NN_CONTENT + "/" + NameConstants.PN_PAGE_LAST_MOD)
    private Calendar lastUpdated;

    @ValueMapValue
    @Named(NameConstants.NN_CONTENT + "/" + NameConstants.PN_PAGE_LAST_MOD_BY)
    private String lastUpdatedBy;

    @PostConstruct
    public void init() {
        try {
            final Optional<Resource> primaryDrugResource = this.getPrimaryDrugResource();
            primaryDrugResource.ifPresent(value -> this.primaryDrug = value.adaptTo(PrimaryDrug.class));
        } catch (final RuntimeException ex) {
            log.error("exception on initializing model", ex);
        }
    }

    private Optional<Resource> getPrimaryDrugResource() {
        final Resource firstContainerResource = this.page
          .getContentResource()
          .getChild(ValidationServiceImpl.ROOT_NODE_NAME + "/" + ValidationServiceImpl.CONTAINER_NODE_NAME);
        if (firstContainerResource != null) {
            return IteratorUtils
              .toList(firstContainerResource.listChildren())
              .stream()
              .filter(childResource -> StringUtils.equals(childResource.getResourceType(), ResourceTypeConstants.PRIMARY_DRUG))
              .findFirst();
        }
        return Optional.empty();
    }

    public PrimaryDrug getPrimaryDrug() {
        return this.primaryDrug;
    }

    public LocalDateTime getLastUpdated() {
        if (this.lastUpdated == null) {
            return null;
        }
        return DateTimeUtils.convertCalendar2LocalDateTime(this.lastUpdated);
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

}
