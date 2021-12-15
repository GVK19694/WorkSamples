package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@ConsumerType
public interface Formulation extends ValidationModel {

    boolean isDisplayFormulation();

    String getFormulationDescription();

    String getFormulationTradenames();

    boolean isAddPhDetails();

    String getOsmolality();

    List<String> getOsmolalityReferences();

    String getPhMin();

    List<String> getPhMinReferences();

    String getPhEffects();

    List<String> getPhEffectsReferences();

    String getPhMax();

    List<String> getPhMaxReferences();

    String getPhRange();

    List<String> getPhRangeReferences();

    String getPhMean();

    List<String> getPhMeanReferences();

    boolean isAddStorageDetails();

    String getStorage();

    List<String> getStorageReferences();

    String getFreezing();

    List<String> getFreezingReferences();

    String getLightEffects();

    List<String> getLightEffectsReferences();

    boolean isAddStabilityDetails();

    String getStability();

    List<String> getStabilityReferences();

    String getStabilityMax();

    List<String> getStabilityMaxReferences();

    String getSorption();

    List<String> getSorptionReferences();

    boolean isAddAdditionalInformation();

    String getFiltration();

    List<String> getFiltrationReferences();

    String getSodiumContent();

    List<String> getSodiumContentReferences();

    String getOtherInformation();

    List<String> getOtherInformationReferences();

    List<String> getOsmolalityReferenceNames();

    List<String> getPhMinReferenceNames();

    List<String> getPhEffectsReferenceNames();

    List<String> getPhMaxReferenceNames();

    List<String> getPhRangeReferenceNames();

    List<String> getPhMeanReferenceNames();

    List<String> getStorageReferenceNames();

    List<String> getFreezingReferenceNames();

    List<String> getLightEffectsReferenceNames();

    List<String> getStabilityReferenceNames();

    List<String> getStabilityMaxReferenceNames();

    List<String> getSorptionReferenceNames();

    List<String> getFiltrationReferenceNames();

    List<String> getSodiumContentReferenceNames();

    List<String> getOtherInformationReferenceNames();

}
