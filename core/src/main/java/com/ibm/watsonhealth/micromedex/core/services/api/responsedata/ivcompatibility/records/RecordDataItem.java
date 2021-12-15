package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.enums.citation.CitationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.DrugSource;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.models.citation.Citation;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Compatibility;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Concentration;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.PrimaryDrug;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.SecondaryDrug;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.vo.GlobalDataVO;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.SolutionProductLookupDataProcessor;
import com.ibm.watsonhealth.micromedex.core.servlets.GenericCemApiEndpointServlet;

@JacksonXmlRootElement(localName = "ResultRecord")
public class RecordDataItem {

    @JacksonXmlProperty(localName = "ResultId")
    private final Long recordId;

    @JacksonXmlProperty(localName = "CitationCode")
    private Long citationCode = Long.MIN_VALUE;

    @JacksonXmlProperty(localName = "CitationAuthor")
    private String citationAuthor;

    @JacksonXmlProperty(localName = "CitationTitle")
    private String citationTitle;

    @JacksonXmlProperty(localName = "CitationJournal")
    private String citationJournal;

    @JacksonXmlProperty(localName = "CitationYear")
    private String citationYear;

    @JacksonXmlProperty(localName = "CitationVolume")
    private String citationVolume;

    @JacksonXmlProperty(localName = "CitationPages")
    private String citationPages;

    @JacksonXmlProperty(localName = "ResultDescription")
    private final String resultDescription;

    @JacksonXmlProperty(localName = "ReferenceID")
    private Long referenceID = Long.MIN_VALUE;

    @JacksonXmlProperty(localName = "ProductID")
    private final Long productId;

    @JacksonXmlProperty(localName = "PrimaryAgent")
    private final String primaryAgent;

    @JacksonXmlProperty(localName = "PrimaryAgentID")
    private final Long primaryAgentId;

    @JacksonXmlProperty(localName = "PrimaryAgentVehicleID")
    private String primaryAgentVehicleId;

    @JacksonXmlProperty(localName = "PrimaryAgentTradeNames")
    private final String primaryAgentTradeNames;

    @JacksonXmlProperty(localName = "PrimaryAgentManufacturerName")
    private final String primaryAgentManufacturerName;

    @JacksonXmlProperty(localName = "PrimaryAgentConcentration")
    private final String primaryAgentConcentration = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "PrimaryAgentConcentrationUnits")
    private final String primaryAgentConcentrationUnits;

    @JacksonXmlProperty(localName = "PrimaryAgentVehicle")
    private final String primaryAgentVehicle;

    @JacksonXmlProperty(localName = "PrimaryAgentOtherNames")
    private final String primaryAgentOtherNames;

    @JacksonXmlProperty(localName = "PrimaryAgentPHRange")
    private final String primaryAgentPHRange = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "SecondaryAgent")
    private final String secondaryAgent;

    @JacksonXmlProperty(localName = "SecondaryAgentID")
    private final Long secondaryAgentId;

    @JacksonXmlProperty(localName = "SecondaryAgentVehicleID")
    private String secondaryAgentVehicleId;

    @JacksonXmlProperty(localName = "SecondaryAgentTradeNames")
    private final String secondaryAgentTradeNames;

    @JacksonXmlProperty(localName = "SecondaryAgentConcentration")
    private final String secondaryAgentConcentration = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "SecondaryAgentManufacturerName")
    private final String secondaryAgentManufacturerName;

    @JacksonXmlProperty(localName = "SecondaryAgentConcentrationUnits")
    private final String secondaryAgentConcentrationUnits;

    @JacksonXmlProperty(localName = "SecondaryAgentVehicle")
    private final String secondaryAgentVehicle;

    @JacksonXmlProperty(localName = "SecondaryAgentOtherNames")
    private String secondaryAgentOtherNames = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "SecondaryAgentPHRange")
    private final String secondaryAgentPHRange = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Container")
    private final String container;

    @JacksonXmlProperty(localName = "StorageConditions")
    private final String storageConditions;

    @JacksonXmlProperty(localName = "Methods")
    private final String methods;

    @JacksonXmlProperty(localName = "StudyPeriod")
    private final String studyPeriod;

    @JacksonXmlProperty(localName = "CompatibilityCode")
    private final String compatibilityCode;

    @JacksonXmlProperty(localName = "PhysicalCompatibility")
    private final String physicalCompatibility;

    @JacksonXmlProperty(localName = "ChemicalStability")
    private final String chemicalStability;

    @JacksonXmlProperty(localName = "ResultType")
    private final String resultType;

    @JacksonXmlProperty(localName = "Notations")
    private final String notations;

    @JacksonXmlProperty(localName = "Solution")
    private final String solution;

    @JacksonXmlProperty(localName = "SolutionID")
    private String solutionId;

    @JacksonXmlProperty(localName = "SolutionManufacturerName")
    private final String solutionManufacturerName;

    @JacksonXmlProperty(localName = "RedbookData")
    private final RedBookData redBookData;

    @JacksonXmlProperty(localName = "LastUpdate")
    private String lastUpdated;

    @JacksonXmlProperty(localName = "AemPath")
    private final String aemPath;

    public RecordDataItem(final GlobalDataVO globalData, final PrimaryDrug primaryDrug, final Concentration concentration, final Compatibility compatibility, final SecondaryDrug secondaryDrug, @NotNull final RedBookService redBookService) throws RepositoryException {
        final ResourceResolver resourceResolver = primaryDrug.getCurrentResource().getResourceResolver();

        this.setCitationData(primaryDrug, resourceResolver);

        Optional<com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData> primaryRedBookData = Optional.empty();
        Optional<com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData> primarySolutionRedBookData = Optional.empty();
        List<String> primaryDrugs = new ArrayList<>();
        SubstanceType primarySubstanceType = null;
        if (primaryDrug.getPrimaryDrugSource() == DrugSource.EXISTING_PRODUCT_MONOGRAPH && (primaryDrug.getLinkedProductMonograph() != null)) {
            primaryDrugs = primaryDrug.getLinkedProductMonograph().getSubstance().getLexiconSubstanceReferences();
            Collections.sort(primaryDrugs, String.CASE_INSENSITIVE_ORDER);
            this.primaryAgent = String.join("/", primaryDrugs);
            this.primaryAgentId = primaryDrug.getLinkedProductMonograph().getSubstance().getProductId();
            primaryRedBookData = redBookService.getRedBookData(primaryDrug.getLinkedProductMonograph().getSubstance());
            primarySubstanceType = primaryDrug.getLinkedProductMonograph().getSubstance().getSubstanceType();
        } else if (primaryDrug.getPrimaryDrugSource() == DrugSource.NEW) {
            final List<String> drugs = primaryDrug.getPrimaryDrugs();
            Collections.sort(drugs, String.CASE_INSENSITIVE_ORDER);
            this.primaryAgent = String.join("/", drugs);
            this.primaryAgentId = null;
            primaryDrugs = primaryDrug.getPrimaryDrugs();
            primaryRedBookData = redBookService.getRedBookDataByGcrName(primaryDrug.getPrimaryDrugs(), resourceResolver);
            primarySubstanceType = SubstanceType.DRUG;
        } else {
            this.primaryAgent = StringUtils.EMPTY;
            this.primaryAgentId = null;
        }
        this.productId = this.primaryAgentId;
        this.primaryAgentOtherNames = String.join("; ", this.getPrimaryAgentOtherNames(primaryDrug));
        this.primaryAgentTradeNames = primaryDrug.getTradeName();
        this.primaryAgentManufacturerName = primaryDrug.getManufacturer();
        this.primaryAgentConcentrationUnits = concentration.getConcentration();
        if (primaryDrug.isSolutionEnabled()) {
            this.primaryAgentVehicle = primaryDrug.getSolutionName();
            primarySolutionRedBookData = redBookService.getRedBookDataBySolutionPath(
              primaryDrug.getSolutionPath(), primaryDrug.getCurrentResource().getResourceResolver());
        } else {
            this.primaryAgentVehicle = StringUtils.EMPTY;
        }

        Optional<com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData> secondaryRedBookData = Optional.empty();
        Optional<com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData> secondarySolutionRedBookData = Optional.empty();
        List<String> secondaryDrugs = new ArrayList<>();
        SubstanceType secondarySubstanceType = null;
        if (secondaryDrug == null) {
            this.recordId = compatibility.getRecordId();
            this.secondaryAgent = StringUtils.EMPTY;
            this.secondaryAgentId = null;
            this.secondaryAgentTradeNames = StringUtils.EMPTY;
            this.secondaryAgentManufacturerName = StringUtils.EMPTY;
            this.secondaryAgentConcentrationUnits = StringUtils.EMPTY;
            this.secondaryAgentVehicle = StringUtils.EMPTY;

            this.container = primaryDrug.getContainer();
            this.storageConditions = primaryDrug.getStorage();
            this.methods = primaryDrug.getMethod();
            this.studyPeriod = primaryDrug.getStudyPeriod();
        } else {
            this.recordId = secondaryDrug.getRecordId();
            if (secondaryDrug.getSecondaryDrugSource() == DrugSource.EXISTING_PRODUCT_MONOGRAPH && (secondaryDrug.getLinkedProductMonograph() != null)) {
                secondaryDrugs = secondaryDrug.getLinkedProductMonograph().getSubstance().getLexiconSubstanceReferences();
                Collections.sort(secondaryDrugs, String.CASE_INSENSITIVE_ORDER);
                this.secondaryAgent = String.join("/", secondaryDrugs);
                this.secondaryAgentId = secondaryDrug.getLinkedProductMonograph().getSubstance().getProductId();
                secondaryRedBookData = redBookService.getRedBookData(secondaryDrug.getLinkedProductMonograph().getSubstance());
                secondarySubstanceType = secondaryDrug.getLinkedProductMonograph().getSubstance().getSubstanceType();
            } else if (secondaryDrug.getSecondaryDrugSource() == DrugSource.NEW) {
                final List<String> drugs = secondaryDrug.getSecondaryDrugs();
                Collections.sort(drugs, String.CASE_INSENSITIVE_ORDER);
                this.secondaryAgent = String.join("/", drugs);
                this.secondaryAgentId = null;
                secondaryDrugs = secondaryDrug.getSecondaryDrugs();
                secondaryRedBookData = redBookService.getRedBookDataByGcrName(secondaryDrug.getSecondaryDrugs(), resourceResolver);
                secondarySubstanceType = SubstanceType.DRUG;
            } else {
                this.secondaryAgent = StringUtils.EMPTY;
                this.secondaryAgentId = null;
            }
            this.secondaryAgentOtherNames = String.join("; ", this.getSecondaryAgentOtherNames(secondaryDrug));
            this.secondaryAgentTradeNames = secondaryDrug.getTradeName();
            this.secondaryAgentManufacturerName = secondaryDrug.getManufacturer();
            this.secondaryAgentConcentrationUnits = secondaryDrug.getSecondaryDrugConcentration();
            if (secondaryDrug.isSolutionEnabled()) {
                this.secondaryAgentVehicle = secondaryDrug.getSolutionName();
                secondarySolutionRedBookData = redBookService.getRedBookDataBySolutionPath(
                  secondaryDrug.getSolutionPath(), primaryDrug.getCurrentResource().getResourceResolver());
            } else {
                this.secondaryAgentVehicle = StringUtils.EMPTY;
            }

            this.container = StringUtils.isBlank(secondaryDrug.getContainer()) ? primaryDrug.getContainer() : secondaryDrug.getContainer();
            this.storageConditions = StringUtils.isBlank(secondaryDrug.getStorage()) ? primaryDrug.getStorage() : secondaryDrug.getStorage();
            this.methods = StringUtils.isBlank(secondaryDrug.getMethod()) ? primaryDrug.getMethod() : secondaryDrug.getMethod();
            this.studyPeriod = StringUtils.isBlank(secondaryDrug.getStudyPeriod()) ? primaryDrug.getStudyPeriod() : secondaryDrug.getStudyPeriod();
        }

        if (compatibility.getCompatibleEnum() != null) {
            this.compatibilityCode = compatibility.getCompatibleEnum().getAemValue();
        } else {
            this.compatibilityCode = StringUtils.EMPTY;
        }
        this.physicalCompatibility = compatibility.getPhysicalCompatibility();
        this.chemicalStability = compatibility.getChemicalStability();
        this.resultType = compatibility.getCombinationTypeEnum().getAemValue();
        this.notations = compatibility.getRecordNotes();

        Optional<com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData> solutionRedBookData = Optional.empty();
        if ((compatibility.getCombinationTypeEnum() == CombinationType.ADMIXTURE || compatibility.getCombinationTypeEnum() == CombinationType.DRUG_SOLUTION) && compatibility.isSolutionEnabled()) {
            this.solution = compatibility.getSolutionName();
            this.solutionManufacturerName = compatibility.getSolutionManufacturer();
            solutionRedBookData = redBookService.getRedBookDataBySolutionPath(compatibility.getSolutionPath(), resourceResolver);
        } else {
            this.solution = StringUtils.EMPTY;
            this.solutionManufacturerName = StringUtils.EMPTY;
        }

        this.resultDescription = this.getResultDescription(secondaryDrug != null);
        this.setSolutionData(primaryDrug, compatibility, secondaryDrug, resourceResolver);

        this.redBookData = new RedBookData();
        this.redBookData.addPrimaryDrugRedBookData(primaryRedBookData.orElse(null), primaryDrugs, primarySubstanceType);
        this.redBookData.addSecondaryDrugRedBookData(secondaryRedBookData.orElse(null), secondaryDrugs, secondarySubstanceType);
        this.redBookData.addPrimarySolutionRedBookData(primarySolutionRedBookData.orElse(null), primaryDrug.getSolutionName());
        if (secondaryDrug != null) {
            this.redBookData.addSecondarySolutionRedBookData(secondarySolutionRedBookData.orElse(null), secondaryDrug.getSolutionName());
        }
        this.redBookData.addSolutionRedBookData(solutionRedBookData.orElse(null), compatibility.getSolutionName());

        this.setLastUpdated(globalData);
        if (secondaryDrug == null) {
            this.aemPath = compatibility.getCurrentResource().getPath();
        } else {
            this.aemPath = secondaryDrug.getCurrentResource().getPath();
        }
    }

    private void setCitationData(final PrimaryDrug primaryDrug, final ResourceResolver resourceResolver) {
        final Optional<Citation> citationModel = this.getCitationModel(primaryDrug, resourceResolver);
        if (citationModel.isPresent()) {
            final Citation citation = citationModel.get();
            this.citationCode = citation.getCitationId();
            this.referenceID = this.citationCode;
            if (citation.getCitationType() == CitationType.PRODUCT_INFO) {
                this.citationAuthor = StringUtils.EMPTY;
                this.citationTitle = StringUtils.EMPTY;
                this.citationJournal = StringUtils.EMPTY;
                this.citationYear = citation.getPublicationDate();
                this.citationVolume = StringUtils.EMPTY;
                this.citationPages = StringUtils.EMPTY;
            } else if (citation.getCitationType() == CitationType.GENERIC) {
                this.citationAuthor = citation.getAuthor();
                this.citationTitle = citation.getTitle();
                this.citationJournal = StringUtils.EMPTY;
                this.citationYear = citation.getPublicationDate();
                this.citationVolume = StringUtils.EMPTY;
                this.citationPages = StringUtils.EMPTY;
            } else if (citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
                this.citationAuthor = citation.getFirstAuthor();
                if (StringUtils.isNotBlank(citation.getSecondAuthor())) {
                    this.citationAuthor = this.citationAuthor + ", " + citation.getSecondAuthor();
                }
                if (StringUtils.isNotBlank(citation.getThirdAuthor())) {
                    this.citationAuthor = this.citationAuthor + ", " + citation.getThirdAuthor();
                }
                if (citation.isEtalEnabled()) {
                    this.citationAuthor = this.citationAuthor + ", Et al";
                }
                this.citationTitle = citation.getTitle();
                this.citationJournal = citation.getJournalAbbreviation();
                this.citationYear = citation.getPublicationDate();
                this.citationVolume = citation.getVolumeIssue();
                this.citationPages = citation.getPages();
            }
        }
    }

    private Optional<Citation> getCitationModel(final PrimaryDrug primaryDrug, final ResourceResolver resourceResolver) {
        if (StringUtils.isNotBlank(primaryDrug.getCitationPath())) {
            final String citationPath = StringUtils.join(primaryDrug.getCitationPath(), "/jcr:content/root/container");
            final List<Resource> childResources = new ArrayList<>();
            resourceResolver.getResource(citationPath).listChildren().forEachRemaining(childResources::add);
            final Optional<Resource> firstCitationResource = childResources
              .stream()
              .filter(resource -> resource.getResourceType().equals(ResourceTypeConstants.CITATION))
              .findFirst();
            return firstCitationResource.map(resource -> resource.adaptTo(Citation.class));
        }
        return Optional.empty();
    }

    private void setSolutionData(final PrimaryDrug primaryDrug, final Compatibility compatibility, final SecondaryDrug secondaryDrug, final ResourceResolver resourceResolver) {
        if (primaryDrug.isSolutionEnabled()) {
            this.primaryAgentVehicleId = this.getSolutionId(primaryDrug.getSolutionPath(), resourceResolver);
        }
        if (compatibility.isSolutionEnabled()) {
            this.solutionId = this.getSolutionId(compatibility.getSolutionPath(), resourceResolver);
        }
        if (secondaryDrug != null && secondaryDrug.isSolutionEnabled()) {
            this.secondaryAgentVehicleId = this.getSolutionId(secondaryDrug.getSolutionPath(), resourceResolver);
        }
    }

    private String getSolutionId(final String solutionPath, final ResourceResolver resourceResolver) {
        final Resource solutionResource = resourceResolver.getResource(solutionPath);
        if (solutionResource != null) {
            final ValueMap valueMap = solutionResource.getValueMap();
            final Long id = valueMap.get(SolutionProductLookupDataProcessor.PROPERTY_PRODUCT_ID, Long.class);
            if (id != null) {
                return id.toString();
            }
        }
        return StringUtils.EMPTY;
    }

    private void setLastUpdated(final GlobalDataVO globalData) {
        if (globalData.getLastModified() == null) {
            this.lastUpdated = StringUtils.EMPTY;
        } else {
            this.lastUpdated = GenericCemApiEndpointServlet.API_ISO_DATE_TIME_FORMATTER.format(globalData.getLastModified());
        }
    }

    private String getResultDescription(final boolean hasSecondaryDrug) {
        String result = this.primaryAgent + this.primaryAgentConcentrationUnits;
        if (hasSecondaryDrug) {
            result = result + "-" + this.secondaryAgent + this.secondaryAgentConcentrationUnits;
        } else {
            result = result + ":" + this.solution;
        }
        return result;
    }

    private List<String> getPrimaryAgentOtherNames(final PrimaryDrug primaryDrug) {
        if (primaryDrug.isMonographPrimaryDrugSource()) {
            return primaryDrug.getLinkedProductMonograph().getSubstance().getAlternativeNames();
        } else {
            return primaryDrug.getAlternativeNames();
        }
    }

    private List<String> getSecondaryAgentOtherNames(final SecondaryDrug secondaryDrug) {
        if (secondaryDrug.isMonographSecondaryDrugSource()) {
            return secondaryDrug.getLinkedProductMonograph().getSubstance().getAlternativeNames();
        } else {
            return secondaryDrug.getAlternativeNames();
        }
    }

}
