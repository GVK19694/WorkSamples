package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CompatibilityType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Compatibility;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.SecondaryDrug;
import com.ibm.watsonhealth.micromedex.core.services.RandomIdService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Compatibility.class, ValidationModel.class }, resourceType = {
  CompatibilityImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CompatibilityImpl extends AbstractSolution implements Compatibility {

    static final String RESOURCE_TYPE = ResourceTypeConstants.COMPATIBILITY;

    public static final String PN_COMBINATION_TYPE = "combinationType";

    @OSGiService
    private RandomIdService randomIdService;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private Long recordId;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String compatible;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String physicalCompatibility;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String chemicalStability;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String combinationType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String recordNotes;

    private List<SecondaryDrug> secondaryDrugs;
    private CompatibilityType compatibleEnum;
    private CombinationType combinationTypeEnum;

    @Override
    public void initBeforeValidation() {
        this.secondaryDrugs = this.childModels
          .stream()
          .filter(SecondaryDrug.class::isInstance)
          .map(SecondaryDrug.class::cast)
          .collect(Collectors.toList());

        this.compatibleEnum = CompatibilityType.getCompatibilityType(this.compatible);
        this.combinationTypeEnum = CombinationType.getCombinationType(this.combinationType);
    }

    @Override
    protected void customValidation() {
        this.setValid(true);

        if (StringUtils.isBlank(this.compatible)) {
            this.setValid(false);
            this.addError("'compatible' must not be empty.");
        }
        if (StringUtils.isBlank(this.combinationType)) {
            this.setValid(false);
            this.addError("'combination type' must not be empty.");
        } else {
            if ((this.combinationTypeEnum == CombinationType.DRUG_SOLUTION) && CollectionUtils.isNotEmpty(this.secondaryDrugs)) {
                this.setValid(false);
                this.addError("No secondary drug component is allowed when '" + this.combinationTypeEnum.getDisplayName() + "' is selected");
            } else if (this.combinationTypeEnum != CombinationType.DRUG_SOLUTION && this.secondaryDrugs.isEmpty()) {
                this.setValid(false);
                this.addError("at least one secondary drug component is mandatory");
            }

            if ((this.combinationTypeEnum == CombinationType.ADMIXTURE || this.combinationTypeEnum == CombinationType.DRUG_SOLUTION) && !this.solutionEnabled) {
                this.setValid(false);
                this.addError(
                  "'solution' has to be filled out when combination type '" + this.combinationTypeEnum.getDisplayName() + "' is selected");
            } else if ((this.combinationTypeEnum == CombinationType.Y_SITE || this.combinationTypeEnum == CombinationType.SYRINGE) && this.solutionEnabled) {
                this.setValid(false);
                this.addError(
                  "'solution' must not be filled out when combination type '" + this.combinationTypeEnum.getDisplayName() + "' is selected");
            }
        }
        this.validateSolution();
    }

    @Override
    public void init() throws InitException {
        try {
            this.generateRecordId();
        } catch (final GenerateIdException ex) {
            throw new InitException(ex);
        }
    }

    private void generateRecordId() throws GenerateIdException {
        if (this.getCurrentResource() != null && this.recordId == 0) {
            final int generatedId = this.randomIdService.saveAndGetId(SecondaryDrugImpl.RECORD_ID_MIN_VALUE, SecondaryDrugImpl.RECORD_ID_MAX_VALUE,
              SecondaryDrugImpl.RECORD_ROOT_PATH, SecondaryDrugImpl.PROPERTY_NAME_RECORD_ID, ResourceTypeConstants.SECONDARY_DRUG,
              this.getCurrentResource());
            this.recordId = (long) generatedId;
        }
    }

    @Override
    public long getRecordId() {
        return this.recordId;
    }

    @Override
    public String getCompatible() {
        return this.compatible;
    }

    @Override
    public CompatibilityType getCompatibleEnum() {
        return this.compatibleEnum;
    }

    @Override
    public String getPhysicalCompatibility() {
        return this.physicalCompatibility;
    }

    @Override
    public String getChemicalStability() {
        return this.chemicalStability;
    }

    @Override
    public String getCombinationType() {
        return this.combinationType;
    }

    @Override
    public CombinationType getCombinationTypeEnum() {
        return this.combinationTypeEnum;
    }

    @Override
    public String getRecordNotes() {
        return this.recordNotes;
    }

    @Override
    public List<SecondaryDrug> getSecondaryDrugs() {
        return this.secondaryDrugs;
    }

}
