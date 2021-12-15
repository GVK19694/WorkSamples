package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Formulation;
import com.ibm.watsonhealth.micromedex.core.utils.PageUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Formulation.class, ValidationModel.class }, resourceType = {
  FormulationImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FormulationImpl extends AbstractValidationModel implements Formulation {

    static final String RESOURCE_TYPE = ResourceTypeConstants.FORMULATION;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean displayFormulation;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String formulationDescription;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String formulationTradenames;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean addPhDetails;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String osmolality;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> osmolalityReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String phMin;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> phMinReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String phEffects;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> phEffectsReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String phMax;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> phMaxReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String phRange;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> phRangeReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String phMean;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> phMeanReferences;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean addStorageDetails;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String storage;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> storageReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String freezing;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> freezingReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String lightEffects;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> lightEffectsReferences;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean addStabilityDetails;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stability;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> stabilityReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stabilityMax;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> stabilityMaxReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String sorption;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> sorptionReferences;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean addAdditionalInformation;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String filtration;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> filtrationReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String sodiumContent;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> sodiumContentReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String otherInformation;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> otherInformationReferences;

    private List<String> osmolalityReferenceNames = new ArrayList<>();
    private List<String> phMinReferenceNames = new ArrayList<>();
    private List<String> phEffectsReferenceNames = new ArrayList<>();
    private List<String> phMaxReferenceNames = new ArrayList<>();
    private List<String> phRangeReferenceNames = new ArrayList<>();
    private List<String> phMeanReferenceNames = new ArrayList<>();
    private List<String> storageReferenceNames = new ArrayList<>();
    private List<String> freezingReferenceNames = new ArrayList<>();
    private List<String> lightEffectsReferenceNames = new ArrayList<>();
    private List<String> stabilityReferenceNames = new ArrayList<>();
    private List<String> stabilityMaxReferenceNames = new ArrayList<>();
    private List<String> sorptionReferenceNames = new ArrayList<>();
    private List<String> filtrationReferenceNames = new ArrayList<>();
    private List<String> sodiumContentReferenceNames = new ArrayList<>();
    private List<String> otherInformationReferenceNames = new ArrayList<>();

    @Override
    protected void customValidation() {
        this.setValid(true);
        if (StringUtils.isBlank(this.formulationDescription)) {
            this.setValid(false);
            this.addError("'formulation description' must not be empty.");
        }
    }

    @Override
    public void initBeforeValidation() {
        // nothing to do
    }

    @Override
    public void init() throws InitException {
        this.osmolalityReferenceNames = PageUtils.getPageTitlesFromPathList(this.osmolalityReferences, this.resourceResolver);
        this.phMinReferenceNames = PageUtils.getPageTitlesFromPathList(this.phMinReferences, this.resourceResolver);
        this.phEffectsReferenceNames = PageUtils.getPageTitlesFromPathList(this.phEffectsReferences, this.resourceResolver);
        this.phMaxReferenceNames = PageUtils.getPageTitlesFromPathList(this.phMaxReferences, this.resourceResolver);
        this.phRangeReferenceNames = PageUtils.getPageTitlesFromPathList(this.phRangeReferences, this.resourceResolver);
        this.phMeanReferenceNames = PageUtils.getPageTitlesFromPathList(this.phMeanReferences, this.resourceResolver);
        this.storageReferenceNames = PageUtils.getPageTitlesFromPathList(this.storageReferences, this.resourceResolver);
        this.freezingReferenceNames = PageUtils.getPageTitlesFromPathList(this.freezingReferences, this.resourceResolver);
        this.lightEffectsReferenceNames = PageUtils.getPageTitlesFromPathList(this.lightEffectsReferences, this.resourceResolver);
        this.stabilityReferenceNames = PageUtils.getPageTitlesFromPathList(this.stabilityReferences, this.resourceResolver);
        this.stabilityMaxReferenceNames = PageUtils.getPageTitlesFromPathList(this.stabilityMaxReferences, this.resourceResolver);
        this.sorptionReferenceNames = PageUtils.getPageTitlesFromPathList(this.sorptionReferences, this.resourceResolver);
        this.filtrationReferenceNames = PageUtils.getPageTitlesFromPathList(this.filtrationReferences, this.resourceResolver);
        this.sodiumContentReferenceNames = PageUtils.getPageTitlesFromPathList(this.sodiumContentReferences, this.resourceResolver);
        this.otherInformationReferenceNames = PageUtils.getPageTitlesFromPathList(this.otherInformationReferences, this.resourceResolver);
    }

    @Override
    public boolean isDisplayFormulation() {
        return this.displayFormulation;
    }

    @Override
    public String getFormulationDescription() {
        return this.formulationDescription;
    }

    @Override
    public String getFormulationTradenames() {
        return this.formulationTradenames;
    }

    @Override
    public boolean isAddPhDetails() {
        return this.addPhDetails;
    }

    @Override
    public String getOsmolality() {
        return this.addPhDetails ? this.osmolality : StringUtils.EMPTY;
    }

    @Override
    public List<String> getOsmolalityReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.osmolalityReferences) : new ArrayList<>();
    }

    @Override
    public String getPhMin() {
        return this.addPhDetails ? this.phMin : StringUtils.EMPTY;
    }

    @Override
    public List<String> getPhMinReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.phMinReferences) : new ArrayList<>();
    }

    @Override
    public String getPhEffects() {
        return this.addPhDetails ? this.phEffects : StringUtils.EMPTY;
    }

    @Override
    public List<String> getPhEffectsReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.phEffectsReferences) : new ArrayList<>();
    }

    @Override
    public String getPhMax() {
        return this.addPhDetails ? this.phMax : StringUtils.EMPTY;
    }

    @Override
    public List<String> getPhMaxReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.phMaxReferences) : new ArrayList<>();
    }

    @Override
    public String getPhRange() {
        return this.addPhDetails ? this.phRange : StringUtils.EMPTY;
    }

    @Override
    public List<String> getPhRangeReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.phRangeReferences) : new ArrayList<>();
    }

    @Override
    public String getPhMean() {
        return this.addPhDetails ? this.phMean : StringUtils.EMPTY;
    }

    @Override
    public List<String> getPhMeanReferences() {
        return this.addPhDetails ? this.getReferenceFieldData(this.phMeanReferences) : new ArrayList<>();
    }

    @Override
    public boolean isAddStorageDetails() {
        return this.addStorageDetails;
    }

    @Override
    public String getStorage() {
        return this.addStorageDetails ? this.storage : StringUtils.EMPTY;
    }

    @Override
    public List<String> getStorageReferences() {
        return this.addStorageDetails ? this.getReferenceFieldData(this.storageReferences) : new ArrayList<>();
    }

    @Override
    public String getFreezing() {
        return this.addStorageDetails ? this.freezing : StringUtils.EMPTY;
    }

    @Override
    public List<String> getFreezingReferences() {
        return this.addStorageDetails ? this.getReferenceFieldData(this.freezingReferences) : new ArrayList<>();
    }

    @Override
    public String getLightEffects() {
        return this.addStorageDetails ? this.lightEffects : StringUtils.EMPTY;
    }

    @Override
    public List<String> getLightEffectsReferences() {
        return this.addStorageDetails ? this.getReferenceFieldData(this.lightEffectsReferences) : new ArrayList<>();
    }

    @Override
    public boolean isAddStabilityDetails() {
        return this.addStabilityDetails;
    }

    @Override
    public String getStability() {
        return this.addStabilityDetails ? this.stability : StringUtils.EMPTY;
    }

    @Override
    public List<String> getStabilityReferences() {
        return this.addStabilityDetails ? this.getReferenceFieldData(this.stabilityReferences) : new ArrayList<>();
    }

    @Override
    public String getStabilityMax() {
        return this.addStabilityDetails ? this.stabilityMax : StringUtils.EMPTY;
    }

    @Override
    public List<String> getStabilityMaxReferences() {
        return this.addStabilityDetails ? this.getReferenceFieldData(this.stabilityMaxReferences) : new ArrayList<>();
    }

    @Override
    public String getSorption() {
        return this.addStabilityDetails ? this.sorption : StringUtils.EMPTY;
    }

    @Override
    public List<String> getSorptionReferences() {
        return this.addStabilityDetails ? this.getReferenceFieldData(this.sorptionReferences) : new ArrayList<>();
    }

    @Override
    public boolean isAddAdditionalInformation() {
        return this.addAdditionalInformation;
    }

    @Override
    public String getFiltration() {
        return this.addAdditionalInformation ? this.filtration : StringUtils.EMPTY;
    }

    @Override
    public List<String> getFiltrationReferences() {
        return this.addAdditionalInformation ? this.getReferenceFieldData(this.filtrationReferences) : new ArrayList<>();
    }

    @Override
    public String getSodiumContent() {
        return this.addAdditionalInformation ? this.sodiumContent : StringUtils.EMPTY;
    }

    @Override
    public List<String> getSodiumContentReferences() {
        return this.addAdditionalInformation ? this.getReferenceFieldData(this.sodiumContentReferences) : new ArrayList<>();
    }

    @Override
    public String getOtherInformation() {
        return this.addAdditionalInformation ? this.otherInformation : StringUtils.EMPTY;
    }

    @Override
    public List<String> getOtherInformationReferences() {
        return this.addAdditionalInformation ? this.getReferenceFieldData(this.otherInformationReferences) : new ArrayList<>();
    }

    private List<String> getReferenceFieldData(final List<String> referenceField) {
        return referenceField == null ? new ArrayList<>() : referenceField.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<String> getOsmolalityReferenceNames() {
        return this.addPhDetails ? this.osmolalityReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getPhMinReferenceNames() {
        return this.addPhDetails ? this.phMinReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getPhEffectsReferenceNames() {
        return this.addPhDetails ? this.phEffectsReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getPhMaxReferenceNames() {
        return this.addPhDetails ? this.phMaxReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getPhRangeReferenceNames() {
        return this.addPhDetails ? this.phRangeReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getPhMeanReferenceNames() {
        return this.addPhDetails ? this.phMeanReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getStorageReferenceNames() {
        return this.addStorageDetails ? this.storageReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getFreezingReferenceNames() {
        return this.addStorageDetails ? this.freezingReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getLightEffectsReferenceNames() {
        return this.addStorageDetails ? this.lightEffectsReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getStabilityReferenceNames() {
        return this.addStabilityDetails ? this.stabilityReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getStabilityMaxReferenceNames() {
        return this.addStabilityDetails ? this.stabilityMaxReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getSorptionReferenceNames() {
        return this.addStabilityDetails ? this.sorptionReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getFiltrationReferenceNames() {
        return this.addAdditionalInformation ? this.filtrationReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getSodiumContentReferenceNames() {
        return this.addAdditionalInformation ? this.sodiumContentReferenceNames : new ArrayList<>();
    }

    @Override
    public List<String> getOtherInformationReferenceNames() {
        return this.addAdditionalInformation ? this.otherInformationReferenceNames : new ArrayList<>();
    }

}
