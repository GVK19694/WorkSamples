package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@ConsumerType
public interface Substance extends ValidationModel {

    long getProductId();

    SubstanceType getSubstanceType();

    SubstanceType getLexiconSubstanceReferenceSource();

    FormulationType getFormulationType();

    List<String> getLexiconSubstanceReferences();

    List<String> getLexiconSubstanceReferencePaths();

    List<String> getAlternativeNames();

    List<String> getSubstanceTradeNames();

    List<Formulation> getFormulations();

    String getReconstitution();

    List<String> getReconstitutionReferences();

    List<String> getReconstitutionReferenceNames();

}
