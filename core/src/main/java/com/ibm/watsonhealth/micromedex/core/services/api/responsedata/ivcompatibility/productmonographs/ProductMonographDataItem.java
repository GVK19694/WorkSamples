package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Formulation;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.vo.GlobalDataVO;
import com.ibm.watsonhealth.micromedex.core.servlets.GenericCemApiEndpointServlet;

@JacksonXmlRootElement(localName = "ProductData")
public class ProductMonographDataItem {

    @JacksonXmlProperty(localName = "Type")
    private final String type;

    @JacksonXmlProperty(localName = "Description")
    private final String description;

    @JacksonXmlProperty(localName = "OtherNames")
    private final String otherNames;

    @JacksonXmlProperty(localName = "TradeNames")
    private final String tradenames;

    @JacksonXmlProperty(localName = "Reconstitution")
    private final String reconstitution;

    @JacksonXmlProperty(localName = "Display")
    private final String display;

    @JacksonXmlProperty(localName = "Formulation")
    private final String formulation;

    @JacksonXmlProperty(localName = "Osmolality")
    private final String osmolality;

    @JacksonXmlProperty(localName = "pHmin")
    private final String phMin;

    @JacksonXmlProperty(localName = "pHeffects")
    private final String phEffects;

    @JacksonXmlProperty(localName = "pHmax")
    private final String phMax;

    @JacksonXmlProperty(localName = "pHrange")
    private final String phRange;

    @JacksonXmlProperty(localName = "pHmean")
    private final String phMean;

    @JacksonXmlProperty(localName = "Storage")
    private final String storage;

    @JacksonXmlProperty(localName = "Freezing")
    private final String freezing;

    @JacksonXmlProperty(localName = "LightEffects")
    private final String lightEffects;

    @JacksonXmlProperty(localName = "Stability")
    private final String stability;

    @JacksonXmlProperty(localName = "StabilityMax")
    private final String stabilityMax;

    @JacksonXmlProperty(localName = "Sorption")
    private final String sorption;

    @JacksonXmlProperty(localName = "Filtration")
    private final String filtration;

    @JacksonXmlProperty(localName = "SodiumContent")
    private final String sodiumContent;

    @JacksonXmlProperty(localName = "Other")
    private final String other;

    @JacksonXmlProperty(localName = "ProductID")
    private final long productId;

    @JacksonXmlProperty(localName = "MultiFormulation")
    private final boolean multiFormulation;

    @JacksonXmlProperty(localName = "LastUpdate")
    private final String lastUpdate;

    @JacksonXmlProperty(localName = "LastUserID")
    private final String lastUserId;

    @JacksonXmlProperty(localName = "RedbookData")
    private final RedBookData redBookData;

    @JacksonXmlProperty(localName = "References")
    private final References references = new References();

    @JacksonXmlProperty(localName = "AemPath")
    private final String aemPath;

    public ProductMonographDataItem(@NotNull final GlobalDataVO globalData, @NotNull final Substance substance, @NotNull final Formulation formulation, @NotNull final RedBookService redBookService) throws RepositoryException {
        this.type = substance.getSubstanceType().getAemValue();
        this.description = String.join(";", substance.getLexiconSubstanceReferences());
        this.otherNames = String.join("; ", substance.getAlternativeNames());
        final List<String> allTradeNames = new ArrayList<>();
        if (substance.getSubstanceTradeNames() != null) {
            allTradeNames.addAll(substance.getSubstanceTradeNames());
        }
        allTradeNames.add(formulation.getFormulationTradenames());
        this.tradenames = String.join(";", allTradeNames);
        this.reconstitution = substance.getReconstitution();
        this.display = formulation.isDisplayFormulation() ? "1" : "0";
        this.formulation = formulation.getFormulationDescription();

        if (formulation.isAddPhDetails()) {
            this.osmolality = formulation.getOsmolality();
            this.phMin = formulation.getPhMin();
            this.phEffects = formulation.getPhEffects();
            this.phMax = formulation.getPhMax();
            this.phRange = formulation.getPhRange();
            this.phMean = formulation.getPhMean();
        } else {
            this.osmolality = StringUtils.EMPTY;
            this.phMin = StringUtils.EMPTY;
            this.phEffects = StringUtils.EMPTY;
            this.phMax = StringUtils.EMPTY;
            this.phRange = StringUtils.EMPTY;
            this.phMean = StringUtils.EMPTY;
        }

        if (formulation.isAddStorageDetails()) {
            this.storage = formulation.getStorage();
            this.freezing = formulation.getFreezing();
            this.lightEffects = formulation.getLightEffects();
        } else {
            this.storage = StringUtils.EMPTY;
            this.freezing = StringUtils.EMPTY;
            this.lightEffects = StringUtils.EMPTY;
        }
        if (formulation.isAddStabilityDetails()) {
            this.stability = formulation.getStability();
            this.stabilityMax = formulation.getStabilityMax();
            this.sorption = formulation.getSorption();
        } else {
            this.stability = StringUtils.EMPTY;
            this.stabilityMax = StringUtils.EMPTY;
            this.sorption = StringUtils.EMPTY;
        }

        if (formulation.isAddAdditionalInformation()) {
            this.filtration = formulation.getFiltration();
            this.sodiumContent = formulation.getSodiumContent();
            this.other = formulation.getOtherInformation();
        } else {
            this.filtration = StringUtils.EMPTY;
            this.sodiumContent = StringUtils.EMPTY;
            this.other = StringUtils.EMPTY;
        }

        this.productId = substance.getProductId();
        this.multiFormulation = substance.getFormulations() != null && substance.getFormulations().size() > 1;

        if (globalData.getLastModified() == null) {
            this.lastUpdate = StringUtils.EMPTY;
        } else {
            this.lastUpdate = GenericCemApiEndpointServlet.API_ISO_DATE_TIME_FORMATTER.format(globalData.getLastModified());
        }
        if (StringUtils.isBlank(globalData.getLastModifiedBy())) {
            this.lastUserId = StringUtils.EMPTY;
        } else {
            this.lastUserId = globalData.getLastModifiedBy();
        }

        this.redBookData = redBookService.getRedBookData(substance).orElse(null);
        this.addReferences(substance, formulation);

        this.aemPath = formulation.getCurrentResource().getPath();
    }

    private void addReferences(@NotNull final Substance substance, @NotNull final Formulation formulation) {
        final ResourceResolver resourceResolver = formulation.getCurrentResource().getResourceResolver();

        this.addReference(substance.getReconstitutionReferences(), "Reconstitution", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getOsmolalityReferences(), "Osmolality", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getPhMinReferences(), "pH minimum", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getPhEffectsReferences(), "pH effects", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getPhMaxReferences(), "pH maximum", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getPhRangeReferences(), "pH Range", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getPhMeanReferences(), "pH mean", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getStorageReferences(), "Storage", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getFreezingReferences(), "Freezing", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getStabilityReferences(), "Stability", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getStabilityMaxReferences(), "Stability Max", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getLightEffectsReferences(), "Light Effects", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getSorptionReferences(), "Sorption", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getFiltrationReferences(), "Filtration", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getSodiumContentReferences(), "Sodium Content", substance.getProductId(), resourceResolver);
        this.addReference(formulation.getOtherInformationReferences(), "Other", substance.getProductId(), resourceResolver);
    }

    private void addReference(final List<String> references, @NotNull final String fieldName, final long productId, @NotNull final ResourceResolver resourceResolver) {
        if (references != null) {
            for (final String reference : references) {
                this.references.addReference(reference, fieldName, productId, resourceResolver);
            }
        }
    }

}
