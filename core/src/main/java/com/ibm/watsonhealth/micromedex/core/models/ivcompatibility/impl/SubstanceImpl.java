package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Formulation;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.RandomIdService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.PageUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Substance.class, ValidationModel.class }, resourceType = {
  SubstanceImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubstanceImpl extends AbstractValidationModel implements Substance {

    static final String RESOURCE_TYPE = ResourceTypeConstants.SUBSTANCE;

    @SuppressWarnings("java:S1075")
    protected static final String PRODUCT_MONOGRAPH_ROOT_PATH = "/content/mdx-cem/iv-compatibility/product-monographs";
    protected static final String PROPERTY_NAME_PRODUCT_MONOGRAPH_ID = "productMonographId";
    protected static final int PRODUCT_MONOGRAPH_ID_MIN_VALUE = 1000000; //inclusive
    protected static final int PRODUCT_MONOGRAPH_ID_MAX_VALUE = 10000000; //exclusive

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private RandomIdService randomIdService;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private Long productMonographId;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String substanceType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String formulationType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> lexiconSubstanceDrugReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> lexiconSubstanceSolutionReferencePaths;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> lexiconSubstanceBothDrugReferences;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> lexiconSubstanceBothSolutionReferencePaths;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String lexiconSubstanceReferenceSource;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> substanceTradeNames;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> alternativeNames;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String reconstitution;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private List<String> reconstitutionReferences;

    private List<Formulation> formulations;
    private SubstanceType substanceTypeEnum;
    private SubstanceType lexiconSubstanceReferenceSourceEnum;
    private FormulationType formulationTypeEnum;
    private final List<String> lexiconSubstanceReferencePaths = new ArrayList<>();
    private final List<String> lexiconSubstanceReferences = new ArrayList<>();
    private List<String> reconstitutionReferenceNames = new ArrayList<>();

    @Override
    public void initBeforeValidation() throws InitException {
        try {
            this.generateProductId();

            this.formulations = this.childModels
              .stream()
              .filter(Formulation.class::isInstance)
              .map(Formulation.class::cast)
              .collect(Collectors.toList());

            if (StringUtils.isNotBlank(this.substanceType)) {
                this.substanceTypeEnum = SubstanceType.getSubstanceType(this.substanceType);
            }
            if (StringUtils.isNotBlank(this.formulationType)) {
                this.formulationTypeEnum = FormulationType.getFormulationType(this.formulationType);
            }
            if (this.substanceTypeEnum == SubstanceType.BOTH && StringUtils.isNotBlank(this.lexiconSubstanceReferenceSource)) {
                this.lexiconSubstanceReferenceSourceEnum = SubstanceType.getSubstanceType(this.lexiconSubstanceReferenceSource);
            }

            if (this.substanceTypeEnum == SubstanceType.SOLUTION && this.lexiconSubstanceSolutionReferencePaths != null) {
                this.lexiconSubstanceReferencePaths.addAll(this.lexiconSubstanceSolutionReferencePaths);
            } else if (this.substanceTypeEnum == SubstanceType.BOTH && this.lexiconSubstanceReferenceSourceEnum == SubstanceType.SOLUTION && this.lexiconSubstanceBothSolutionReferencePaths != null) {
                this.lexiconSubstanceReferencePaths.addAll(this.lexiconSubstanceBothSolutionReferencePaths);
            }

            this.reconstitutionReferenceNames = PageUtils.getPageTitlesFromPathList(this.reconstitutionReferences, this.resourceResolver);
        } catch (final GenerateIdException ex) {
            throw new InitException(ex);
        }
    }

    @Override
    protected void customValidation() {
        this.setValid(true);
        if (StringUtils.isBlank(this.substanceType)) {
            this.setValid(false);
            this.addError("'substance type' must not be empty.");
        }
        if (StringUtils.isBlank(this.formulationType)) {
            this.setValid(false);
            this.addError("'formulation type' must not be empty.");
        }

        this.validateLexiconSubstanceReference();

        if (this.formulations.isEmpty()) {
            this.setValid(false);
            this.addError("at least one formulation component is mandatory");
        }
    }

    private void validateLexiconSubstanceReference() {
        if (CollectionUtils.isEmpty(this.getLexiconSubstanceReferences())) {
            this.setValid(false);
            this.addError("'lexicon substance reference' must not be empty.");
        } else if (this.getLexiconSubstanceReferences().stream().noneMatch(StringUtils::isNotBlank)) {
            this.setValid(false);
            this.addError("at least one 'lexicon substance reference' item must not be empty.");
        }

        if (this.onlySingleLexiconSubstanceReferenceAllowed() && this.numLexiconSubstanceReferences() > 1) {
            this.setValid(false);
            this.addError("only 1 'lexicon substance reference' item is allowed with the selected 'substance type' and 'formulation type'.");
        }

        if (this.substanceTypeEnum == SubstanceType.BOTH && this.formulationTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.numLexiconSubstanceReferences() < 2) {
            this.setValid(false);
            this.addError("at least 2 'lexicon substance reference' items are necessary with the selected 'substance type' and 'formulation type'.");
        }
    }

    private boolean onlySingleLexiconSubstanceReferenceAllowed() {
        return ((this.substanceTypeEnum == SubstanceType.DRUG || this.substanceTypeEnum == SubstanceType.BOTH) && (this.formulationTypeEnum == FormulationType.SINGLE_SUBSTANCE || this.formulationTypeEnum == FormulationType.GROUPING_NAME)) || this.substanceTypeEnum == SubstanceType.SOLUTION;
    }

    private long numLexiconSubstanceReferences() {
        return Optional.ofNullable(this.getLexiconSubstanceReferences()).orElse(new ArrayList<>()).stream().filter(StringUtils::isNotBlank).count();
    }

    @Override
    public void init() {
        // nothing to do
    }

    private void generateProductId() throws GenerateIdException {
        if (this.getCurrentResource() != null && this.productMonographId == 0) {
            final int generatedId = this.randomIdService.saveAndGetId(PRODUCT_MONOGRAPH_ID_MIN_VALUE, PRODUCT_MONOGRAPH_ID_MAX_VALUE,
              PRODUCT_MONOGRAPH_ROOT_PATH, PROPERTY_NAME_PRODUCT_MONOGRAPH_ID, ResourceTypeConstants.FORMULATION, this.getCurrentResource());
            this.productMonographId = (long) generatedId;
        }
    }

    @Override
    public long getProductId() {
        return this.productMonographId;
    }

    @Override
    public SubstanceType getSubstanceType() {
        return this.substanceTypeEnum;
    }

    @Override
    public SubstanceType getLexiconSubstanceReferenceSource() {
        return this.lexiconSubstanceReferenceSourceEnum;
    }

    @Override
    public FormulationType getFormulationType() {
        return this.formulationTypeEnum;
    }

    @Override
    public List<String> getLexiconSubstanceReferencePaths() {
        return this.lexiconSubstanceReferencePaths.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<String> getAlternativeNames() {
        if (this.formulationTypeEnum == FormulationType.COMBINATION_SUBSTANCE && this.alternativeNames != null) {
            return this.alternativeNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getLexiconSubstanceReferences() {
        if (CollectionUtils.isEmpty(this.lexiconSubstanceReferences)) {
            if (this.substanceTypeEnum == SubstanceType.DRUG) {
                if (this.lexiconSubstanceDrugReferences != null) {
                    this.lexiconSubstanceReferences.addAll(this.reformatLexiconBrowserItems(this.lexiconSubstanceDrugReferences));
                }
            } else if (this.substanceTypeEnum == SubstanceType.SOLUTION) {
                if (this.lexiconSubstanceSolutionReferencePaths != null) {
                    this.lexiconSubstanceReferences.addAll(this.getSolutionNames(this.lexiconSubstanceSolutionReferencePaths));
                }
            } else if (this.substanceTypeEnum == SubstanceType.BOTH) {
                if (this.lexiconSubstanceReferenceSourceEnum == SubstanceType.DRUG) {
                    if (this.lexiconSubstanceBothDrugReferences != null) {
                        this.lexiconSubstanceReferences.addAll(this.reformatLexiconBrowserItems(this.lexiconSubstanceBothDrugReferences));
                    }
                } else {
                    if (this.lexiconSubstanceBothSolutionReferencePaths != null) {
                        this.lexiconSubstanceReferences.addAll(this.getSolutionNames(this.lexiconSubstanceBothSolutionReferencePaths));
                    }
                }
            }
        }
        return this.lexiconSubstanceReferences;
    }

    private List<String> reformatLexiconBrowserItems(@NotNull final List<String> lexiconBrowserItems) {
        final List<String> result = new ArrayList<>();
        String[] splittedItem;
        for (final String item : lexiconBrowserItems) {
            splittedItem = item.split("@/@");
            if (splittedItem.length == 2) {
                result.add(splittedItem[1]);
            } else {
                result.add(item);
            }
        }
        return result;
    }

    private List<String> getSolutionNames(@NotNull final List<String> solutionPaths) {
        final List<String> result = new ArrayList<>();
        for (final String path : solutionPaths) {
            final Resource solutionResource = this.getCurrentResource().getResourceResolver().getResource(path);
            if (solutionResource != null) {
                final ValueMap valueMap = solutionResource.getValueMap();
                result.add(valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_NAME, StringUtils.EMPTY));
            }
        }
        return result;
    }

    @Override
    public String getReconstitution() {
        return this.reconstitution;
    }

    @Override
    public List<String> getReconstitutionReferences() {
        return this.reconstitutionReferences == null ?
                 new ArrayList<>() :
                 this.reconstitutionReferences.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<String> getReconstitutionReferenceNames() {
        return this.reconstitutionReferenceNames == null ?
                 new ArrayList<>() :
                 this.reconstitutionReferenceNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<String> getSubstanceTradeNames() {
        return this.substanceTradeNames;
    }

    @Override
    public List<Formulation> getFormulations() {
        return this.formulations;
    }

}
