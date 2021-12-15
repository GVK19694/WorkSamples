package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Compatibility;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Concentration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Concentration.class, ValidationModel.class }, resourceType = {
  ConcentrationImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ConcentrationImpl extends AbstractValidationModel implements Concentration {

    static final String RESOURCE_TYPE = ResourceTypeConstants.CONCENTRATION;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String concentration;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String studyNotes;

    private List<Compatibility> compatibilities;

    @Override
    protected void customValidation() {
        this.setValid(true);
        if (StringUtils.isBlank(this.concentration)) {
            this.setValid(false);
            this.addError("'concentration' must not be empty.");
        }
        if (this.compatibilities.isEmpty()) {
            this.setValid(false);
            this.addError("at least one compatibility component is mandatory");
        }
    }

    @Override
    public void initBeforeValidation() {
        this.compatibilities = this.childModels
          .stream()
          .filter(Compatibility.class::isInstance)
          .map(Compatibility.class::cast)
          .collect(Collectors.toList());
    }

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public String getConcentration() {
        return this.concentration;
    }

    @Override
    public String getStudyNotes() {
        return this.studyNotes;
    }

    @Override
    public List<Compatibility> getCompatibilities() {
        return this.compatibilities;
    }

}
