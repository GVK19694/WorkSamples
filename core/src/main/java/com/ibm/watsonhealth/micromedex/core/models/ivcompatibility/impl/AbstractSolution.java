package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Solution;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;

public abstract class AbstractSolution extends AbstractValidationModel implements Solution {

    @ValueMapValue
    protected boolean solutionEnabled;

    @ValueMapValue
    protected String solutionPath;

    @ValueMapValue
    protected String solutionManufacturer;

    @ValueMapValue
    protected String solutionTradename;

    @ValueMapValue
    protected String solutionVolume;

    protected String solutionName = StringUtils.EMPTY;

    @Override
    public boolean isSolutionEnabled() {
        return this.solutionEnabled;
    }

    @Override
    public String getSolutionPath() {
        return this.solutionPath;
    }

    @Override
    public String getSolutionName() {
        if (StringUtils.isBlank(this.solutionName)) {
            if (StringUtils.isBlank(this.solutionPath)) {
                return StringUtils.EMPTY;
            }
            final Resource solutionResource = this.getCurrentResource().getResourceResolver().getResource(this.solutionPath);
            if (solutionResource == null) {
                return StringUtils.EMPTY;
            }
            final ValueMap valueMap = solutionResource.getValueMap();
            return valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_NAME, StringUtils.EMPTY);
        } else {
            return this.solutionName;
        }
    }

    @Override
    public String getSolutionManufacturer() {return this.solutionManufacturer;}

    @Override
    public String getSolutionTradename() {return this.solutionTradename;}

    @Override
    public String getSolutionVolume() {return this.solutionVolume;}

    protected void validateSolution() {
        if (this.solutionEnabled && StringUtils.isBlank(this.getSolutionName())) {
            this.setValid(false);
            this.addError("'name' must not be empty");
        }
    }

}
