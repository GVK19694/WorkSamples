package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.DrugSource;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.SecondaryDrug;
import com.ibm.watsonhealth.micromedex.core.services.RandomIdService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;
import com.ibm.watsonhealth.micromedex.core.utils.PageUtils;
import com.ibm.watsonhealth.micromedex.core.utils.TemplateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { SecondaryDrug.class, ValidationModel.class }, resourceType = {
  SecondaryDrugImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecondaryDrugImpl extends AbstractSolution implements SecondaryDrug {

    static final String RESOURCE_TYPE = ResourceTypeConstants.SECONDARY_DRUG;

    @SuppressWarnings("java:S1075")
    protected static final String RECORD_ROOT_PATH = "/content/mdx-cem/iv-compatibility/records";
    protected static final String PROPERTY_NAME_RECORD_ID = "recordId";
    protected static final int RECORD_ID_MIN_VALUE = 1000000; //inclusive
    protected static final int RECORD_ID_MAX_VALUE = 10000000; //exclusive

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private RandomIdService randomIdService;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private Long recordId;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondaryDrugSource;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondaryDrugPath;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondaryDrugType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> secondaryDrugs;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> alternativeNames;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondaryDrugConcentration;

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

    private String secondaryDrugName;
    private DrugSource secondaryDrugSourceEnum;
    private FormulationType secondaryDrugTypeEnum;
    private ProductMonograph linkedProductMonograph;

    @Override
    public void initBeforeValidation() {
        this.secondaryDrugSourceEnum = DrugSource.getDrugSource(this.secondaryDrugSource);
        if (this.secondaryDrugSourceEnum == DrugSource.NEW) {
            this.secondaryDrugTypeEnum = FormulationType.getFormulationType(this.secondaryDrugType);
        }

        if (this.isMonographSecondaryDrugSource()) {
            final Resource linkedMonographResource = this.resourceResolver.getResource(this.secondaryDrugPath);
            if (linkedMonographResource != null) {
                this.secondaryDrugName = PageUtils.getPageTitleFromPath(this.secondaryDrugPath, this.resourceResolver).orElse(StringUtils.EMPTY);
                this.linkedProductMonograph = linkedMonographResource.adaptTo(ProductMonograph.class);
            }
        }

    }

    @Override
    protected void customValidation() {
        this.setValid(true);
        this.validateSecondaryDrugNames();
        this.validateSolution();

        final Optional<CombinationType> combinationType = this.getCombinationType();
        if (combinationType.isPresent()) {
            if ((combinationType.get() == CombinationType.Y_SITE || combinationType.get() == CombinationType.SYRINGE) && !this.solutionEnabled) {
                this.setValid(false);
                this.addError("'vehicle' has to be filled out when combination type '" + combinationType.get().getDisplayName() + "' is selected");
            } else if ((combinationType.get() == CombinationType.ADMIXTURE) && this.solutionEnabled) {
                this.setValid(false);
                this.addError("'vehicle' must not be filled out when combination type '" + combinationType.get().getDisplayName() + "' is selected");
            }
        }
    }

    private void validateSecondaryDrugNames() {
        if (this.secondaryDrugSourceEnum == DrugSource.EXISTING_PRODUCT_MONOGRAPH) {
            if (StringUtils.isBlank(this.secondaryDrugPath)) {
                this.setValid(false);
                this.addError("'secondary drug path' must not be empty");
            }
            if (this.linkedProductMonograph == null || this.linkedProductMonograph.getSubstance() == null || !this.linkedProductMonograph
              .getSubstance()
              .isValid()) {
                this.setValid(false);
                this.addError("the selected 'secondary drug' is not valid");
            }
            if (!TemplateUtils.isTemplateType(this.secondaryDrugPath, this.resourceResolver, TemplateConstants.IV_COMPATIBILITY_PRODUCT)) {
                this.setValid(false);
                this.addError("the selected secondary drug path is not a product monograph page");
            }
        } else if (this.secondaryDrugSourceEnum == DrugSource.NEW) {
            if ((this.secondaryDrugTypeEnum == FormulationType.SINGLE_SUBSTANCE || this.secondaryDrugTypeEnum == FormulationType.GROUPING_NAME) && this.numSecondaryDrugs() != 1) {
                this.setValid(false);
                this.addError("exactly 1 'secondary drug' item is mandatory with the selected 'secondary drug type'.");
            } else if (this.secondaryDrugTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.numSecondaryDrugs() < 2) {
                this.setValid(false);
                this.addError("at least 2 'secondary drug' items are mandatory with the selected 'secondary drug type'.");
            }
        }
    }

    private long numSecondaryDrugs() {
        return Optional.ofNullable(this.secondaryDrugs).orElse(new ArrayList<>()).stream().filter(StringUtils::isNotBlank).count();
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
            final int generatedId = this.randomIdService.saveAndGetId(RECORD_ID_MIN_VALUE, RECORD_ID_MAX_VALUE, RECORD_ROOT_PATH,
              PROPERTY_NAME_RECORD_ID, ResourceTypeConstants.SECONDARY_DRUG, this.getCurrentResource());
            this.recordId = (long) generatedId;
        }
    }

    private Optional<CombinationType> getCombinationType() {
        final Optional<Resource> compatibilityResource = Optional.ofNullable(this.getCurrentResource().getParent()).map(Resource::getParent);
        if (compatibilityResource.isPresent()) {
            final ValueMap valueMap = compatibilityResource.get().getValueMap();
            if (valueMap.containsKey(CompatibilityImpl.PN_COMBINATION_TYPE)) {
                return Optional.of(CombinationType.getCombinationType(valueMap.get(CompatibilityImpl.PN_COMBINATION_TYPE, String.class)));
            }
        }
        return Optional.empty();
    }

    @Override
    public long getRecordId() {
        return this.recordId;
    }

    @Override
    public DrugSource getSecondaryDrugSource() {
        return this.secondaryDrugSourceEnum;
    }

    @Override
    public boolean isMonographSecondaryDrugSource() {
        return this.secondaryDrugSourceEnum == DrugSource.EXISTING_PRODUCT_MONOGRAPH;
    }

    @Override
    public boolean isNewSecondaryDrugSource() {
        return this.secondaryDrugSourceEnum == DrugSource.NEW;
    }

    @Override
    public String getSecondaryDrugPath() {
        return this.isMonographSecondaryDrugSource() ? this.secondaryDrugPath : StringUtils.EMPTY;
    }

    @Override
    public ProductMonograph getLinkedProductMonograph() {
        return this.isMonographSecondaryDrugSource() ? this.linkedProductMonograph : null;
    }

    @Override
    public FormulationType getSecondaryDrugType() {
        return this.isNewSecondaryDrugSource() ? this.secondaryDrugTypeEnum : null;
    }

    @Override
    public List<String> getSecondaryDrugs() {
        return this.isNewSecondaryDrugSource() ?
                 this.secondaryDrugs.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()) :
                 new ArrayList<>();
    }

    @Override
    public List<String> getAlternativeNames() {
        if (this.isNewSecondaryDrugSource() && this.secondaryDrugTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.alternativeNames != null) {
            return this.alternativeNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getSecondaryDrugConcentration() {
        return this.secondaryDrugConcentration;
    }

    @Override
    public String getManufacturer() {
        return this.manufacturer;
    }

    @Override
    public String getTradeName() {
        return this.tradeName;
    }

    @Override
    public String getStudyPeriod() {
        return this.studyPeriod;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getContainer() {
        return this.container;
    }

    @Override
    public String getStorage() {
        return this.storage;
    }

    @Override
    public String getSecondaryDrugName() {
        return this.secondaryDrugName;
    }

}
