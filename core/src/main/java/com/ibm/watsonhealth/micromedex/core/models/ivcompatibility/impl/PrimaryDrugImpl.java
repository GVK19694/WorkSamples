package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
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
import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.DrugSource;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Compatibility;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Concentration;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.PrimaryDrug;
import com.ibm.watsonhealth.micromedex.core.utils.PageUtils;
import com.ibm.watsonhealth.micromedex.core.utils.TemplateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { PrimaryDrug.class, ValidationModel.class }, resourceType = {
  PrimaryDrugImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PrimaryDrugImpl extends AbstractSolution implements PrimaryDrug {

    static final String RESOURCE_TYPE = ResourceTypeConstants.PRIMARY_DRUG;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String citationPath;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String primaryDrugSource;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String primaryDrugPath;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String primaryDrugType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> primaryDrugs;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> alternativeNames;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String manufacturer;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String tradeName;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String studyPeriod;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String method;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    @Named("containerfield")
    private String container;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String storage;

    private String citationName;
    private String primaryDrugName;
    private List<Concentration> concentrations;
    private DrugSource primaryDrugSourceEnum;
    private FormulationType primaryDrugTypeEnum;
    private ProductMonograph linkedProductMonograph;

    @Override
    public void initBeforeValidation() {
        this.concentrations = this.childModels
          .stream()
          .filter(Concentration.class::isInstance)
          .map(Concentration.class::cast)
          .collect(Collectors.toList());
        this.primaryDrugSourceEnum = DrugSource.getDrugSource(this.primaryDrugSource);
        if (this.primaryDrugSourceEnum == DrugSource.NEW) {
            this.primaryDrugTypeEnum = FormulationType.getFormulationType(this.primaryDrugType);
        }

        if (this.isMonographPrimaryDrugSource()) {
            final Resource linkedMonographResource = this.resourceResolver.getResource(this.primaryDrugPath);
            if (linkedMonographResource != null) {
                this.primaryDrugName = PageUtils.getPageTitleFromPath(this.primaryDrugPath, this.resourceResolver).orElse(StringUtils.EMPTY);
                this.linkedProductMonograph = linkedMonographResource.adaptTo(ProductMonograph.class);
            }
        }

        if (StringUtils.isNotBlank(this.citationPath)) {
            this.citationName = PageUtils.getPageTitleFromPath(this.citationPath, this.resourceResolver).orElse(StringUtils.EMPTY);
        }
    }

    @Override
    protected void customValidation() {
        this.setValid(true);
        if (StringUtils.isBlank(this.citationPath)) {
            this.setValid(false);
            this.addError("'citation path' must not be empty");
        }
        if (!TemplateUtils.isTemplateType(this.citationPath, this.resourceResolver, TemplateConstants.CITATION)) {
            this.setValid(false);
            this.addError("the selected citiation is not a citation page");
        }
        this.validatePrimaryDrugNames();
        this.validateSolution();
        final List<CombinationType> combinationTypes = this.getCombinationTypes();
        if (CollectionUtils.containsAny(combinationTypes, CombinationType.Y_SITE, CombinationType.SYRINGE) && !this.solutionEnabled) {
            this.setValid(false);
            this.addError("'vehicle' has to be filled out when combination type 'Y-Site' or 'Syringe' are selected");
        } else if (CollectionUtils.containsAny(
          combinationTypes, CombinationType.ADMIXTURE, CombinationType.DRUG_SOLUTION) && !CollectionUtils.containsAny(
          combinationTypes, CombinationType.Y_SITE, CombinationType.SYRINGE) && this.solutionEnabled) {
            this.setValid(false);
            this.addError("'vehicle' must not be filled out when combination type 'Admixture' or 'Drug-Solution' are selected");
        }

        if (this.concentrations.isEmpty()) {
            this.setValid(false);
            this.addError("at least one concentration component is mandatory");
        }
    }

    private void validatePrimaryDrugNames() {
        if (this.primaryDrugSourceEnum == DrugSource.EXISTING_PRODUCT_MONOGRAPH) {
            if (StringUtils.isBlank(this.primaryDrugPath)) {
                this.setValid(false);
                this.addError("'primary drug path' must not be empty");
            }
            if (this.linkedProductMonograph == null || this.linkedProductMonograph.getSubstance() == null || !this.linkedProductMonograph
              .getSubstance()
              .isValid()) {
                this.setValid(false);
                this.addError("the selected 'primary drug' is not valid");
            }
            if (!TemplateUtils.isTemplateType(this.primaryDrugPath, this.resourceResolver, TemplateConstants.IV_COMPATIBILITY_PRODUCT)) {
                this.setValid(false);
                this.addError("the selected primary drug path is not a product monograph page");
            }
        } else if (this.primaryDrugSourceEnum == DrugSource.NEW) {
            if ((this.primaryDrugTypeEnum == FormulationType.SINGLE_SUBSTANCE || this.primaryDrugTypeEnum == FormulationType.GROUPING_NAME) && this.numPrimaryDrugs() != 1) {
                this.setValid(false);
                this.addError("exactly 1 'primary drug' item is mandatory with the selected 'primary drug type'.");
            } else if (this.primaryDrugTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.numPrimaryDrugs() < 2) {
                this.setValid(false);
                this.addError("at least 2 'primary drug' items are mandatory with the selected 'primary drug type'.");
            }
        }
    }

    private long numPrimaryDrugs() {
        return Optional.ofNullable(this.primaryDrugs).orElse(new ArrayList<>()).stream().filter(StringUtils::isNotBlank).count();
    }

    @Override
    public void init() {
        // nothing to do
    }

    private List<CombinationType> getCombinationTypes() {
        final List<CombinationType> result = new ArrayList<>();
        final List<Concentration> concentrationModels = this.childModels
          .stream()
          .filter(Concentration.class::isInstance)
          .map(Concentration.class::cast)
          .collect(Collectors.toList());

        for (final Concentration concentration : concentrationModels) {
            for (final Compatibility compatibility : concentration.getCompatibilities()) {
                if (StringUtils.isNotBlank(compatibility.getCombinationType()) && !result.contains(compatibility.getCombinationTypeEnum())) {
                    result.add(compatibility.getCombinationTypeEnum());
                }
            }
        }
        return result;
    }

    @Override
    public String getCitationPath() {
        return this.citationPath;
    }

    @Override
    public String getCitationName() {
        return this.citationName;
    }

    @Override
    public DrugSource getPrimaryDrugSource() {
        return this.primaryDrugSourceEnum;
    }

    @Override
    public boolean isMonographPrimaryDrugSource() {
        return this.primaryDrugSourceEnum == DrugSource.EXISTING_PRODUCT_MONOGRAPH;
    }

    @Override
    public boolean isNewPrimaryDrugSource() {
        return this.primaryDrugSourceEnum == DrugSource.NEW;
    }

    @Override
    public String getPrimaryDrugPath() {
        return this.isMonographPrimaryDrugSource() ? this.primaryDrugPath : StringUtils.EMPTY;
    }

    @Override
    public String getPrimaryDrugName() {
        return this.primaryDrugName;
    }

    @Override
    public ProductMonograph getLinkedProductMonograph() {
        return this.isMonographPrimaryDrugSource() ? this.linkedProductMonograph : null;
    }

    @Override
    public FormulationType getPrimaryDrugType() {
        return this.isNewPrimaryDrugSource() ? this.primaryDrugTypeEnum : null;
    }

    @Override
    public List<String> getPrimaryDrugs() {
        return this.isNewPrimaryDrugSource() ?
                 this.primaryDrugs.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()) :
                 new ArrayList<>();
    }

    @Override
    public List<String> getAlternativeNames() {
        if (this.isNewPrimaryDrugSource() && this.primaryDrugTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.alternativeNames != null) {
            return this.alternativeNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getManufacturer() {return this.manufacturer;}

    @Override
    public String getTradeName() {return this.tradeName;}

    @Override
    public String getStudyPeriod() {return this.studyPeriod;}

    @Override
    public String getMethod() {return this.method;}

    @Override
    public String getContainer() {return this.container;}

    @Override
    public String getStorage() {return this.storage;}

    @Override
    public List<Concentration> getConcentrations() {
        return this.concentrations;
    }

}
